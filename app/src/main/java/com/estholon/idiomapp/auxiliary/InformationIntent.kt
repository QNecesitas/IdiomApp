package com.estholon.idiomapp.auxiliary

import com.estholon.idiomapp.data.Idioms

class InformationIntent {
    companion object{
        var itemIdiomLeft = Idioms("ES","Español")
        var itemIdiomRight = Idioms("FR","Français")

        var categoriesSelectedList = mutableListOf<String>()
    }
}