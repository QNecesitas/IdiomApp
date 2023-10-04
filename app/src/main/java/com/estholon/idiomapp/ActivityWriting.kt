package com.estholon.idiomapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.estholon.idiomapp.auxiliary.InformationIntent
import com.estholon.idiomapp.auxiliary.TextToSpeech
import com.estholon.idiomapp.databinding.ActivityWritingBinding
import com.estholon.idiomapp.viewmodels.WritingViewModel
import com.estholon.idiomapp.viewmodels.WritingViewModelFactory
import java.util.Locale

class ActivityWriting : AppCompatActivity() {
    private lateinit var binding: ActivityWritingBinding
    
    //Speech
    private lateinit var textToSpeechEs:TextToSpeech
    private lateinit var textToSpeechEn:TextToSpeech
    private lateinit var textToSpeechDe:TextToSpeech
    private lateinit var textToSpeechPt:TextToSpeech
    private  lateinit var textToSpeechFr:TextToSpeech
    private lateinit var textToSpeech:TextToSpeech
    private lateinit var textToSpeech1: TextToSpeech



    //viewModel
    private val viewModel: WritingViewModel by viewModels{
        WritingViewModelFactory((application as IdiomApp).database.cardDao(),(application as IdiomApp).database.recordCategoriesDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Speech
        textToSpeechEs= TextToSpeech(this, Locale("es","ES"))
        textToSpeechEn= TextToSpeech(this, Locale("en","US"))
        textToSpeechDe= TextToSpeech(this, Locale("de","DE"))
        textToSpeechPt= TextToSpeech(this, Locale("pt","BR"))
        textToSpeechFr= TextToSpeech(this, Locale("fr","FR"))
        textToSpeech=textToSpeechEs
        textToSpeech1=textToSpeechEs
        textToSpeech=textToSpeechEs


        //Observe
        viewModel.writingCardSelected.observe(this){
            fillOutCard()
        }

        viewModel.listWritingCard.observe(this){
            if (viewModel.listWritingCard.value?.isEmpty() == true){
                binding.clListNull.visibility= View.VISIBLE
            }
        }


        //Listeners
        binding.mbReturn.setOnClickListener {
            finish()
        }

        binding.ivSound.setOnClickListener {
            viewModel.writingCardSelected.value?.get(0)
                ?.let { it1 -> textToSpeech.speak(it1.sentence) }
        }

        binding.ivSound1.setOnClickListener {
            viewModel.writingCardSelected.value?.get(0)
                ?.let { it1 -> textToSpeech1.speak(it1.translation) }
        }

        binding.next.setOnClickListener {
            nextCard()
        }

        binding.last.setOnClickListener {
            lastCard()
        }

        binding.btnCheck.setOnClickListener {
            checkExercise()
        }

        binding.btnResolve.setOnClickListener {
            resolveExercise()
        }



        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_writing)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityWriting, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityWriting, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityWriting, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityWriting, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityWriting, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        viewModel.getAllCardsBD(
            InformationIntent.itemIdiomLeft.id,
            InformationIntent.itemIdiomRight.id,
            InformationIntent.categoriesSelectedList
        )


    }

    private fun fillOutCard(){
        val image=viewModel.writingCardSelected.value?.get(0)?.image
        val imageUri= Uri.parse(image)


        Glide.with(this)
            .load(imageUri)
            .error(R.drawable.outline_image_24)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivAddimage)


        if (InformationIntent.itemIdiomLeft.id==viewModel.writingCardSelected.value?.get(0)?.idiomSentence) {

            when (viewModel.writingCardSelected.value?.get(0)?.idiomSentence) {
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

            when (viewModel.writingCardSelected.value?.get(0)?.idiomTranslation) {
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

            binding.tvSentence.text = viewModel.writingCardSelected.value?.get(0)?.sentence

        }else{

            when (viewModel.writingCardSelected.value?.get(0)?.idiomTranslation) {
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

            when (viewModel.writingCardSelected.value?.get(0)?.idiomSentence) {
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

            binding.tvSentence.text = viewModel.writingCardSelected.value?.get(0)?.translation
        }

    }

    private fun nextCard(){
        binding.etResponse.setText("")
        binding.CLGradeResponse.visibility = View.GONE
        binding.btnResolve.visibility = View.VISIBLE
        binding.btnCheck.visibility = View.VISIBLE
        viewModel.nextCard()
    }

    private fun lastCard(){
        binding.etResponse.setText("")
        binding.CLGradeResponse.visibility = View.GONE
        binding.btnResolve.visibility = View.VISIBLE
        binding.btnCheck.visibility = View.VISIBLE
        viewModel.lastCard()
    }



    //Logic Game
    private fun checkExercise(){
        val response = if (InformationIntent.itemIdiomLeft.id==viewModel.writingCardSelected.value?.get(0)?.idiomSentence) {
            viewModel.writingCardSelected.value?.get(0)?.translation
        }else{
            viewModel.writingCardSelected.value?.get(0)?.sentence
        }

        binding.CLGradeResponse.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(this,R.anim.slide_up)
        binding.CLGradeResponse.startAnimation(anim)

        if(binding.etResponse.text.toString().trim().equals( response?.trim(),true)){
            viewModel.rights ++
            binding.CLGradeResponse.background = AppCompatResources.getDrawable(this,R.drawable.backg_grade_right)
            binding.exclamation.text = getString(R.string.felicidades)
            binding.responseIs.text = getString(R.string.La_respuesta_es_correcta)
            binding.errorRights.text = getString(R.string.Errores_aciertos,viewModel.errors,viewModel.rights)
            binding.solveElevate.visibility = View.GONE
            binding.btnResolve.visibility = View.GONE
            binding.btnCheck.visibility = View.GONE
        }else{
            viewModel.errors ++
            binding.CLGradeResponse.background = AppCompatResources.getDrawable(this,R.drawable.backg_grade_wrong)
            binding.exclamation.text = getString(R.string.ups)
            binding.responseIs.text = getString(R.string.La_respuesta_es_incorrecta)
            binding.errorRights.text = getString(R.string.Errores_aciertos,viewModel.errors,viewModel.rights)
            binding.solveElevate.visibility = View.VISIBLE
            binding.solveElevate.setOnClickListener { resolveExercise() }
            binding.btnResolve.visibility = View.GONE
            binding.btnCheck.visibility = View.GONE
        }
    }

    private fun resolveExercise(){
        val response = if (InformationIntent.itemIdiomLeft.id==viewModel.writingCardSelected.value?.get(0)?.idiomSentence) {
            viewModel.writingCardSelected.value?.get(0)?.translation
        }else{
            viewModel.writingCardSelected.value?.get(0)?.sentence
        }

        binding.etResponse.setText(response)
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