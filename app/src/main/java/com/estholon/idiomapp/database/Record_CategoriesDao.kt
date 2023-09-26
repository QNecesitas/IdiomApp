package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface Record_CategoriesDao {
    @Query("DELETE FROM Record_Categories WHERE idCategories=:id")
    suspend fun deleteCategory(id:Int)

    @Query("INSERT INTO Record_Categories VALUES(:idRecord,:idCategory)")
    suspend fun insertCategoryRecord(idRecord:Int,idCategory:Int)
}