package com.estholon.idiomapp.auxiliary

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatCallback
import com.estholon.idiomapp.R
import java.util.Locale

open class TextToSpeech(
    private val context: Context, private val idiomLocale: Locale, private val onInitCallback:
        () -> Unit
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var isAvailable = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(idiomLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                isAvailable = false
            }
        } else {
            isAvailable = false
        }

    }

    fun setSpeechRate(rate: Double) {
        if (isAvailable) {
            textToSpeech.setSpeechRate(rate.toFloat())
        } else {
            // Maneja el caso en el que la síntesis de voz no está disponible
        }
    }

    fun speak(text: String) {
        if (isAvailable) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Toast.makeText(
                context,
                context.getString(
                    R.string.su_dispositivo_no_permite_este_lenguaje,
                    idiomLocale.displayName
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}

