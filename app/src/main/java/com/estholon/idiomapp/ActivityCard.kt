package com.estholon.idiomapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.auxiliary.TextToSpeech
import com.estholon.idiomapp.databinding.ActivityCardBinding
import com.estholon.idiomapp.viewmodels.CardViewModel
import com.estholon.idiomapp.viewmodels.CardViewModelFactory
import java.util.Locale

class ActivityCard : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding

    //Speech
    private lateinit var textToSpeechEs:TextToSpeech
    private lateinit var textToSpeechEn:TextToSpeech
    private lateinit var textToSpeechDe:TextToSpeech
    private lateinit var textToSpeechPt:TextToSpeech
    private  lateinit var textToSpeechFr:TextToSpeech
    private lateinit var textToSpeech:TextToSpeech
    private lateinit var textToSpeech1: TextToSpeech



    //viewModel
    private val viewModel:CardViewModel by viewModels{
        CardViewModelFactory((application as IdiomApp).database.cardDao(),(application as IdiomApp).database.record_categoriesDao())
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Speech
        textToSpeechEs= TextToSpeech(this, Locale("es","ES")){}
        textToSpeechEn= TextToSpeech(this, Locale("en","US")){}
        textToSpeechDe= TextToSpeech(this, Locale("de","DE")){}
        textToSpeechPt= TextToSpeech(this, Locale("pt","BR")){}
        textToSpeechFr= TextToSpeech(this, Locale("fr","FR")){}
        textToSpeech=textToSpeechEs
        textToSpeech1=textToSpeechEs

        textToSpeech=textToSpeechEs


        //Observe
        viewModel.cardSelected.observe(this){
            fillOutCard()
        }

        viewModel.listCard.observe(this){
            if (viewModel.listCard.value!!.isEmpty()){
                binding.clListNull.visibility=View.VISIBLE
        }
        }

        viewModel.state.observe(this){
            when(it){
                CardViewModel.StateTime.START -> binding.cvAddImage.visibility=View.VISIBLE
                CardViewModel.StateTime.FIRST_WORLD -> binding.clSentence.visibility=View.VISIBLE
                CardViewModel.StateTime.SECOND_WORLD -> binding.clSentence1.visibility=View.VISIBLE
                CardViewModel.StateTime.FINISH ->{
                    binding.cvAddImage.visibility=View.INVISIBLE
                    binding.clSentence.visibility=View.INVISIBLE
                    binding.clSentence1.visibility=View.INVISIBLE
                    viewModel.nextCard()
                    viewModel.timeToShow()
                }
            }
        }
        viewModel.speed.observe(this){
            binding.tvSpeed.text = "${viewModel.speed.value}" + "x"
        }

        binding.mbReturn.setOnClickListener {
            finish()
        }
        binding.ivSound.setOnClickListener {
            viewModel.cardSelected.value?.get(0)
                ?.let { it1 -> textToSpeech.speak(it1.sentence) }
        }
        binding.ivSound1.setOnClickListener {
            viewModel.cardSelected.value?.get(0)
                ?.let { it1 -> textToSpeech1.speak(it1.translation) }
        }
        binding.next.setOnClickListener {
            nextCard()
        }
        binding.last.setOnClickListener {
            lastCard()
        }
        binding.play.setOnClickListener {
            if(viewModel.playStop.value == true){
                viewModel.timeToShow()
            }
            viewModel.timeToShow()
        }
        binding.tvSpeed.setOnClickListener {
            viewModel.speed()
        }




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
                    val intent = Intent(this@ActivityCard, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityCard, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityCard, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityCard, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityCard, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        viewModel.getAllCardsBD(InformationIntent.itemIdiomLeft.id,InformationIntent.itemIdiomRight.id,InformationIntent.categoriesSelectedList)


    }
    private fun fillOutCard(){
        val image=viewModel.cardSelected.value?.get(0)?.image
        val imageUri=Uri.parse(image)
        binding.ivAddimage.setImageURI(imageUri)
        if (InformationIntent.itemIdiomLeft.id==viewModel.cardSelected.value?.get(0)?.idiomSentence) {
            when (viewModel.cardSelected.value?.get(0)?.idiomSentence) {
                "ES" -> {

                    textToSpeech = textToSpeechEs
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.spain)
                    )
                }

                "FR" -> {

                    textToSpeech = textToSpeechFr
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.france)
                    )
                }

                "DE" -> {

                    textToSpeech = textToSpeechDe
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.germany)
                    )
                }

                "PT" -> {

                    textToSpeech = textToSpeechPt
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.portugal)
                    )
                }

                "EN" -> {

                    textToSpeech = textToSpeechEn
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.unite)
                    )
                }


            }

            when (viewModel.cardSelected.value?.get(0)?.idiomTranslation) {
                "ES" -> {

                    textToSpeech1 = textToSpeechEs
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.spain)
                    )
                }

                "FR" -> {

                    textToSpeech1 = textToSpeechFr
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.france)
                    )
                }

                "DE" -> {

                    textToSpeech1 = textToSpeechDe
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.germany)
                    )
                }

                "PT" -> {

                    textToSpeech1 = textToSpeechPt
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.portugal)
                    )
                }

                "EN" -> {

                    textToSpeech1 = textToSpeechEn
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.unite)
                    )
                }


            }

            binding.tvSentence.text = viewModel.cardSelected.value?.get(0)?.sentence
            binding.tvSentence1.text = viewModel.cardSelected.value?.get(0)?.translation
        }else{
            when (viewModel.cardSelected.value?.get(0)?.idiomTranslation) {
                "ES" -> {

                    textToSpeech = textToSpeechEs
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.spain)
                    )
                }

                "FR" -> {

                    textToSpeech = textToSpeechFr
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.france)
                    )
                }

                "DE" -> {

                    textToSpeech = textToSpeechDe
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.germany)
                    )
                }

                "PT" -> {

                    textToSpeech = textToSpeechPt
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.portugal)
                    )
                }

                "EN" -> {

                    textToSpeech = textToSpeechEn
                    binding.ivIdiom.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.unite)
                    )
                }


            }

            when (viewModel.cardSelected.value?.get(0)?.idiomSentence) {
                "ES" -> {

                    textToSpeech1 = textToSpeechEs
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.spain)
                    )
                }

                "FR" -> {

                    textToSpeech1 = textToSpeechFr
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.france)
                    )
                }

                "DE" -> {

                    textToSpeech1 = textToSpeechDe
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.germany)
                    )
                }

                "PT" -> {

                    textToSpeech1 = textToSpeechPt
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.portugal)
                    )
                }

                "EN" -> {

                    textToSpeech1 = textToSpeechEn
                    binding.ivIdiom1.setImageDrawable(
                        AppCompatResources.getDrawable(this , R.drawable.unite)
                    )
                }


            }

            binding.tvSentence.text = viewModel.cardSelected.value?.get(0)?.translation
            binding.tvSentence1.text = viewModel.cardSelected.value?.get(0)?.sentence
        }

    }
    fun nextCard(){
        viewModel.nextCard()
    }

    fun lastCard(){
        viewModel.lastCard()
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