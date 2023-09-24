package com.estholon.idiomapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.auxiliary.TextToSpeech
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.databinding.ActivityHomeBinding
import com.estholon.idiomapp.databinding.LiAddCategoryBinding
import com.estholon.idiomapp.viewmodels.HomeViewModel
import com.estholon.idiomapp.viewmodels.HomeViewModelFactory
import com.google.android.material.chip.Chip

class ActivityHome : AppCompatActivity() {


    private lateinit var binding : ActivityHomeBinding

    private var li_category_binding: LiAddCategoryBinding? = null

    private lateinit var textToSpeech: TextToSpeech

    //View Model
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as IdiomApp).database.categoriesDao(),
            (application as IdiomApp).database.idiomsDao())
    }

    //Spinner
    private var spinnerLIsOpen = false

    private var spinnerRIsOpen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        textToSpeech = TextToSpeech(this){}

        binding.btnRegistro.setOnClickListener{
            val textToRead = "Gregor no quitas mijo, mama huevo"
            textToSpeech.speak(textToRead)
            val intent=Intent(this,ActivityRecords::class.java)
            startActivity(intent)
        }

        //Observe
        viewModel.listCategory.observe(this) {
            refreshChipGroup(it)
        }


        binding.tvIntroCate.setOnClickListener{
            li_add_category()
        }



        viewModel.listIdiomsLeft.observe(this) { items ->
            val adapter = SpinnerAdapter(this , R.layout.custom_spinner , items)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            binding.spinnerIdiom1.adapter = adapter
            adapter.setClick(object : SpinnerAdapter.ITouch{
                override fun onClick(idioms: Idioms) {
                    if(idioms != viewModel.itemIdiomLeft){
                        viewModel.selectedIdiomLeft(idioms)
                    }
                }

            })
        }

        viewModel.listIdiomsRight.observe(this) { items ->
            val adapter = SpinnerAdapter(this , R.layout.custom_spinner , items)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            binding.spinnerIdiom2.adapter = adapter
            adapter.setClick(object : SpinnerAdapter.ITouch{
                override fun onClick(idioms: Idioms) {
                    if(idioms !=viewModel.itemIdiomRight){
                        viewModel.selectedIdiomRight(idioms)
                    }
                }

            })
        }


        viewModel.getAllIdioms()
        viewModel.refresh()
    }




    fun li_add_category(){
        val inflater = LayoutInflater.from(binding.root.context)
        li_category_binding = LiAddCategoryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_category_binding!!.root)
        val alertDialog = builder.create()

        viewModel.refresh()

        li_category_binding!!.ivAdd.setOnClickListener{
            if(li_category_binding!!.tiet.text.toString().isNotEmpty()){
                li_category_binding!!.tiet.setText("")
                viewModel.addCategory(li_category_binding!!.tiet.text.toString())
            }
        }

        li_category_binding!!.ivClose.setOnClickListener{
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }


    fun refreshChipGroup(list : MutableList<Category>){
        li_category_binding?.chipGroup?.removeAllViews()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener{
                alert_borrar(chip)
            }
            li_category_binding?.chipGroup?.addView(chip)
        }
    }

    private fun alert_borrar(chip: Chip) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Borrar))
        builder.setMessage(R.string.al_borrar)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Aceptar) { dialog , _ ->
            //finish the activity
            borrar_chip(chip)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.Cancelar){ dialog , _ ->
            //finish the activity
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    private fun borrar_chip(chip: Chip){
        li_category_binding?.chipGroup?.removeView(chip)
    }



}