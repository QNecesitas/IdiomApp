package com.estholon.idiomapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.data.Translations
import com.estholon.idiomapp.database.CategoriesDao
import com.estholon.idiomapp.database.IdiomsDao
import com.estholon.idiomapp.database.RecordCategoriesDao
import com.estholon.idiomapp.database.RecordsDao
import com.estholon.idiomapp.database.TranslationsDao
import kotlinx.coroutines.launch

class NewRecordViewModel(
    private val idiomsDao: IdiomsDao,
    private val categoriesDao: CategoriesDao,
    private val recordCategoriesDao: RecordCategoriesDao,
    private val recordsDao: RecordsDao,
    private val translationsDao: TranslationsDao
) : ViewModel() {

    //List Idiom
    private val _listIdioms = MutableLiveData<MutableList<Idioms>>()
    val listIdioms: LiveData<MutableList<Idioms>> get() = _listIdioms

    //List Idiom
    private val _listRecords = MutableLiveData<MutableList<Records>>()
    val listRecords: LiveData<MutableList<Records>> get() = _listRecords

    //List category
    private val _listCategory = MutableLiveData<MutableList<Category>>()
    val listCategory: LiveData<MutableList<Category>> get() = _listCategory
    private var i = 2

    //List category
    private val _selectedCategory = MutableLiveData<MutableList<Category>>()
    val selectedCategory: LiveData<MutableList<Category>> get() = _selectedCategory

    private val _listRecordDB = MutableLiveData<MutableList<Records>>()
    private val listRecordDB: LiveData<MutableList<Records>> get() = _listRecordDB
    fun getAllIdioms() {
        viewModelScope.launch {
            _listIdioms.value = mutableListOf()
            _listIdioms.value = idiomsDao.fetchIdioms()
        }
    }

    fun getAllSentences() {
        viewModelScope.launch {
            _listRecords.value = mutableListOf()
            _listRecords.value?.add(Records(1, "", "no", "ES"))
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
            val sentenceToDelete = deleteSentences.find { it.id == id }
            if (sentenceToDelete != null) {
                deleteSentences.remove(sentenceToDelete)
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
            _selectedCategory.value?.add(Category(id, categories))
            _selectedCategory.value?.let { listSelectedCategories.addAll(it) }
            _selectedCategory.value = listSelectedCategories
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            val deleteCategories = mutableListOf<Category>()
            _selectedCategory.value?.let { deleteCategories.addAll(it) }
            val categoriesToDelete = deleteCategories.find { it.id == id }
            if (categoriesToDelete != null) {
                deleteCategories.remove(categoriesToDelete)
            }
            _selectedCategory.value = deleteCategories
        }
    }

    fun deleteCategories(id: Int) {
        viewModelScope.launch {
            categoriesDao.deleteCategory(id)
            refreshCategories()
        }
    }

    fun deleteCategoryRecord(id: Int) {
        viewModelScope.launch {
            recordCategoriesDao.deleteCategory(id)
        }
    }

    init {
        _selectedCategory.value = mutableListOf()
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
                selectedCategory.value?.let { it1 ->
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
                recordCategoriesDao.insertCategoryRecord(id, i.id)
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
}

class NewRecordViewModelFactory(
    private val idiomsDao: IdiomsDao,
    private val categoriesDao: CategoriesDao,
    private val recordCategoriesDao: RecordCategoriesDao,
    private val recordsDao: RecordsDao,
    private val translationsDao: TranslationsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewRecordViewModel(
                idiomsDao,
                categoriesDao,
                recordCategoriesDao,
                recordsDao,
                translationsDao
            ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}