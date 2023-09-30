package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.estholon.idiomapp.data.Records

@Dao
interface RecordsDao {

    @Query("SELECT * FROM Records ")
    suspend fun fetchRecord(): MutableList<Records>

    @Query("SELECT * FROM Records WHERE idIdiom=:idIdiom ")
    suspend fun fetchFilterRecord(idIdiom:String): MutableList<Records>

    @Insert
    suspend fun insertRecord(records: Records)

    @Query("SELECT * FROM Records WHERE sentence=:sentence")
    suspend fun getRecord(sentence: String):MutableList<Records>

    @Query("SELECT * FROM Records WHERE id=:id")
    suspend fun fetchRecordForId(id:Int):Records

    @Query("DELETE FROM Records WHERE id=:id")
    suspend fun deleteRecord(id:Int)

}