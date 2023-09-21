package com.estholon.idiomapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.estholon.idiomapp.adapters.SentenceAdapter
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.databinding.ActivityRecordsBinding
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory
import com.estholon.idiomapp.viewmodels.RecordViewModel
import com.estholon.idiomapp.viewmodels.RecordViewModelFactory

class ActivityRecords : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding

    //View Model
    private val viewModel: RecordViewModel by viewModels {
        RecordViewModelFactory((application as IdiomApp).database.recordsDao())
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

        viewModel.getAllRecord()
    }
}