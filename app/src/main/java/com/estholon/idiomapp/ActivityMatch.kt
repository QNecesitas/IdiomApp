package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.MatchAdapter
import com.estholon.idiomapp.adapters.SentenceAdapter
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityMatchBinding
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.viewmodels.MatchViewModel
import com.estholon.idiomapp.viewmodels.MatchViewModelFactory

class ActivityMatch : AppCompatActivity() {
    private lateinit var binding: ActivityMatchBinding
    private lateinit var chronometer: Chronometer

    private val viewModel:MatchViewModel by viewModels() {
        MatchViewModelFactory((application as IdiomApp).database.cardDao(),(application as IdiomApp).database.record_categoriesDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterMatch: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterMatch = MatchAdapter(this)
        binding.recycler.adapter = adapterMatch


         //Observes
        viewModel.listCard.observe(this){
            if (viewModel.listCard.value!!.isEmpty() || viewModel.listCard.value!!.size <=4 ){
                binding.clListNull.visibility= View.VISIBLE
                binding.chronometer.visibility=View.GONE
            }
        }
        viewModel.matchRecord.observe(this){
            Log.e("XXX","${viewModel.matchRecord.value}")
            adapterMatch.submitList(it)
        }



        chronometer=binding.chronometer
        binding.chronometer.setOnClickListener {
            chronometer.start()
        }



        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_new_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityMatch, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityMatch, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityMatch, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityMatch, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityMatch, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }



        viewModel.getAllCardsBD(InformationIntent.itemIdiomLeft.id, InformationIntent.itemIdiomRight.id, InformationIntent.categoriesSelectedList)

    }

    //TODO Hay que hacer que al finalizar se reincicie el progreso de ResultGame


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish()
        }
    }
}