package com.estholon.idiomapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.estholon.idiomapp.databinding.ActivityMatchBinding
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding

class ActivityMatch : AppCompatActivity() {
    private lateinit var binding: ActivityMatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_match)
    }
}