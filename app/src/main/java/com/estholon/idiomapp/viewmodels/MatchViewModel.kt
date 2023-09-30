package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.CardMatch
import com.estholon.idiomapp.data.WritingCard
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.database.WritingCardDao
import com.estholon.idiomapp.database.Record_CategoriesDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MatchViewModel(
    private val cardDao: WritingCardDao ,
    private val recordCategoriesdao: Record_CategoriesDao
) : ViewModel() {

    //List category
    private val _listCard = MutableLiveData<MutableList<WritingCard>>()
    val listCard: LiveData<MutableList<WritingCard>> get() = _listCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory

    private val _cardSelected = MutableLiveData<MutableList<WritingCard>>()
    val cardSelected: LiveData<MutableList<WritingCard>> get() = _cardSelected

    private var _matchRecord = MutableLiveData<MutableList<CardMatch>>()
    val matchRecord: LiveData<MutableList<CardMatch>> get() = _matchRecord

    private val _matchSelected = MutableLiveData<MutableList<CardMatch>>()
    val matchSelected: LiveData<MutableList<CardMatch>> get() = _matchSelected

    var error = 0


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

    private fun getAllCategoryRelationsBD(category: MutableList<Category>) {
        viewModelScope.launch {
            _listRecordCategory.value = mutableListOf()
            _listRecordCategory.value = recordCategoriesdao.fetchRecordCategory()
            listRecordCategory.value?.let { getRecordCategoryFiltered(category , it) }
        }
    }

    private fun getRecordCategoryFiltered(
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

    private fun getCardFilteredRandom(
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
                if (listCard.value!!.size >= 4) {
                    val cardSelectedList = mutableListOf<WritingCard>()
                    for (i in 0 until 4) {
                        listCard.value?.let { cardSelectedList.add(it.get(i)) }
                    }
                    _cardSelected.value = cardSelectedList
                }


            }
            listGetMatch()
        }
    }


    private fun getWithOutCategories() {
        if (listCard.value!!.isNotEmpty()) {
            listCard.value?.shuffle()
            if (listCard.value!!.size >= 4) {
                val cardSelectedList = mutableListOf<WritingCard>()
                for (i in 0 until 4) {
                    listCard.value?.let { cardSelectedList.add(it.get(i)) }
                }
                _cardSelected.value = cardSelectedList
            }


        }
        listGetMatch()
    }

    private fun listGetMatch() {
        val listMatch = mutableListOf<CardMatch>()
        if(cardSelected.value!=null) {
            for (matchRandom in cardSelected.value!!) {
                listMatch.add(
                    CardMatch(
                        matchRandom.id,
                        matchRandom.sentence,
                        matchRandom.idiomSentence,
                        "Nada"
                    )
                )
                listMatch.add(
                    CardMatch(
                        matchRandom.id,
                        matchRandom.translation,
                        matchRandom.idiomTranslation,
                        "Nada"
                    )
                )
            }
            listMatch.shuffle()
            _matchRecord.value = listMatch
        }
    }

    fun getMatchSelected(id: Int , position: Int,idIdiom: String) {
        //Fill auxiliary list with the 8 elements from matchRecord
        viewModelScope.launch {
            val auxiliaryList = mutableListOf<CardMatch>()
            matchRecord.value?.let { auxiliaryList.addAll(it) }

            //If the list with selected elements is empty
            if (matchSelected.value?.isEmpty() == true) {
                auxiliaryList[position].state = "Seleccionado"
                _matchSelected.value?.add(auxiliaryList[position])
                _matchRecord.value = auxiliaryList

            } else {
                //If the list with selected elements contains any
                val selected = matchSelected.value!![0]

                if (id == selected.id && idIdiom != selected.idIdiom) {
                    //If the the ids are the same but not is the selected (The selected is the good response)
                    auxiliaryList[position].state = "Seleccionado"
                    _matchRecord.value = auxiliaryList
                    for (match in auxiliaryList) {
                        if (match.id == selected.id) {
                            match.state = "Correcto"
                        }
                    }
                    _matchSelected.value?.clear()
                    _matchRecord.value = auxiliaryList
                    delay(500)
                    auxiliaryList.removeIf { id == it.id }
                    _matchRecord.value = auxiliaryList

                } else if (id == selected.id) {
                    //If the id are the same and is the selected (The selected is already selected)
                    _matchSelected.value?.clear()
                    auxiliaryList[position].state = "Nada"

                } else {
                    //If the ids are not the same (The selected is the bad response)
                    error++
                    auxiliaryList[position].state = "Mal"
                    for (match in auxiliaryList) {
                        if (match.id == selected.id && match.sentence == selected.sentence) {
                            match.state = "Mal"

                        }
                    }

                    _matchRecord.value = auxiliaryList
                    delay(500)
                    auxiliaryList[position].state = "Nada"
                    for (match in auxiliaryList) {
                        if (match.id == selected.id) {
                            match.state = "Nada"
                            _matchSelected.value?.clear()
                            _matchRecord.value = auxiliaryList

                        }
                    }

                }

            }

        }
    }

    init {
        _matchSelected.value = mutableListOf()
    }
}

class MatchViewModelFactory(
    private val cardDao: WritingCardDao ,
    private val recordCategoriesdao: Record_CategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchViewModel(cardDao , recordCategoriesdao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}