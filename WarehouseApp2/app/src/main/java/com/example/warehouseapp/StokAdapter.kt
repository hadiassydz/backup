package com.example.warehouseapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StokAdapter(
    var ctx: Context,
    var resource: Int,
    var item: ArrayList<StokModel>
) : ArrayAdapter<StokModel>(ctx, resource, item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(ctx).inflate(resource, parent, false)

        val barang = view.findViewById<TextView>(R.id.txt_barang)
        val kategori = view.findViewById<TextView>(R.id.txt_kategori)
        val stokItem = item[position]

        barang.text = stokItem.Barang ?: "Nama tidak tersedia"
        kategori.text = stokItem.Kategori ?: "Kategori tidak tersedia"

        return view
    }
}