package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Query
import com.estholon.idiomapp.data.Card

@Dao
interface CardDao {

    @Query("SELECT r.id, r.sentence, t.sentence AS translation, r.image, r.idIdiom AS idiomSentence, t.idIdiom AS idiomTranslation FROM Records r INNER JOIN Translations t WHERE r.id = t.idRecord AND ((r.idIdiom =:idIdiomI AND t.idIdiom =:idIdiomD) OR (r.idIdiom =:idIdiomD AND t.idIdiom =:idIdiomI))")
    suspend fun fetchCard(idIdiomI:String,idIdiomD: String):MutableList<Card>
}