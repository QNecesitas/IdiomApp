package com.estholon.idiomapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.estholon.idiomapp.R
import com.estholon.idiomapp.auxiliary.TextToSpeech
import com.estholon.idiomapp.data.CardMatch
import com.estholon.idiomapp.databinding.RecyclerMatchBinding
import java.util.Locale

class MatchAdapter(private val context: Context) :
    ListAdapter<CardMatch , MatchAdapter.MatchViewHolder>(DiffCallback) {
    //Speech
    private var textToSpeechEs : TextToSpeech = TextToSpeech(context, Locale("es","ES")){}
    private var textToSpeechEn : TextToSpeech = TextToSpeech(context, Locale("en","US")){}
    private var textToSpeechDe : TextToSpeech = TextToSpeech(context, Locale("de","DE")){}
    private var textToSpeechPt : TextToSpeech = TextToSpeech(context, Locale("pt","BR")){}
    private var textToSpeechFr : TextToSpeech = TextToSpeech(context, Locale("fr","FR")){}

    private var clickSelector: ITouchSelector? = null

    class MatchViewHolder(private var binding: RecyclerMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            record: CardMatch ,
            context: Context ,
            textToSpeechEs: TextToSpeech ,
            textToSpeechEn: TextToSpeech ,
            textToSpeechDe: TextToSpeech ,
            textToSpeechPt: TextToSpeech ,
            textToSpeechFr: TextToSpeech,
            clickSelector: ITouchSelector?
        ) {

            //Declare
            val sentence = record.sentence
            var textToSpeech = textToSpeechEs


            binding.root.setOnClickListener {
                clickSelector?.onClickSelector(record.id,record.idIdiom,adapterPosition)
            }

            when(record.state){
                "Nada" ->{
                    binding.viewselect.visibility=View.GONE
                    binding.viewselectedBad.visibility=View.GONE
                    binding.viewselectedCorrect.visibility=View.GONE
                }
                "Seleccionado" ->{
                    binding.viewselect.visibility=View.VISIBLE
                    binding.viewselectedCorrect.visibility=View.GONE
                    binding.viewselectedBad.visibility=View.GONE
                }
                "Mal" ->{
                    binding.viewselectedBad.visibility=View.VISIBLE
                    binding.viewselect.visibility=View.GONE
                    binding.viewselectedCorrect.visibility=View.GONE
                }
                "Correcto" ->{
                    binding.viewselectedCorrect.visibility= View.VISIBLE
                    binding.viewselect.visibility=View.GONE
                    binding.viewselectedBad.visibility=View.GONE
                }
            }


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

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MatchViewHolder {
        val viewHolder = MatchViewHolder(
            RecyclerMatchBinding.inflate(
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


    override fun onBindViewHolder(holder: MatchViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context,
            textToSpeechEs,
            textToSpeechEn,
            textToSpeechDe,
            textToSpeechPt,
            textToSpeechFr,
            clickSelector
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CardMatch>() {
            override fun areItemsTheSame(oldItem: CardMatch , newItem: CardMatch): Boolean {
                return oldItem.id == newItem.id && oldItem.state == newItem.state
            }

            override fun areContentsTheSame(oldItem: CardMatch , newItem: CardMatch): Boolean {
                return  oldItem.id == newItem.id && oldItem.state == newItem.state
            }

        }
    }

    interface ITouchSelector {
        fun onClickSelector(id:Int,idIdiom:String,position: Int)
    }

    fun setClickSelector(clickSelector: ITouchSelector?) {
        this.clickSelector = clickSelector
    }



}