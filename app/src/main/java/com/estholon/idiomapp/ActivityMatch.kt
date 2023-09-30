package com.estholon.idiomapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.MatchAdapter
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityMatchBinding
import com.estholon.idiomapp.viewmodels.MatchViewModel
import com.estholon.idiomapp.viewmodels.MatchViewModelFactory

class ActivityMatch : AppCompatActivity() {
    private lateinit var binding: ActivityMatchBinding
    private lateinit var chronometer: Chronometer

    private val viewModel:MatchViewModel by viewModels {
        MatchViewModelFactory((application as IdiomApp).database.cardDao(),(application as IdiomApp).database.record_categoriesDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterMatch: MatchAdapter

    //Finish
    private lateinit var resultGameLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("NotifyDataSetChanged")
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
            if (viewModel.listCard.value!!.isEmpty() || viewModel.listCard.value!!.size <4 ){
                binding.clListNull.visibility= View.VISIBLE
                binding.chronometer.visibility=View.GONE
            }
        }
        viewModel.matchRecord.observe(this){
            adapterMatch.submitList(it)
            adapterMatch.notifyDataSetChanged()
            if (viewModel.matchRecord.value?.isEmpty() == true){
                chronometer.stop()
                finishGame()
            }
        }
        binding.mbReturn.setOnClickListener {
            finish()
        }


        //Activate chronometer
        chronometer = binding.chronometer
        chronometer.start()

        adapterMatch.setClickSelector(object:MatchAdapter.ITouchSelector{
            override fun onClickSelector(id: Int,idIdiom:String,position:Int) {
                viewModel.getMatchSelected(id,position,idIdiom)
            }

        })


        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_match )
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


        //Result
        resultGameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                launcherReturned(result)
            }

        viewModel.getAllCardsBD(InformationIntent.itemIdiomLeft.id, InformationIntent.itemIdiomRight.id, InformationIntent.categoriesSelectedList)

    }

    private fun finishGame(){
        chronometer.stop()
        val tiempoTranscurrido = SystemClock.elapsedRealtime() - chronometer.base
        val segundosTotales = tiempoTranscurrido / 1000
        val minutos = (segundosTotales / 60).toInt()
        val segundos = (segundosTotales % 60).toInt()
        val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)

        val intent = Intent(this, ActivityResultGame::class.java)
        intent.putExtra("result_time", tiempoFormateado)
        intent.putExtra("result_errors", viewModel.error)
        resultGameLauncher.launch(intent)

    }

    private fun launcherReturned(result: ActivityResult){
        viewModel.getAllCardsBD(InformationIntent.itemIdiomLeft.id, InformationIntent.itemIdiomRight.id, InformationIntent.categoriesSelectedList)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }
}