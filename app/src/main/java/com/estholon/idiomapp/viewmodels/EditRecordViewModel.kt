package com.estholon.idiomapp.viewmodels

import android.util.Log
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

    private var i = 150000

    //List Idiom
    private val _listRecordDB = MutableLiveData<MutableList<Records>>()
    val listRecordDB: LiveData<MutableList<Records>> get() = _listRecordDB

    //List category
    private val _listAllCategory = MutableLiveData<MutableList<Category>>()
    val listAllCategory: LiveData<MutableList<Category>> get() = _listAllCategory
    //List category
    private val _listPhoto = MutableLiveData<MutableList<Records>>()
    val listPhoto: LiveData<MutableList<Records>> get() = _listPhoto

    init {
        _listAuxiliarCategory.value= mutableListOf()
    }
    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value = mutableListOf()
            _listIdioms.value = idiomsDao.fetchIdioms()
        }
    }

    fun getRecord(idRecord: Int) {
        var iterador=idRecord + 1
        listAuxiliar.value?.clear()
        //get date for Record of the all table
        viewModelScope.launch {
            _listAuxiliar.value = mutableListOf()
            _listAuxiliar.value = recordsDao.fetchRecordforId(idRecord)
            _listTranslation.value = mutableListOf()
            _listTranslation.value = translationsDao.fetchTranslationforId(idRecord)
            _listCategories.value = mutableListOf()
            _listCategories.value = recordCategoriesdao.fetchCategoriesforId(idRecord)
            _listAllCategory.value = categoriesDao.fetchCategories()
            //Join the ListRecord with the ListTranslation
            if (listTranslation.value?.isNotEmpty() == true) {
                //TODO Aqui la lista auxiliar esta recorriendo la lista translation buscando a su pareja por id y añadiendola,pero me devuelve doble los elementos que coge en traslation
                for (translation in listTranslation.value!!) {
                    listAuxiliar.value!!.add(
                        Records(
                            iterador++  ,
                            translation.sentence ,
                            "no" ,
                            translation.idIdiom
                        )
                    )
                }
            }
            _listRecords.value = _listAuxiliar.value
            _listPhoto.value = _listAuxiliar.value
            if (listCategories.value?.isNotEmpty() == true && listAllCategory.value?.isNotEmpty() == true) {
                for (categoryRecord in listCategories.value!!) {
                    for (category in listAllCategory.value!!) {
                        if (categoryRecord.idCategories == category.id) {
                            _listAuxiliarCategory.value?.add(category)
                        }
                    }
                }
            }
            _listCategorySelected.value = listAuxiliarCategory.value
            listAuxiliarCategory.value?.clear()
        }


    }
    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            val deleteCategorias = mutableListOf<Category>()
            _listCategorySelected.value?.let { deleteCategorias.addAll(it) }
            val categoriaAEliminar = deleteCategorias.find { it.id == id }
            if (categoriaAEliminar != null) {
                deleteCategorias.remove(categoriaAEliminar)
            }
            _listCategorySelected.value = deleteCategorias
        }
    }

    fun addDateLIstSentence() {
        viewModelScope.launch {
            val addListRecord = mutableListOf<Records>()
            _listRecords.value?.let { addListRecord.addAll(it) }
            addListRecord.add(Records(i++, "", "no", "ES"))
            _listRecords.value = addListRecord
        }
    }
    fun deleteSentence(id: Int) {
        viewModelScope.launch {
            val deleteSentences = mutableListOf<Records>()
            _listRecords.value?.let { deleteSentences.addAll(it) }
            val sentenceAEliminar = deleteSentences.find { it.id == id }
            if (sentenceAEliminar != null) {
                deleteSentences.remove(sentenceAEliminar)
            }
            _listRecords.value = deleteSentences
        }
    }
    fun refreshCategories() {
        viewModelScope.launch {
            _listCategory.value = mutableListOf()
            _listCategory.value = categoriesDao.fetchCategories()
        }
    }

    fun addCategory(newCategory: String) {
        viewModelScope.launch {


            val category = Category(0, newCategory)

            categoriesDao.insertCategory(category)

            refreshCategories()

        }
    }
    fun selectedCategory(id: Int, categories: String) {
        viewModelScope.launch {
            val listSelectedCategories = mutableListOf<Category>()
            _listCategorySelected.value?.add(Category(id, categories))
            _listCategorySelected.value?.let { listSelectedCategories.addAll(it) }
            _listCategorySelected.value = listSelectedCategories
        }
    }

    fun deleteCategoryRecord(id: Int) {
        viewModelScope.launch {
            recordCategoriesdao.deleteCategory(id)
        }
    }

    fun deleteCategories(id: Int) {
        viewModelScope.launch {
            categoriesDao.deleteCategory(id)
            refreshCategories()
        }
    }

    fun insertRecord(image: String, sentence: String, idIdiom: String) {
        viewModelScope.launch {
            val record = Records(0, sentence, image, idIdiom)
            recordsDao.insertRecord(record)
            getRecordDB(sentence)
        }
    }
    private fun getRecordDB(sentence: String) {
        viewModelScope.launch {
            _listRecordDB.value = mutableListOf()
            _listRecordDB.value = recordsDao.getRecord(sentence)
            listRecordDB.value?.last()?.let {
                listCategorySelected.value?.let { it1 ->
                    insertRecordCategory(
                        it.id,
                        it1
                    )
                }
            }
        }
    }

    private fun insertRecordCategory(id: Int, category: MutableList<Category>) {
        viewModelScope.launch {
            for (i in category) {
                recordCategoriesdao.insertCategoryRecord(id, i.id)
            }
            listRecords.value?.let { insertTranslation(it, id) }
        }
    }
    private fun insertTranslation(record: MutableList<Records>, idRecord: Int) {
        viewModelScope.launch {
            for ((index, i) in record.withIndex()) {
                if (index == 0) continue
                val translation = Translations(0, i.sentence, i.idIdiom, idRecord)
                translationsDao.insertTranslation(translation)
            }
        }

    }

    fun deleteRecord(idRecord:Int){
        viewModelScope.launch {
            recordsDao.deleteRecord(idRecord)
            translationsDao.deleteTranslation(idRecord)
            recordCategoriesdao.deleteRecordCategory(idRecord)
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