package com.estholon.idiomapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Idioms (
    @PrimaryKey(autoGenerate = false) var id:String,
    @ColumnInfo(name = "idiom") var idiom:String,
    @ColumnInfo(name = "image") var image:String
    )