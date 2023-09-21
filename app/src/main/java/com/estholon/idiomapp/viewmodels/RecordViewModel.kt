package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.database.RecordsDao
import kotlinx.coroutines.launch

class RecordViewModel(private val recordDao: RecordsDao) : ViewModel() {

    //List Record
    private val _listRecord = MutableLiveData<MutableList<Records>>()
    val listRecord: LiveData<MutableList<Records>> get() = _listRecord

    fun getAllRecord() {
        viewModelScope.launch {
            _listRecord.value = mutableListOf()
            _listRecord.value = recordDao.fetchRecord()
        }
    }
}

class RecordViewModelFactory(
    private val recordDao: RecordsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(recordDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}