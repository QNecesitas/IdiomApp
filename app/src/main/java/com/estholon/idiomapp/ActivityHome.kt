package com.estholon.idiomapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.databinding.ActivityHomeBinding
import com.estholon.idiomapp.databinding.LiAddCategoryBinding
import com.estholon.idiomapp.viewmodels.HomeViewModel
import com.estholon.idiomapp.viewmodels.HomeViewModelFactory
import com.google.android.material.chip.Chip

class ActivityHome : AppCompatActivity() {


    private lateinit var binding : ActivityHomeBinding

    private var li_category_binding: LiAddCategoryBinding? = null

    //View Model
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as IdiomApp).database.categoriesDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }


        //Observe
        viewModel.listCategory.observe(this) {
            refreshChipGroup(it)
        }


        binding.tvIntroCate.setOnClickListener{
            li_add_category()
        }

        binding.ivPais1.setOnClickListener{

        }

        binding.ivPais2.setOnClickListener{

        }

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