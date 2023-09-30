package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query
import com.estholon.idiomapp.data.WritingCard

@Dao
interface WritingCardDao {

    g
    suspend fun fetchCard(idIdiomI:String,idIdiomD: String):MutableList<WritingCard>
}