package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Card
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.database.CardDao
import com.estholon.idiomapp.database.Record_CategoriesDao
import kotlinx.coroutines.launch

class CardViewModel(private val cardDao: CardDao,private val recordCategoriesdao: Record_CategoriesDao):ViewModel() {

    //List category
    private val _listCard = MutableLiveData<MutableList<Card>>()
    val listCard: LiveData<MutableList<Card>> get() = _listCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory
    var iterador=0

    private val _cardSelected = MutableLiveData<MutableList<Card>>()
    val cardSelected: LiveData<MutableList<Card>> get() = _cardSelected





    //Obtain info
    fun getAllCardsBD(idIdiomI:String , idIdiomD:String , category: MutableList<Category>){
        viewModelScope.launch {
            _listCard.value=cardDao.fetchCard(idIdiomI,idIdiomD)
            if(listCard.value!!.isNotEmpty()) {
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
            listCard.value?.let { listRecordCategory.value?.let { it1 -> getCardFilteredRandom(it , it1) } }
        }

    }

    fun getCardFilteredRandom(card:MutableList<Card> , recorCategory:MutableList<Record_Categories>){
        viewModelScope.launch {
           val listCardFilter= mutableListOf<Card>()
           for (list in card){
               for (element in recorCategory){
                   if(list.id==element.idRecord){
                       listCardFilter.add(list)
                   }
               }
           }
            _listCard.value?.clear()
            _listCard.value=listCardFilter
            if (listCard.value!!.isNotEmpty()) {
                listCard.value?.shuffle()
                val cardSelectedList = mutableListOf<Card>()
                listCard.value?.let { cardSelectedList.add(it.get(0)) }
                _cardSelected.value = cardSelectedList
            }
        }
    }
    fun getWithOutCategories(){
        listCard.value?.shuffle()
        val cardSelectedList= mutableListOf<Card>()
        listCard.value?.let { cardSelectedList.add(it.get(0)) }
        _cardSelected.value=cardSelectedList
    }
    fun nextCard(){
        iterador++
        if (iterador< listCard.value!!.size){
            _cardSelected.value?.clear()
            val nextCardList= mutableListOf<Card>()
            listCard.value?.get(iterador)?.let { nextCardList.add(it) }
            _cardSelected.value=nextCardList
        }else{
            iterador=0
            listCard.value?.shuffle()
            _cardSelected.value?.clear()
            val cardSelectedList= mutableListOf<Card>()
            listCard.value?.let { cardSelectedList.add(it.get(iterador)) }
            _cardSelected.value=cardSelectedList
        }
    }

    fun lastCard(){
        iterador--
        if (iterador>= 0){
            _cardSelected.value?.clear()
            val lastCardList= mutableListOf<Card>()
            listCard.value?.get(iterador)?.let { lastCardList.add(it) }
            _cardSelected.value=lastCardList
        }else{
            iterador= listCard.value!!.size - 1
            listCard.value?.shuffle()
            _cardSelected.value?.clear()
            val cardSelectedList= mutableListOf<Card>()
            listCard.value?.let { cardSelectedList.add(it.get(iterador)) }
            _cardSelected.value=cardSelectedList
        }
    }
    }








class CardViewModelFactory(
    private val cardDao: CardDao ,
    private val recordCategoriesdao: Record_CategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(cardDao,recordCategoriesdao ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}