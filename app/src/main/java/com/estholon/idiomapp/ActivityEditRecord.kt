package com.estholon.idiomapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.AddSentenceAdapter
import com.estholon.idiomapp.adapters.EditSentenceAdapter
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityEditRecordBinding
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.viewmodels.EditRecordViewModel
import com.estholon.idiomapp.viewmodels.EditRecordViewModelFactory
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory
import com.google.android.material.chip.Chip

class ActivityEditRecord : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecordBinding

    //View Model
    private val viewModel: EditRecordViewModel by viewModels {
        EditRecordViewModelFactory((application as IdiomApp).database.recordsDao(),(application as IdiomApp).database.translationsDao(),(application as IdiomApp).database.record_categoriesDao(),(application as IdiomApp).database.idiomsDao(),(application as IdiomApp).database.categoriesDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterSentence: EditSentenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get id from ActivityRecord
        val idRecord=intent.getIntExtra("idRecord",0)

        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_new_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        //Recycler
        binding.rvLanguage.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterSentence = EditSentenceAdapter(this , mutableListOf())
        binding.rvLanguage.adapter = adapterSentence

        //Observe
        viewModel.listRecords.observe(this) {
            val image=viewModel.listRecords.value?.get(0)?.image
            val imageUri= Uri.parse(image)
            binding.ivAddimage.setImageURI(imageUri)
            viewModel.listIdioms.value?.let { it1 -> adapterSentence.setIdiomList(it1.toList() as MutableList<Idioms>) }
            adapterSentence.submitList(it)

        }

        viewModel.listCategorySelected.observe(this){
            otherRefreshChipGroup(it)
        }



        viewModel.listIdioms.observe(this){
            viewModel.getRecord(idRecord)
        }

        viewModel.getAllIdioms()
    }
    private fun otherRefreshChipGroup(list : MutableList<Category>){
        binding.chipGroup.removeAllViews()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener{
                viewModel.deleteCategory(e.id)
            }

            binding.chipGroup.addView(chip)

        }
    }
}