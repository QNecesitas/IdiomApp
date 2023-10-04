package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.data.WritingCard
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.database.WritingCardDao
import com.estholon.idiomapp.database.RecordCategoriesDao
import kotlinx.coroutines.launch

class WritingViewModel(
    private val writingCardDao: WritingCardDao,
    private val recordCategoriesdao: RecordCategoriesDao
) : ViewModel() {

    //List category
    private val _listWritingCard = MutableLiveData<MutableList<WritingCard>>()
    val listWritingCard: LiveData<MutableList<WritingCard>> get() = _listWritingCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    private val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory
    var iterador = 0

    private val _writingCardSelected = MutableLiveData<MutableList<WritingCard>>()
    val writingCardSelected: LiveData<MutableList<WritingCard>> get() = _writingCardSelected

    //Cunt errors and rights
    var errors = 0;
    var rights = 0;

    //Obtain info
    fun getAllCardsBD(idIdiomI: String, idIdiomD: String, category: MutableList<Category>) {
        viewModelScope.launch {
            _listWritingCard.value = writingCardDao.fetchCard(idIdiomI, idIdiomD)
            repairInvertedInfoListWritingCard()
            if (listWritingCard.value!!.isNotEmpty()) {
                if (category.isNotEmpty()) {
                    getAllCategoryRelationsBD(category)
                } else {
                    getWithOutCategories()
                }
            }
        }
    }

    private fun repairInvertedInfoListWritingCard(){
        val newList = mutableListOf<WritingCard>()
        _listWritingCard.value?.let { newList.addAll(it) }
        for(element in newList){
            if(element.idiomSentence != InformationIntent.itemIdiomLeft.id){
                //Idiom
                val auxIdiom = element.idiomSentence
                element.idiomSentence = element.idiomTranslation
                element.idiomTranslation = auxIdiom
                //Text
                val auxText = element.sentence
                element.sentence = element.translation
                element.translation = auxText
            }
        }
    }

    private fun getAllCategoryRelationsBD(category: MutableList<Category>) {
        viewModelScope.launch {
            _listRecordCategory.value = mutableListOf()
            _listRecordCategory.value = recordCategoriesdao.fetchRecordCategory()
            listRecordCategory.value?.let { getRecordCategoryFiltered(category, it) }
        }
    }

    private fun getRecordCategoryFiltered(
        categoriesSelected: MutableList<Category>,
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
            listWritingCard.value?.let {
                listRecordCategory.value?.let { it1 ->
                    getCardFilteredRandom(
                        it,
                        it1
                    )
                }
            }
        }

    }

    private fun getCardFilteredRandom(
        writingCard: MutableList<WritingCard>,
        recordCategory: MutableList<Record_Categories>
    ) {
        viewModelScope.launch {
            val listWritingCardFilter = mutableListOf<WritingCard>()
            for (list in writingCard) {
                for (element in recordCategory) {
                    if (list.id == element.idRecord) {
                        listWritingCardFilter.add(list)
                    }
                }
            }
            _listWritingCard.value?.clear()
            _listWritingCard.value = listWritingCardFilter
            if (listWritingCard.value!!.isNotEmpty()) {
                listWritingCard.value?.shuffle()
                val writingCardSelectedList = mutableListOf<WritingCard>()
                listWritingCard.value?.let { writingCardSelectedList.add(it.get(0)) }
                _writingCardSelected.value = writingCardSelectedList
            }
        }
    }

    private fun getWithOutCategories() {
        listWritingCard.value?.shuffle()
        val writingCardSelectedList = mutableListOf<WritingCard>()
        listWritingCard.value?.let { writingCardSelectedList.add(it.get(0)) }
        _writingCardSelected.value = writingCardSelectedList
    }

    fun nextCard() {
        iterador++
        if (iterador < listWritingCard.value!!.size) {
            _writingCardSelected.value?.clear()
            val nextWritingCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { nextWritingCardList.add(it) }
            _writingCardSelected.value = nextWritingCardList
        } else {
            iterador = 0
            listWritingCard.value?.shuffle()
            _writingCardSelected.value?.clear()
            val writingCardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { writingCardSelectedList.add(it.get(iterador)) }
            _writingCardSelected.value = writingCardSelectedList
        }
    }

    fun lastCard() {
        iterador--
        if (iterador >= 0) {
            _writingCardSelected.value?.clear()
            val lastWritingCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { lastWritingCardList.add(it) }
            _writingCardSelected.value = lastWritingCardList
        } else {
            iterador = listWritingCard.value!!.size - 1
            listWritingCard.value?.shuffle()
            _writingCardSelected.value?.clear()
            val writingCardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { writingCardSelectedList.add(it.get(iterador)) }
            _writingCardSelected.value = writingCardSelectedList
        }
    }


}


class WritingViewModelFactory(
    private val writingCardDao: WritingCardDao,
    private val recordCategoriesDao: RecordCategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WritingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WritingViewModel(writingCardDao, recordCategoriesDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}