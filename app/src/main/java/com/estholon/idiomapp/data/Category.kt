package com.estholon.idiomapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class Category (
    @PrimaryKey(autoGenerate = true) var id:Int,
    @ColumnInfo(name = "categories") var categories: String
    )