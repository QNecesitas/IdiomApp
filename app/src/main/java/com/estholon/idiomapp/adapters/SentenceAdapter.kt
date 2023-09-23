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

class SentenceAdapter(private val context: Context) :
    ListAdapter<Records , SentenceViewHolder>(DiffCallback) {

    private var clickSound: ITouchSound? = null


    class SentenceViewHolder(private var binding: RecyclerGetSentencesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            record: Records ,
            context: Context ,
            clickSound: ITouchSound?
        ) {

            //Declare
            val sentence = record.sentence
            when (record.idIdiom) {
                "ES" -> binding.ivImage.setImageDrawable(
                    AppCompatResources.getDrawable(context , R.drawable.spain)
                )

                "FR" -> binding.ivImage.setImageDrawable(
                    AppCompatResources.getDrawable(context , R.drawable.france)
                )

                "DE" -> binding.ivImage.setImageDrawable(
                    AppCompatResources.getDrawable(context , R.drawable.germany)
                )

                "PT" -> binding.ivImage.setImageDrawable(
                    AppCompatResources.getDrawable(context , R.drawable.portugal)
                )

                "EN" -> binding.ivImage.setImageDrawable(
                    AppCompatResources.getDrawable(context , R.drawable.unite)
                )


            }




            binding.tvSentence.text = sentence
            binding.ivSound.setOnClickListener { clickSound?.onClickSound(record) }

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
            context ,
            clickSound
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

    interface ITouchSound {
        fun onClickSound(record: Records)
    }

    fun setClickSound(clickSound: ITouchSound?) {
        this.clickSound = clickSound
    }


}