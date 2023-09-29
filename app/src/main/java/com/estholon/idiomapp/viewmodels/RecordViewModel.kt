package com.estholon.idiomapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.data.Translations
import com.estholon.idiomapp.database.IdiomsDao
import com.estholon.idiomapp.database.RecordsDao
import com.estholon.idiomapp.database.TranslationsDao
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecordViewModel(
    private val recordDao: RecordsDao,
    private val idiomsDao: IdiomsDao,
    private val translationsDao: TranslationsDao
) : ViewModel() {

    //List Record
    private val _listRecord = MutableLiveData<MutableList<Records>>()
    val listRecord: LiveData<MutableList<Records>> get() = _listRecord

    //List driver
    private val _listRecordFilter = MutableLiveData<MutableList<Records>>()
    val listRecordFilter: LiveData<MutableList<Records>> get() = _listRecordFilter

    //List Idiom
    private val _listIdioms = MutableLiveData<MutableList<Idioms>>()
    val listIdioms: LiveData<MutableList<Idioms>> get() = _listIdioms

    fun getAllRecord() {
        viewModelScope.launch {
            _listRecord.value = mutableListOf()
            val auxiliarList = mutableListOf<Records>()
            auxiliarList.addAll(recordDao.fetchRecord())
            auxiliarList.addAll(translationsDao.fetchTranslationsAsRecords())
            auxiliarList.sortBy { it.sentence.lowercase() }
            _listRecord.value = auxiliarList
        }
    }

    fun filterByText(text: String) {
        if (text.trim().isNotEmpty()) {
            val filterList = _listRecord.value?.filter {
                it.sentence.contains(text, ignoreCase = true)

            }?.toMutableList()

            if (filterList != null) {
                _listRecordFilter.postValue(filterList)
            }

        } else {
            _listRecordFilter.postValue(listRecord.value)
        }
    }

    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value = mutableListOf()
            _listIdioms.value = idiomsDao.fetchIdioms()
            _listIdioms.value!!.add(0, Idioms("ALL", "Todos"))
        }
    }

    fun getFilterRecord(id: String) {
        viewModelScope.launch {
            val records = recordDao.fetchRecord()
            val translationsAsRecord = translationsDao.fetchTranslationsAsRecords()
            val auxiliarList = mutableListOf<Records>()
            auxiliarList.addAll(records)
            auxiliarList.addAll(translationsAsRecord)
            val auxListFiltered = auxiliarList.filter { it.idIdiom == id }.toMutableList()
            _listRecord.value = auxListFiltered

        }
    }


}

class RecordViewModelFactory(
    private val recordDao: RecordsDao,
    private val idiomsDao: IdiomsDao,
    private val translationsDao: TranslationsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RecordViewModel(
                recordDao,
                idiomsDao,
                translationsDao
            ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}