package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.data.Translations


@Dao
 interface TranslationsDao {

  @Insert
  suspend fun insertTranslation(translations: Translations)

 @Query("SELECT idRecord AS id ,sentence, 'no' AS image, idIdiom  FROM Translations")
 suspend fun fetchTranslationsAsRecords(): MutableList<Records>

}