package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query
import com.estholon.idiomapp.data.Records

@Dao
interface RecordsDao {

    @Query("SELECT * FROM Records ")
    suspend fun fetchRecord(): MutableList<Records>

    @Query("SELECT * FROM Records WHERE idIdiom=:idIdiom ")
    suspend fun fetchFilterRecord(idIdiom:String): MutableList<Records>

}