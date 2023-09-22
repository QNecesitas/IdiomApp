package com.estholon.idiomapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory
import kotlin.math.log

class ActivityNewRecords : AppCompatActivity() {

    private lateinit var binding:ActivityNewRecordsBinding


    //View Model
    private val viewModel: NewRecordViewModel by viewModels {
        NewRecordViewModelFactory((application as IdiomApp).database.idiomsDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val spinnerPersonalizado=binding.spinnerPersonalizado

        viewModel.listIdioms.observe(this) { items ->
            // Actualiza el adaptador del Spinner cuando cambian los elementos
            val adapter = SpinnerAdapter(this , R.layout.custom_spinner , items)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            spinnerPersonalizado.adapter = adapter

        }


        spinnerPersonalizado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Maneja la selección del elemento del Spinner aquí
                // Haz lo que necesites con el elemento seleccionado
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Cuando no se selecciona nada en el Spinner
            }
        }


        viewModel.getAllIdioms()
    }



}