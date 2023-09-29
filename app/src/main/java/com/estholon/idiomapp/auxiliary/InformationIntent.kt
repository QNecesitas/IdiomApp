package com.estholon.idiomapp.auxiliary

import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms

class InformationIntent {
    companion object{
        var itemIdiomLeft = Idioms("ES","Español")
        var itemIdiomRight = Idioms("EN","English")

        var categoriesSelectedList = mutableListOf<Category>()
    }
}