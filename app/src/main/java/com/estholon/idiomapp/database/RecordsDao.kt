package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query
import com.estholon.idiomapp.data.Records

@Dao
interface RecordsDao {

    @Query("SELECT * FROM Records ")
    suspend fun fetchRecord(): MutableList<Records>

}