package com.estholon.idiomapp.database

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.estholon.idiomapp.data.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Category")
    suspend fun fetchCategories(): MutableList<Category>

}