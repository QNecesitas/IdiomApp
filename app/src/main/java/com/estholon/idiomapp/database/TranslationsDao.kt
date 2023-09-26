package com.estholon.idiomapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.estholon.idiomapp.data.Translations


@Dao
 interface TranslationsDao {

  @Insert
  suspend fun insertTranslation(translations: Translations)
}