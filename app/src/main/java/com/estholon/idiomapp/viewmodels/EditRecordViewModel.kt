package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.data.Translations
import com.estholon.idiomapp.database.CategoriesDao
import com.estholon.idiomapp.database.IdiomsDao
import com.estholon.idiomapp.database.Record_CategoriesDao
import com.estholon.idiomapp.database.RecordsDao
import com.estholon.idiomapp.database.TranslationsDao
import kotlinx.coroutines.launch

class EditRecordViewModel(private val recordsDao: RecordsDao,private val translationsDao: TranslationsDao, private val recordCategoriesdao: Record_CategoriesDao,private val idiomsDao: IdiomsDao,private val categoriesDao: CategoriesDao):ViewModel() {

    //List Idiom
    private val _listRecords = MutableLiveData<MutableList<Records>>()
    val listRecords: LiveData<MutableList<Records>> get() = _listRecords

    //List Idiom
    private val _listTranslation = MutableLiveData<MutableList<Translations>>()
    val listTranslation: LiveData<MutableList<Translations>> get() = _listTranslation

    //List Idiom
    private val _listCategories = MutableLiveData<MutableList<Record_Categories>>()
    val listCategories: LiveData<MutableList<Record_Categories>> get() = _listCategories

    //List Idiom
    private val _listAuxiliar = MutableLiveData<MutableList<Records>>()
    val listAuxiliar: LiveData<MutableList<Records>> get() = _listAuxiliar

    //List Idiom
    private val _listIdioms = MutableLiveData<MutableList<Idioms>>()
    val listIdioms: LiveData<MutableList<Idioms>> get() = _listIdioms

    //List Idiom
    private val _listCategorySelected = MutableLiveData<MutableList<Category>>()
    val listCategorySelected: LiveData<MutableList<Category>> get() = _listCategorySelected

    //List category
    private val _listCategory = MutableLiveData<MutableList<Category>>()
    val listCategory: LiveData<MutableList<Category>> get() = _listCategory

    //List category
    private val _listAuxiliarCategory = MutableLiveData<MutableList<Category>>()
    val listAuxiliarCategory: LiveData<MutableList<Category>> get() = _listAuxiliarCategory

    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value = mutableListOf()
            _listIdioms.value = idiomsDao.fetchIdioms()
        }
    }

    fun getRecord(idRecord: Int) {
        //get date for Record of the all table
        viewModelScope.launch {
            _listAuxiliar.value = mutableListOf()
            _listAuxiliar.value = recordsDao.fetchRecordforId(idRecord)
            _listTranslation.value = mutableListOf()
            _listTranslation.value = translationsDao.fetchTranslationforId(idRecord)
            _listCategories.value = mutableListOf()
            _listCategories.value = recordCategoriesdao.fetchCategoriesforId(idRecord)
            _listCategory.value = mutableListOf()
            _listCategory.value = categoriesDao.fetchCategories()

            //Join the ListRecord with the ListTranslation
            for (translation in listTranslation.value!!) {
                _listAuxiliar.value!!.add(
                    Records(
                        translation.idRecord ,
                        translation.sentence ,
                        "no" ,
                        translation.idIdiom
                    )
                )
            }
            _listRecords.value = _listAuxiliar.value
            for (categoryRecord in listCategories.value!!) {
                for (category in listCategory.value!!){
                    if (categoryRecord.idCategories==category.id){
                        _listAuxiliarCategory.value?.add(category)
                    }
                }
            }
            _listCategorySelected.value=listAuxiliarCategory.value
        }

    }
    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            val deleteCategorias = mutableListOf<Record_Categories>()
            _listCategories.value?.let { deleteCategorias.addAll(it) }
            val categoriaAEliminar = deleteCategorias.find { it.idCategories == id }
            if (categoriaAEliminar != null) {
                deleteCategorias.remove(categoriaAEliminar)
            }
            _listCategories.value = deleteCategorias
        }
    }
}

class EditRecordViewModelFactory(
    private val recordsDao: RecordsDao,
    private val translationsDao: TranslationsDao,
    private val recordCategoriesdao: Record_CategoriesDao,
    private val idiomsDao: IdiomsDao,
    private val categoriesDao: CategoriesDao

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditRecordViewModel(recordsDao,translationsDao,recordCategoriesdao,idiomsDao,categoriesDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}