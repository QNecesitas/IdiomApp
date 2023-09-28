package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.estholon.idiomapp.databinding.ActivityResultGameBinding

class ActivityResultGame : AppCompatActivity() {

    private lateinit var binding: ActivityResultGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val time =  intent.getStringExtra("result_time")
        val errors =  intent.getIntExtra("result_errors",0)

        //Fill info
        binding.tvTiempo.text = getString(R.string.El_tiempo_ha_sido,time)
        binding.tvErrors.text = getString(R.string.Los_errores_han_sido,errors)

        //OnCLick
        binding.btnIrAlMenu.setOnClickListener {
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
        }

        binding.btnRepetir.setOnClickListener {
            finish()
        }


    }
}