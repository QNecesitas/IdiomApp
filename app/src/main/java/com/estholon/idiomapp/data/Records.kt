package com.estholon.idiomapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Idioms::class,
            parentColumns = ["id"],
            childColumns = ["idIdiom"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
 data class Records (
    @PrimaryKey(autoGenerate = true) var id:Int,
    @ColumnInfo(name = "sentence") var sentence:String,
    @ColumnInfo(name = "image") var image:String,
    @ColumnInfo(name = "idIdiom") var idIdiom:String
        )