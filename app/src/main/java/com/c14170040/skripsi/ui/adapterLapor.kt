package com.c14170040.skripsi.ui

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.c14170040.skripsi.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.itemarea.view.*
import kotlinx.android.synthetic.main.itemlapor.view.*

@Parcelize
data class laporan(
    val namasales : String,
    val emailsales : String,
    val mesin : String,
    val jenismesin : String,
    val catatan : String,
    val tanggal : String,
    val linkfoto : String,
    val pemilik : String
) : Parcelable
class adapterLapor(private val listLapor: ArrayList<laporan>) : RecyclerView.Adapter<adapterLapor.ListViewHolder>() {
    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var sMesinTgl : TextView = itemView.tvTglMesin
        var sSales : TextView = itemView.tvPelapor
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterLapor.ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.itemlapor,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: adapterLapor.ListViewHolder, position: Int) {
        var llapor = listLapor[position]
        holder.sMesinTgl.setText(llapor.tanggal+"-"+llapor.mesin)
        holder.sSales.setText(llapor.namasales+"-"+llapor.emailsales)
        holder.sMesinTgl.setOnClickListener {
            val pI = Intent(holder.itemView.context,detailLaporan::class.java)
            pI.putExtra("laporan",llapor)
            startActivity(holder.itemView.context,pI,null)
        }
    }

    override fun getItemCount(): Int {
        return listLapor.size
    }


}