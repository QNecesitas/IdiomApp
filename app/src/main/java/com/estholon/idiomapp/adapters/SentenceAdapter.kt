package com.estholon.idiomapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.estholon.idiomapp.R
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.RecyclerGetSentencesBinding
import com.estholon.idiomapp.adapters.SentenceAdapter.*
import com.estholon.idiomapp.auxiliary.TextToSpeech
import java.util.Locale

class SentenceAdapter(private val context: Context) :
    ListAdapter<Records , SentenceViewHolder>(DiffCallback) {

    //Speech
    private var textToSpeechEs : TextToSpeech = TextToSpeech(context, Locale("es","ES")){}
    private var textToSpeechEn : TextToSpeech = TextToSpeech(context, Locale("en","US")){}
    private var textToSpeechDe : TextToSpeech = TextToSpeech(context, Locale("de","DE")){}
    private var textToSpeechPt : TextToSpeech = TextToSpeech(context, Locale("pt","BR")){}
    private var textToSpeechFr : TextToSpeech = TextToSpeech(context, Locale("fr","FR")){}

    class SentenceViewHolder(private var binding: RecyclerGetSentencesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            record: Records ,
            context: Context,
            textToSpeechEs: TextToSpeech,
            textToSpeechEn: TextToSpeech,
            textToSpeechDe: TextToSpeech,
            textToSpeechPt: TextToSpeech,
            textToSpeechFr: TextToSpeech
        )   {

            //Declare
            val sentence = record.sentence
            var textToSpeech = textToSpeechEs
            when (record.idIdiom) {
                "ES" -> {
                    binding.ivImage.setImageDrawable(
                        AppCompatResources.getDrawable(context , R.drawable.spain)
                    )
                    textToSpeech = textToSpeechEs
                }

                "FR" -> {
                    binding.ivImage.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.france)
                    )
                    textToSpeech = textToSpeechFr
                }

                "DE" -> {
                    binding.ivImage.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.germany)
                    )
                    textToSpeech = textToSpeechDe
                }

                "PT" -> {
                    binding.ivImage.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.portugal)
                    )
                    textToSpeech = textToSpeechPt
                }

                "EN" -> {
                    binding.ivImage.setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.unite)
                    )
                    textToSpeech = textToSpeechEn
                }


            }



            binding.ivSound.setOnClickListener {
                textToSpeech.speak(record.sentence)
            }

            binding.tvSentence.text = sentence

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): SentenceViewHolder {
        val viewHolder = SentenceViewHolder(
            RecyclerGetSentencesBinding.inflate(
                LayoutInflater.from(parent.context) ,
                parent ,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: SentenceViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context,
            textToSpeechEs,
            textToSpeechEn,
            textToSpeechDe,
            textToSpeechPt,
            textToSpeechFr
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Records>() {
            override fun areItemsTheSame(oldItem: Records , newItem: Records): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Records , newItem: Records): Boolean {
                return oldItem == newItem
            }

        }
    }


}