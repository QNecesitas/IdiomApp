package com.estholon.idiomapp.auxiliary

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.PersistableBundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import java.util.Locale

open class TextToSpeech(private val context: Context,private val onInitCallback:
    ()-> Unit) : TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    init{
        textToSpeech = TextToSpeech(context, this)
    }


    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            val result = textToSpeech.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //Manejo de errores relacionado con idiomas
            } else {
                //El TextToSpech se realizo con exito, se puede utilizar aqui
            }
        }else{
                //Manejo de errores de initialization
            }

        }

    fun speak(text:String){
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown(){
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}

