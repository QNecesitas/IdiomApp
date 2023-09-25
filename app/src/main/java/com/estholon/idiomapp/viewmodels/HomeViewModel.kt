package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.database.CategoriesDao
import com.estholon.idiomapp.database.IdiomsDao
import kotlinx.coroutines.launch

class HomeViewModel(private val categoriesDao: CategoriesDao,
                    private val idiomsDao: IdiomsDao) : ViewModel() {

    //List category
    private val _listCategory = MutableLiveData<MutableList<Category>>()
    val listCategory: LiveData<MutableList<Category>> get() = _listCategory

    //List Idiom
    private val _listIdioms = MutableLiveData<MutableList<Idioms>>()
    val listIdioms: LiveData<MutableList<Idioms>> get() = _listIdioms

    //List Idiom Right
    private val _listIdiomsRight = MutableLiveData<MutableList<Idioms>>()
    val listIdiomsRight: LiveData<MutableList<Idioms>> get() = _listIdiomsRight

    //List Idiom Left
    private val _listIdiomsLeft = MutableLiveData<MutableList<Idioms>>()
    val listIdiomsLeft: LiveData<MutableList<Idioms>> get() = _listIdiomsLeft

    private var itemIdiomRight = Idioms("FR","Français")

    private var lastPositionRight = 0;

    private var itemIdiomLeft = Idioms("ES","Español")

    private var lastPositionLeft = 0;

    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value= mutableListOf ()
            _listIdioms.value= idiomsDao.fetchIdioms()
            filterIdioms()
        }
    }

    private fun filterIdioms(){
        val tempListLeft = mutableListOf<Idioms>()
        val tempListRight = mutableListOf<Idioms>()

        _listIdioms.value?.let {
            tempListLeft.addAll(it)
        }
        _listIdioms.value?.let {
            tempListRight.addAll(it)
        }

        tempListLeft.remove(itemIdiomRight)
        tempListLeft.remove(itemIdiomLeft)
        tempListLeft.add(lastPositionLeft,itemIdiomLeft)

        tempListRight.remove(itemIdiomLeft)
        tempListRight.remove(itemIdiomRight)
        tempListRight.add(lastPositionRight,itemIdiomRight)

        _listIdiomsLeft.value = tempListLeft
        _listIdiomsRight.value = tempListRight
    }

    fun selectedIdiomRight(idioms: Idioms, position: Int){
        lastPositionRight = position
        if (_listIdioms.value != null){
            itemIdiomRight = idioms
        }
        filterIdioms()
    }

    fun selectedIdiomLeft(idioms: Idioms, position: Int){
        lastPositionLeft = position
        if (_listIdioms.value != null) itemIdiomLeft = idioms
        filterIdioms()
    }

    fun addCategory(newCategory: String){
        viewModelScope.launch {


            val category = Category( 0, newCategory)

            categoriesDao.insertCategory(category)

            refreshCategories()

        }
    }
    fun refreshCategories() {
        viewModelScope.launch {
            _listCategory.value = mutableListOf()
            _listCategory.value = categoriesDao.fetchCategories()
        }
    }
}



class HomeViewModelFactory(
    private val categoryDao: CategoriesDao,
    private val idiomsDao: IdiomsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(categoryDao, idiomsDao ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}