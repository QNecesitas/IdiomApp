package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.database.IdiomsDao
import kotlinx.coroutines.launch

class NewRecordViewModel(private val idiomsDao:IdiomsDao):ViewModel() {

    //List Idiom
    private val _listIdioms = MutableLiveData<MutableList<Idioms>>()
    val listIdioms: LiveData<MutableList<Idioms>> get() = _listIdioms

    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value= mutableListOf ()
            _listIdioms.value= idiomsDao.fetchIdioms()
        }
    }
}
class NewRecordViewModelFactory(
    private val idiomsDao: IdiomsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewRecordViewModel(idiomsDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}