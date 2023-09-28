package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.WritingCard
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.database.WritingCardDao
import com.estholon.idiomapp.database.Record_CategoriesDao
import kotlinx.coroutines.launch

class CardViewModel(private val writingCardDao: WritingCardDao, private val recordCategoriesdao: Record_CategoriesDao):ViewModel() {

    //List category
    private val _listWritingCard = MutableLiveData<MutableList<WritingCard>>()
    val listWritingCard: LiveData<MutableList<WritingCard>> get() = _listWritingCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory
    var iterador=0

    private val _Writing_cardSelected = MutableLiveData<MutableList<WritingCard>>()
    val writingCardSelected: LiveData<MutableList<WritingCard>> get() = _Writing_cardSelected





    //Obtain info
    fun getAllCardsBD(idIdiomI:String , idIdiomD:String , category: MutableList<Category>){
        viewModelScope.launch {
            _listWritingCard.value=writingCardDao.fetchCard(idIdiomI,idIdiomD)
            if(listWritingCard.value!!.isNotEmpty()) {
                if (category.isNotEmpty()) {
                    getAllCategoryRelationsBD(category)
                } else {
                    getWithOutCategories()
                }
            }
        }
    }

    fun getAllCategoryRelationsBD(category: MutableList<Category>){
        viewModelScope.launch {
            _listRecordCategory.value= mutableListOf()
            _listRecordCategory.value=recordCategoriesdao.fetchRecordCategory()
            listRecordCategory.value?.let { getRecordCategoryFiltered(category, it) }
        }
    }

    fun getRecordCategoryFiltered(categoriesSelected: MutableList<Category> , recordCategories:MutableList<Record_Categories>){
        viewModelScope.launch {
            val recordCategoryFiltered= mutableListOf<Record_Categories>()
            for (category in categoriesSelected){
                for (recordCategory in recordCategories){
                    if (category.id==recordCategory.idCategories){
                        recordCategoryFiltered.add(recordCategory)
                    }
                }
            }
            listRecordCategory.value?.clear()
            listRecordCategory.value?.addAll(recordCategoryFiltered)
            listWritingCard.value?.let { listRecordCategory.value?.let { it1 -> getCardFilteredRandom(it , it1) } }
        }

    }

    fun getCardFilteredRandom(writingCard:MutableList<WritingCard>, recorCategory:MutableList<Record_Categories>){
        viewModelScope.launch {
           val listWritingCardFilter= mutableListOf<WritingCard>()
           for (list in writingCard){
               for (element in recorCategory){
                   if(list.id==element.idRecord){
                       listWritingCardFilter.add(list)
                   }
               }
           }
            _listWritingCard.value?.clear()
            _listWritingCard.value=listWritingCardFilter
            if (listWritingCard.value!!.isNotEmpty()) {
                listWritingCard.value?.shuffle()
                val writingCardSelectedList = mutableListOf<WritingCard>()
                listWritingCard.value?.let { writingCardSelectedList.add(it.get(0)) }
                _Writing_cardSelected.value = writingCardSelectedList
            }
        }
    }
    fun getWithOutCategories(){
        listWritingCard.value?.shuffle()
        val writingCardSelectedList= mutableListOf<WritingCard>()
        listWritingCard.value?.let { writingCardSelectedList.add(it.get(0)) }
        _Writing_cardSelected.value=writingCardSelectedList
    }
    fun nextCard(){
        iterador++
        if (iterador< listWritingCard.value!!.size){
            _Writing_cardSelected.value?.clear()
            val nextWritingCardList= mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { nextWritingCardList.add(it) }
            _Writing_cardSelected.value=nextWritingCardList
        }else{
            iterador=0
            listWritingCard.value?.shuffle()
            _Writing_cardSelected.value?.clear()
            val writingCardSelectedList= mutableListOf<WritingCard>()
            listWritingCard.value?.let { writingCardSelectedList.add(it.get(iterador)) }
            _Writing_cardSelected.value=writingCardSelectedList
        }
    }

    fun lastCard(){
        iterador--
        if (iterador>= 0){
            _Writing_cardSelected.value?.clear()
            val lastWritingCardList= mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { lastWritingCardList.add(it) }
            _Writing_cardSelected.value=lastWritingCardList
        }else{
            iterador= listWritingCard.value!!.size - 1
            listWritingCard.value?.shuffle()
            _Writing_cardSelected.value?.clear()
            val writingCardSelectedList= mutableListOf<WritingCard>()
            listWritingCard.value?.let { writingCardSelectedList.add(it.get(iterador)) }
            _Writing_cardSelected.value=writingCardSelectedList
        }
    }
    }








class CardViewModelFactory(
    private val writingCardDao: WritingCardDao,
    private val recordCategoriesdao: Record_CategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(writingCardDao,recordCategoriesdao ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}