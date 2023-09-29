package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.WritingCard
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.database.WritingCardDao
import com.estholon.idiomapp.database.Record_CategoriesDao
import kotlinx.coroutines.launch

class MatchViewModel(private val cardDao: WritingCardDao , private val recordCategoriesdao: Record_CategoriesDao):ViewModel() {

    //List category
    private val _listCard = MutableLiveData<MutableList<WritingCard>>()
    val listCard: LiveData<MutableList<WritingCard>> get() = _listCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory

    private val _cardSelected = MutableLiveData<MutableList<WritingCard>>()
    val cardSelected: LiveData<MutableList<WritingCard>> get() = _cardSelected

    private val _matchRecord = MutableLiveData<MutableList<Records>>()
    val matchRecord: LiveData<MutableList<Records>> get() = _matchRecord







    //Obtain info
    fun getAllCardsBD(idIdiomI: String , idIdiomD: String , category: MutableList<Category>) {
        viewModelScope.launch {
            _listCard.value = cardDao.fetchCard(idIdiomI , idIdiomD)
            if (listCard.value!!.isNotEmpty()) {
                if (category.isNotEmpty()) {
                    getAllCategoryRelationsBD(category)
                } else {
                    getWithOutCategories()
                }
            }
        }
    }

    fun getAllCategoryRelationsBD(category: MutableList<Category>) {
        viewModelScope.launch {
            _listRecordCategory.value = mutableListOf()
            _listRecordCategory.value = recordCategoriesdao.fetchRecordCategory()
            listRecordCategory.value?.let { getRecordCategoryFiltered(category , it) }
        }
    }

    fun getRecordCategoryFiltered(
        categoriesSelected: MutableList<Category> ,
        recordCategories: MutableList<Record_Categories>
    ) {
        viewModelScope.launch {
            val recordCategoryFiltered = mutableListOf<Record_Categories>()
            for (category in categoriesSelected) {
                for (recordCategory in recordCategories) {
                    if (category.id == recordCategory.idCategories) {
                        recordCategoryFiltered.add(recordCategory)
                    }
                }
            }
            listRecordCategory.value?.clear()
            listRecordCategory.value?.addAll(recordCategoryFiltered)
            listCard.value?.let {
                listRecordCategory.value?.let { it1 ->
                    getCardFilteredRandom(
                        it ,
                        it1
                    )
                }
            }
        }

    }

    fun getCardFilteredRandom(
        card: MutableList<WritingCard> ,
        recorCategory: MutableList<Record_Categories>
    ) {
        viewModelScope.launch {
            val listCardFilter = mutableListOf<WritingCard>()
            for (list in card) {
                for (element in recorCategory) {
                    if (list.id == element.idRecord) {
                        listCardFilter.add(list)
                    }
                }
            }
            _listCard.value?.clear()
            _listCard.value = listCardFilter
            if (listCard.value!!.isNotEmpty()) {
                listCard.value?.shuffle()
                if (listCard.value!!.size>=4){
                    val cardSelectedList = mutableListOf<WritingCard>()
                    for (i in 0 until 4){
                        listCard.value?.let { cardSelectedList.add(it.get(i)) }
                    }
                    _cardSelected.value = cardSelectedList
                }


            }
            listGetMatch()
        }
    }


    fun getWithOutCategories() {
        if (listCard.value!!.isNotEmpty()) {
            listCard.value?.shuffle()
            if (listCard.value!!.size>=4){
                val cardSelectedList = mutableListOf<WritingCard>()
                for (i in 0 until 4){
                    listCard.value?.let { cardSelectedList.add(it.get(i)) }
                }
                _cardSelected.value = cardSelectedList
            }


        }
        listGetMatch()
    }

    fun listGetMatch(){
         val listMatch= mutableListOf<Records>()
        for (matchRandom in cardSelected.value!!){
            listMatch.add(Records(matchRandom.id,matchRandom.sentence,"no",matchRandom.idiomSentence))
            listMatch.add(Records(matchRandom.id,matchRandom.translation,"no",matchRandom.idiomTranslation))
        }
        listMatch.shuffle()
        _matchRecord.value =listMatch
    }
}
class MatchViewModelFactory(
    private val cardDao: WritingCardDao ,
    private val recordCategoriesdao: Record_CategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchViewModel(cardDao,recordCategoriesdao ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}