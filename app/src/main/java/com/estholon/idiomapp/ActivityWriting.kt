package com.estholon.idiomapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.estholon.idiomapp.databinding.ActivityCardsBinding
import com.estholon.idiomapp.databinding.ActivityWritingBinding

class ActivityWriting : AppCompatActivity() {
    private lateinit var binding: ActivityWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_writing)
    }
}