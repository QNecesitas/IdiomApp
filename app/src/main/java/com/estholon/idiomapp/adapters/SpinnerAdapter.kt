package com.estholon.idiomapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.estholon.idiomapp.R
import com.estholon.idiomapp.data.Idioms

class SpinnerAdapter(context: Context, resource: Int,val objects: List<Idioms>) :
    ArrayAdapter<Idioms>(context, resource, objects) {

    @SuppressLint("ViewHolder" , "MissingInflatedId" , "UseCompatLoadingForDrawables")
    override fun getView(position: Int , convertView: View? , parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.custom_spinner, parent, false)
        val imageView = vista.findViewById<ImageView>(R.id.iv_idioms)

        imageView.setImageDrawable(context.getDrawable(getImage(position)))

        return vista
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDropDownView(position: Int , convertView: View? , parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.custom_spinner_dropdown, parent, false)
        val imageView = vista.findViewById<ImageView>(R.id.imageView)
        imageView.setImageDrawable(context.getDrawable(getImage(position)))

        return vista
    }

    private fun getImage(position: Int):Int{
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
            "ALL" ->{
                return R.drawable.worldall
            }
            else ->{
                return R.drawable.spain
            }
        }
    }



}