package com.estholon.idiomapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.database.CategoriesDao
import kotlinx.coroutines.launch

class HomeViewModel(private val categoriesDao: CategoriesDao) : ViewModel() {

    //List category
    private val _listCategory = MutableLiveData<MutableList<Category>>()
    val listCategory: LiveData<MutableList<Category>> get() = _listCategory


    fun addCategory(newCategory: String){
        viewModelScope.launch {


            val category = Category( 0, newCategory)

            categoriesDao.insertCategory(category)

            refresh()

        }
    }
    fun refresh() {
        viewModelScope.launch {
            _listCategory.value = mutableListOf()

            _listCategory.value = categoriesDao.fetchCategories()
        }
    }
}



class HomeViewModelFactory(
    private val categoryDao: CategoriesDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(categoryDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}