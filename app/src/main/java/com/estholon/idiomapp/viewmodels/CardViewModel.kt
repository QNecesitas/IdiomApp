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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardViewModel(private val writingCardDao: WritingCardDao, private val recordCategoriesdao: Record_CategoriesDao):ViewModel() {

    //List category
    private val _listWritingCard = MutableLiveData<MutableList<WritingCard>>()
    val listWritingCard: LiveData<MutableList<WritingCard>> get() = _listWritingCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory
    var iterador = 0

    private val _writing_cardSelected = MutableLiveData<MutableList<WritingCard>>()
    val writingCardSelected: LiveData<MutableList<WritingCard>> get() = _writing_cardSelected

    enum class StateTime { START , FIRST_WORLD , SECOND_WORLD , FINISH }

    private val _state = MutableLiveData<StateTime>()
    val state: LiveData<StateTime> get() = _state

    private val _speed = MutableLiveData<Double>()
    val speed: LiveData<Double> get() = _speed

    private val _playStop = MutableLiveData<Boolean>()
    val playStop: LiveData<Boolean> get() = _playStop


    init {
        _speed.value = 1.0
        _playStop.value=true
    }


    //Obtain info
    fun getAllCardsBD(idIdiomI: String , idIdiomD: String , category: MutableList<Category>) {
        viewModelScope.launch {
            _listWritingCard.value = writingCardDao.fetchCard(idIdiomI , idIdiomD)
            if (listWritingCard.value!!.isNotEmpty()) {
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
            listWritingCard.value?.let {
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
            val listWritingCardFilter = mutableListOf<WritingCard>()
            for (list in card) {
                for (element in recorCategory) {
                    if (list.id == element.idRecord) {
                        listWritingCardFilter.add(list)
                    }
                }
            }
            _listWritingCard.value?.clear()
            _listWritingCard.value = listWritingCardFilter
            if (listWritingCard.value!!.isNotEmpty()) {
                listWritingCard.value?.shuffle()
                val cardSelectedList = mutableListOf<WritingCard>()
                listWritingCard.value?.let { cardSelectedList.add(it.get(0)) }
                _writing_cardSelected.value = cardSelectedList
            }
        }
    }

    fun getWithOutCategories() {
        listWritingCard.value?.shuffle()
        val cardSelectedList = mutableListOf<WritingCard>()
        listWritingCard.value?.let { cardSelectedList.add(it.get(0)) }
        _writing_cardSelected.value = cardSelectedList
    }

    fun nextCard() {
        iterador++
        if (iterador < listWritingCard.value!!.size) {
            _writing_cardSelected.value?.clear()
            val nextCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { nextCardList.add(it) }
            _writing_cardSelected.value = nextCardList
        } else {
            iterador = 0
            listWritingCard.value?.shuffle()
            _writing_cardSelected.value?.clear()
            val cardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { cardSelectedList.add(it.get(iterador)) }
            _writing_cardSelected.value = cardSelectedList
        }
    }

    fun lastCard() {
        iterador--
        if (iterador >= 0) {
            _writing_cardSelected.value?.clear()
            val lastCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { lastCardList.add(it) }
            _writing_cardSelected.value = lastCardList
        } else {
            iterador = listWritingCard.value!!.size - 1
            listWritingCard.value?.shuffle()
            _writing_cardSelected.value?.clear()
            val cardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { cardSelectedList.add(it.get(iterador)) }
            _writing_cardSelected.value = cardSelectedList
        }
    }

    fun timeToShow() {
        viewModelScope.launch {
            val time = 2000 / speed.value!!
            val delayTime: Long = time.toLong()
            _state.value = StateTime.START
            delay(delayTime)
            _state.value = StateTime.FIRST_WORLD
            delay(delayTime)
            _state.value = StateTime.SECOND_WORLD
            delay(delayTime)
            _state.value = StateTime.FINISH
            delay(delayTime)
        }

    }

    fun speed() {
        when (speed.value) {
            1.0 -> {
                _speed.value = 2.0
            }

            2.0 -> {
                _speed.value = 0.5
            }

            0.5 -> {
                _speed.value = 1.0
            }
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