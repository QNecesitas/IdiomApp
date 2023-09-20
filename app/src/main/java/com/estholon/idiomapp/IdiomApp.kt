package com.estholon.idiomapp

import android.app.Application
import com.estholon.idiomapp.database.AppDatabase

class IdiomApp:Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}