package com.estholon.idiomapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.estholon.idiomapp.databinding.ActivityCardsBinding
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding

class ActivityCards : AppCompatActivity() {

    private lateinit var binding: ActivityCardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_cards)
    }
}