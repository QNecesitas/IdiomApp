package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory

class ActivityNewRecords : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecordsBinding


    //View Model
    private val viewModel: NewRecordViewModel by viewModels {
        NewRecordViewModelFactory((application as IdiomApp).database.idiomsDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_new_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityRecords::class.java)
                    startActivity(intent)
                }
                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityNewRecords::class.java)
                    startActivity(intent)
                }
                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityCards::class.java)
                    startActivity(intent)
                }
                R.id.menu_match -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityMatch::class.java)
                    startActivity(intent)
                }
                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        //Spinner Logic
        val spinnerPersonalizado = binding.spinnerPersonalizado
        viewModel.listIdioms.observe(this) { items ->
            // Actualiza el adaptador del Spinner cuando cambian los elementos
            val adapter = SpinnerAdapter(this , R.layout.custom_spinner , items)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            spinnerPersonalizado.adapter = adapter

        }
        spinnerPersonalizado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>? ,
                view: View? ,
                position: Int ,
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


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            finish()
        }
    }

}