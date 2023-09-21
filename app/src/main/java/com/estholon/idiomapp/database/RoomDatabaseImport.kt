package com.estholon.idiomapp.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomDatabaseImport:RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        //Idioms
        db.execSQL("INSERT INTO 'Idioms' VALUES"+
        "('ES','Español'),"+
        "('FR','Français'),"+
        "('DE','Deutsch'),"+
        "('PT','Português'),"+
        "('EN','English')")
    }
}