package com.estholon.idiomapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Record_Categories
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.data.Translations

@Database(
        entities = [Category::class , Idioms::class , Record_Categories::class, Records::class, Translations::class] ,
        version = 1 ,
        exportSchema = false
    )
    abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriesDao(): CategoriesDao
    abstract fun idiomsDao(): IdiomsDao
    abstract fun recordCategoriesDao(): RecordCategoriesDao
    abstract fun recordsDao():RecordsDao
    abstract fun translationsDao():TranslationsDao

    abstract fun cardDao():WritingCardDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null
            private val callback = RoomDatabaseImport()

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext ,
                        AppDatabase::class.java ,
                        "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(callback)
                        .build()
                    INSTANCE = instance

                    instance
                }
            }
        }

    }
