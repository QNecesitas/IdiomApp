package com.estholon.idiomapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.databinding.ActivityHomeBinding
import com.estholon.idiomapp.databinding.LiAllCategoryBinding
import com.estholon.idiomapp.viewmodels.HomeViewModel
import com.estholon.idiomapp.viewmodels.HomeViewModelFactory
import com.google.android.material.chip.Chip

class ActivityHome : AppCompatActivity() {


    private lateinit var binding : ActivityHomeBinding

    private var liCategoryBinding: LiAllCategoryBinding? = null

    //View Model
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as IdiomApp).database.categoriesDao(),
            (application as IdiomApp).database.idiomsDao(),
            (application as IdiomApp).database.recordCategoriesDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        //Adapter
        val adapterLeft = SpinnerAdapter(this , R.layout.custom_spinner, mutableListOf())
        adapterLeft.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        binding.spinnerIdiom1.adapter = adapterLeft
        val adapterRight = SpinnerAdapter(this , R.layout.custom_spinner , mutableListOf())
        adapterRight.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        binding.spinnerIdiom2.adapter = adapterRight



        //Adapter Listeners
        binding.spinnerIdiom1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.selectedIdiomLeft(adapterLeft.objects[p2],p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.spinnerIdiom2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.selectedIdiomRight(adapterRight.objects[p2],p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }



        //Listeners
        binding.tvIntroCate.setOnClickListener{
            liAllCategory()
        }

        binding.btnRegistro.setOnClickListener{
            val intent = Intent(this@ActivityHome , ActivityRecords::class.java)
            startActivity(intent)
        }

        binding.btnMatch.setOnClickListener {
            val intent = Intent(this@ActivityHome , ActivityMatch::class.java)
            startActivity(intent)
        }

        binding.btnWriting.setOnClickListener {
            val intent = Intent(this@ActivityHome , ActivityWriting::class.java)
            startActivity(intent)
        }

        binding.btnCards.setOnClickListener {
            val intent = Intent(this@ActivityHome , ActivityCard::class.java)
            startActivity(intent)
        }



        //Observe
        viewModel.listCategory.observe(this) {
            refreshChipGroupInflate(it)
        }

        viewModel.listIdiomsLeft.observe(this) { items ->
            adapterLeft.clear()
            adapterLeft.addAll(items)
            adapterLeft.notifyDataSetChanged()
        }

        viewModel.listIdiomsRight.observe(this) { items ->
            adapterRight.clear()
            adapterRight.addAll(items)
            adapterRight.notifyDataSetChanged()
        }


        viewModel.getAllIdioms()

    }



    //Categories
    private fun liAllCategory(){
        val inflater = LayoutInflater.from(binding.root.context)
        liCategoryBinding = LiAllCategoryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        if(liCategoryBinding != null) {
            builder.setView(liCategoryBinding!!.root)
        }
        val alertDialog = builder.create()

        viewModel.refreshCategories()

        if(liCategoryBinding!=null) {
            liCategoryBinding?.ivClose?.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }

    private fun refreshChipGroupInflate(list : MutableList<Category>){
        liCategoryBinding?.chipGroupCategories?.removeAllViews()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            if(InformationIntent.categoriesSelectedList.contains(e)){
                chip.isChecked = true
            }
            chip.setOnCloseIconClickListener{
                alertDeleteInflate(chip, e.id)
            }
            chip.setOnCheckedChangeListener { _, b ->
                if(b){
                    InformationIntent.categoriesSelectedList.add(e)
                    refreshChipGroupStatic()
                }else{
                    InformationIntent.categoriesSelectedList.remove(e)
                    refreshChipGroupStatic()
                }
            }
            liCategoryBinding?.chipGroupCategories?.addView(chip)
        }
    }

    private fun alertDeleteInflate(chip: Chip, id: Int) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Borrar))
        builder.setMessage(R.string.al_borrar)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Aceptar) { dialog , _ ->
            deleteChipInflate(chip, id)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.Cancelar){ dialog , _ ->
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    private fun deleteChipInflate(chip: Chip, id:Int){
        liCategoryBinding?.chipGroupCategories?.removeView(chip)
        InformationIntent.categoriesSelectedList.removeIf {it.id == id}
        viewModel.deleteCategoryRecord(id)
        viewModel.deleteCategories(id)
    }

    private fun refreshChipGroupStatic(){
        binding.chipGroupCategories.removeAllViews()
        for (e in InformationIntent.categoriesSelectedList){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = false
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener{
                InformationIntent.categoriesSelectedList.remove(e)
                refreshChipGroupStatic()
            }

            binding.chipGroupCategories.addView(chip)

        }
    }


    //Exit app
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showAlertDialogExit()
    }

    private fun showAlertDialogExit() {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(R.string.salir)
        builder.setMessage(R.string.seguro_desea_salir)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Si) { _, _ ->
            //finish the activity
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        builder.setNegativeButton(R.string.No) { dialog, _ ->
            //dialog gone
            dialog.dismiss()
        }
        //create the alert dialog and show it
        builder.create().show()
    }

}