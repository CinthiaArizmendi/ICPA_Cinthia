package com.example.icpa_cinthia

import MisOficios
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AdapterSpinnercopia(c: Context, l: ArrayList<MisOficios>): BaseAdapter() {

    var contexto = c
    var lista = l

    override fun getCount(): Int {
        return lista.size
    }

    override fun getItem(position: Int): Any {
        return lista[position].nombreOfi
    }

    override fun getItemId(position: Int): Long {
        return 0

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        var layout = LayoutInflater.from(contexto)
        view = layout.inflate(R.layout.spinner_s, null)

        var txt = view.findViewById<TextView>(R.id.tv_oficio)
        txt.text = lista[position].nombreOfi

        return view

    }

}