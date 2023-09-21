package com.estholon.idiomapp.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["idRecord","idCategories"],
    foreignKeys = [
        ForeignKey(
            entity = Records::class,
            parentColumns = ["id"],
            childColumns = ["idRecord"],
            onDelete = ForeignKey.CASCADE
        ),
    ForeignKey(
        entity = Categories::class,
        parentColumns = ["id"],
        childColumns = ["idCategories"],
        onDelete = ForeignKey.CASCADE
    )
    ]
)
data class Record_Categories(
    var idRecord:Int,
    var idCategories:Int
)
