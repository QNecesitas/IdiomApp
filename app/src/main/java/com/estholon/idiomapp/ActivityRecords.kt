package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import androidx.activity.viewModels
import com.estholon.idiomapp.adapters.SentenceAdapter
import com.estholon.idiomapp.adapters.SpinnerAdapter
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityRecordsBinding
import com.estholon.idiomapp.viewmodels.RecordViewModel
import com.estholon.idiomapp.viewmodels.RecordViewModelFactory

class ActivityRecords : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding

    //View Model
    private val viewModel: RecordViewModel by viewModels {
        RecordViewModelFactory((application as IdiomApp).database.recordsDao(),(application as IdiomApp).database.idiomsDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterSentence: SentenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterSentence = SentenceAdapter(this)
        binding.recycler.adapter = adapterSentence

        //Observe
        viewModel.listRecord.observe(this) {
            adapterSentence.submitList(it)
        }

        viewModel.listRecordFilter.observe(this) {
            adapterSentence.submitList(it)
        }

        //SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                viewModel.filterByText(newText.toString())

                return false
            }

        })

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

                val selectedItem = spinnerPersonalizado.selectedItem as Idioms

                binding.search.setQuery("",false)
                // Accede al ID del objeto seleccionado
                val selectedId = selectedItem.id
                if(selectedId=="ALL"){
                    viewModel.getAllRecord()
                }else{
                    viewModel.getFilterRecord(selectedId)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Cuando no se selecciona nada en el Spinner
            }
        }

        binding.addRecord.setOnClickListener {
            val intent=Intent(this,ActivityNewRecords::class.java)
            startActivity(intent)
        }


        viewModel.getAllIdioms()

        viewModel.getAllRecord()


    }
}