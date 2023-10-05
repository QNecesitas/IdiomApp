package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.SentenceAdapter
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityRecordsBinding
import com.estholon.idiomapp.viewmodels.RecordViewModel
import com.estholon.idiomapp.viewmodels.RecordViewModelFactory

class ActivityRecords : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding
    private lateinit var newRecordLauncher: ActivityResultLauncher<Intent>
    private lateinit var editRecordLauncher: ActivityResultLauncher<Intent>


    //View Model
    private val viewModel: RecordViewModel by viewModels {
        RecordViewModelFactory(
            (application as IdiomApp).database.recordsDao(),
            (application as IdiomApp).database.idiomsDao(),
            (application as IdiomApp).database.translationsDao(),
        )
    }



    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterSentence: SentenceAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityRecords, ActivityRecords::class.java)
                    startActivity(intent)
                }
                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityRecords, ActivityNewRecords::class.java)
                    startActivity(intent)
                }
                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityRecords, ActivityCard::class.java)
                    startActivity(intent)
                }
                R.id.menu_match -> {
                    val intent = Intent(this@ActivityRecords, ActivityMatch::class.java)
                    startActivity(intent)
                }
                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityRecords, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        //Recycler
        binding.recycler.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterSentence = SentenceAdapter(this)
        adapterSentence.setOnClickLister(object: SentenceAdapter.OnCLickListener{
            override fun onClick(record: Records) {
                val intent = Intent(this@ActivityRecords, ActivityEditRecord::class.java)
                intent.putExtra("idRecord",record.id)
                editRecordLauncher.launch(intent)
            }

        })
        binding.recycler.adapter = adapterSentence



        //Observers
        viewModel.listRecord.observe(this) {
            adapterSentence.submitList(it)
        }

        viewModel.listRecordFilter.observe(this) {
            adapterSentence.submitList(it)
        }



        //SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener ,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                viewModel.filterByText(newText.toString())

                return false
            }

        })



        //Spinner
        val customSpinner = binding.customSpinner
        viewModel.listIdioms.observe(this) { items ->
            // Updating the adapter
            val adapter = SpinnerAdapter(this , R.layout.custom_spinner , items)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            customSpinner.adapter = adapter

        }
        customSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>? ,
                view: View? ,
                position: Int ,
                id: Long
            ) {

                val selectedItem = customSpinner.selectedItem as Idioms

                binding.search.setQuery("",false)
                val selectedId = selectedItem.id
                if(selectedId=="ALL"){
                    viewModel.getAllRecord()
                }else{
                    viewModel.getFilterRecord(selectedId)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        //Listeners
        binding.addRecord.setOnClickListener {
            val intent=Intent(this,ActivityNewRecords::class.java)
            newRecordLauncher.launch(intent)
        }



        //Thread
        newRecordLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                recordReturn()
            }

        editRecordLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                recordReturn()
            }

        viewModel.getAllIdioms()

        viewModel.getAllRecord()


    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            finish()
        }
    }

    private fun recordReturn() {
        viewModel.getAllIdioms()
        viewModel.getAllRecord()
    }

}