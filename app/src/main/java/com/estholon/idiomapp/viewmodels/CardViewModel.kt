package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.data.WritingCard
import com.estholon.idiomapp.database.RecordCategoriesDao
import com.estholon.idiomapp.database.WritingCardDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardViewModel(
    private val writingCardDao: WritingCardDao,
    private val recordCategoriesdao: RecordCategoriesDao
) : ViewModel() {

    //List category
    private val _listWritingCard = MutableLiveData<MutableList<WritingCard>>()
    val listWritingCard: LiveData<MutableList<WritingCard>> get() = _listWritingCard

    private val _listRecordCategory = MutableLiveData<MutableList<Record_Categories>>()
    private val listRecordCategory: LiveData<MutableList<Record_Categories>> get() = _listRecordCategory
    private var iterador = 0

    private val _writingCardSelected = MutableLiveData<MutableList<WritingCard>>()
    val writingCardSelected: LiveData<MutableList<WritingCard>> get() = _writingCardSelected

    enum class StateTime { START, FIRST_WORLD, SECOND_WORLD, FINISH }

    private val _state = MutableLiveData<StateTime>()
    val state: LiveData<StateTime> get() = _state

    private val _speed = MutableLiveData<Double>()
    val speed: LiveData<Double> get() = _speed

    private val _playStop = MutableLiveData<Boolean>()
    val playStop: LiveData<Boolean> get() = _playStop


    init {
        _speed.value = 1.0
        _playStop.value = false
    }


    fun setPlayStop(isPlay: Boolean) {
        _playStop.value = isPlay
    }



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
                        this@CardViewModel, it,
                        it1
                    )
                }
            }
        }

    }

    private fun getWithOutCategories() {
        //Filter by undefined words
        _listWritingCard.value = listWritingCard.value?.filter {
            it.translation != "Por definir" && it.translation != "To define" && it.translation != "Definieren" && it.translation != "À définir"
        }?.toMutableList()

        listWritingCard.value?.shuffle()
        val cardSelectedList = mutableListOf<WritingCard>()
        listWritingCard.value?.let { cardSelectedList.add(it[0]) }
        _writingCardSelected.value = cardSelectedList
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


    //Buttons
    fun nextCard() {
        iterador++
        if (iterador < listWritingCard.value!!.size) {
            _writingCardSelected.value?.clear()
            val nextCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { nextCardList.add(it) }
            _writingCardSelected.value = nextCardList
        } else {
            iterador = 0
            listWritingCard.value?.shuffle()
            _writingCardSelected.value?.clear()
            val cardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { cardSelectedList.add(it[iterador]) }
            _writingCardSelected.value = cardSelectedList
        }
    }

    fun lastCard() {
        iterador--
        if (iterador >= 0) {
            _writingCardSelected.value?.clear()
            val lastCardList = mutableListOf<WritingCard>()
            listWritingCard.value?.get(iterador)?.let { lastCardList.add(it) }
            _writingCardSelected.value = lastCardList
        } else {
            iterador = listWritingCard.value!!.size - 1
            listWritingCard.value?.shuffle()
            _writingCardSelected.value?.clear()
            val cardSelectedList = mutableListOf<WritingCard>()
            listWritingCard.value?.let { cardSelectedList.add(it[iterador]) }
            _writingCardSelected.value = cardSelectedList
        }
    }

    fun timeToShow() {
        viewModelScope.launch {
            val time = 2000 / speed.value!!
            val delayTime: Long = time.toLong()
            if (playStop.value == true) _state.value = StateTime.START
            if(playStop.value == true) delay(delayTime)
            if (playStop.value == true) _state.value = StateTime.FIRST_WORLD
            if(playStop.value == true) delay(delayTime)
            if (playStop.value == true) _state.value = StateTime.SECOND_WORLD
            if(playStop.value == true) delay(delayTime)
            if (playStop.value == true) _state.value = StateTime.FINISH
            if(playStop.value == true) delay(delayTime)
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

    companion object {
        private fun getCardFilteredRandom(
            cardViewModel: CardViewModel, card: MutableList<WritingCard>,
            recordCategory: MutableList<Record_Categories>
        ) {
            cardViewModel.viewModelScope.launch {

                //Filter by selected categories
                var listWritingCardFilter = mutableListOf<WritingCard>()
                for (list in card) {
                    for (element in recordCategory) {
                        if (list.id == element.idRecord) {
                            listWritingCardFilter.add(list)
                        }
                    }
                }

                //Filter by undefined words
                listWritingCardFilter = listWritingCardFilter.filter {
                    it.translation != "Por definir" && it.translation != "To define" && it.translation != "Definieren" && it.translation != "À définir"
                }.toMutableList()


                cardViewModel._listWritingCard.value?.clear()
                cardViewModel._listWritingCard.value = listWritingCardFilter
                if (cardViewModel.listWritingCard.value!!.isNotEmpty()) {
                    cardViewModel.listWritingCard.value?.shuffle()
                    val cardSelectedList = mutableListOf<WritingCard>()
                    cardViewModel.listWritingCard.value?.let { cardSelectedList.add(it[0]) }
                    cardViewModel._writingCardSelected.value = cardSelectedList
                }
            }
        }
    }
}


class CardViewModelFactory(
    private val writingCardDao: WritingCardDao,
    private val recordCategoriesdao: RecordCategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(writingCardDao, recordCategoriesdao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}