package com.estholon.idiomapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.estholon.idiomapp.R
import com.estholon.idiomapp.data.Idioms

class SpinnerAdapter(context: Context, resource: Int, objects: List<Idioms>) :
    ArrayAdapter<Idioms>(context, resource, objects) {

    @SuppressLint("ViewHolder" , "MissingInflatedId" , "UseCompatLoadingForDrawables")
    override fun getView(position: Int , convertView: View? , parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.custom_spinner, parent, false)
        val imageView = vista.findViewById<ImageView>(R.id.iv_idioms)

        imageView.setImageDrawable(context.getDrawable(getImage(position))) // Assume que 'image' es un recurso drawable



        return vista
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDropDownView(position: Int , convertView: View? , parent: ViewGroup): View {
        // Infla tu diseño personalizado que contiene una ImageView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.custom_spinner_dropdown, parent, false)
        val imageView = vista.findViewById<ImageView>(R.id.imageView)
        imageView.setImageDrawable(context.getDrawable(getImage(position))) // Assume que 'image' es un recurso drawable



        return vista
    }

    fun getImage(position: Int):Int{
        when(getItem(position)!!.id){
            "ES" ->{
                return R.drawable.spain
            }
            "FR" ->{
                return R.drawable.france
            }
            "DE" ->{
                return R.drawable.germany
            }
            "PT" ->{
                return R.drawable.portugal
            }
            "EN" ->{
                return R.drawable.unite
            }
            else ->{
                return R.drawable.spain
            }
        }
    }

}