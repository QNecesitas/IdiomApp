package com.estholon.idiomapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.estholon.idiomapp.R
import com.estholon.idiomapp.auxiliary.TextToSpeech
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.RecyclerSentenceBinding
import java.util.Locale
import com.estholon.idiomapp.adapters.AddSentenceAdapter.*

class AddSentenceAdapter(private val context: Context, private var idioms: List<Idioms>) :
    ListAdapter<Records , AddSentenceViewHolder>(DiffCallback) {

    fun setIdiomList(newIdioms: List<Idioms>){
        idioms = newIdioms
    }

    //Speech
    private var textToSpeechEs : TextToSpeech = TextToSpeech(context, Locale("es","ES"))
    private var textToSpeechEn : TextToSpeech = TextToSpeech(context, Locale("en","US"))
    private var textToSpeechDe : TextToSpeech = TextToSpeech(context, Locale("de","DE"))
    private var textToSpeechPt : TextToSpeech = TextToSpeech(context, Locale("pt","BR"))
    private var textToSpeechFr : TextToSpeech = TextToSpeech(context, Locale("fr","FR"))

    private var textChanged:ITouchTextChanged?=null
    private var onSpinnerListener:SpinnerListener?=null
    private var clickDelete: ITouchDelete? = null


    class AddSentenceViewHolder(private var binding: RecyclerSentenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            record: Records ,
            context: Context ,
            idioms: List<Idioms> ,
            textToSpeechEs: TextToSpeech ,
            textToSpeechEn: TextToSpeech ,
            textToSpeechDe: TextToSpeech ,
            textToSpeechPt: TextToSpeech ,
            textToSpeechFr: TextToSpeech,
            textChanged: ITouchTextChanged?,
            onSpinnerListener: SpinnerListener?,
            clickDelete: ITouchDelete?

        ) {

            //Declare
            val customSpinner = binding.customSpinner
            var textToSpeech = textToSpeechEs


            binding.ivSound.setOnClickListener {
                textToSpeech.speak(binding.etSentence.text.toString())
            }
            binding.ivClose.setOnClickListener { clickDelete?.onClickDelete(record,adapterPosition) }

            val adapter = SpinnerAdapter(context , R.layout.custom_spinner , idioms)
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
            customSpinner.adapter = adapter

            customSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>? ,
                        view: View? ,
                        position: Int ,
                        id: Long
                    ) {
                        val selectedItem = customSpinner.selectedItem as Idioms


                        val selectedId = selectedItem.id
                        onSpinnerListener?.onSpinnerClick(selectedId,adapterPosition)
                        when (selectedId) {
                            "ES" -> {

                                textToSpeech = textToSpeechEs
                            }

                            "FR" -> {

                                textToSpeech = textToSpeechFr
                            }

                            "DE" -> {

                                textToSpeech = textToSpeechDe
                            }

                            "PT" -> {

                                textToSpeech = textToSpeechPt
                            }

                            "EN" -> {

                                textToSpeech = textToSpeechEn
                            }


                        }


                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}


                }

            binding.etSentence.doOnTextChanged { text, _, _, _ ->
                textChanged?.onTextChanged(text.toString(),adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): AddSentenceViewHolder {
        val viewHolder = AddSentenceViewHolder(
            RecyclerSentenceBinding.inflate(
                LayoutInflater.from(parent.context) ,
                parent ,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            viewHolder.adapterPosition
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: AddSentenceViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context,
            idioms,
            textToSpeechEs,
            textToSpeechEn,
            textToSpeechDe,
            textToSpeechPt,
            textToSpeechFr,
            textChanged,
            onSpinnerListener,
            clickDelete
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

    interface SpinnerListener {
        fun onSpinnerClick(idiom:String,position: Int)
    }

    fun setSpinnerListener(onSpinnerListener: SpinnerListener?) {
        this.onSpinnerListener = onSpinnerListener
    }

    interface ITouchTextChanged {
        fun onTextChanged(text:String,position: Int)
    }

    fun setTextChanged(textChanged: ITouchTextChanged?) {
        this.textChanged = textChanged
    }

    interface ITouchDelete {
        fun onClickDelete(record: Records,position: Int)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }


}