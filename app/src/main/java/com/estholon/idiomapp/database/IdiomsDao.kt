package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query
import com.estholon.idiomapp.data.Idioms
import kotlinx.coroutines.flow.Flow

@Dao
interface IdiomsDao {
    @Query("SELECT * FROM Idioms ")
    suspend fun fetchIdioms(): MutableList<Idioms>
}