package com.c14170040.skripsi.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.c14170040.skripsi.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.itemarea.view.*
import kotlinx.android.synthetic.main.itemuser.view.*

@Parcelize
data class area(
        val nama : String,
        val sales : String,
        val emailsales : String,
        val jumlahmesin : Int
) : Parcelable

class adapterArea(private val listArea : ArrayList<area>) : RecyclerView.Adapter<adapterArea.ListViewHolder>() {
    var listener:RecyclerViewClickListener?=null
    interface RecyclerViewClickListener{
        fun buttontap(view:View,datArea:area)
    }
    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var sNama : TextView = itemView.tv_namaarea
        var sEmailSales : TextView = itemView.tv_emailsales
        var sSales : TextView = itemView.tv_sales
        var sJumlahMesin : TextView = itemView.tv_jumlahmesin
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.itemarea,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var larea = listArea[position]
        holder.sNama.setText(larea.nama)
        holder.sJumlahMesin.setText("Jumlah Mesin : "+larea.jumlahmesin.toString())
        holder.sSales.setText("Sales : "+larea.sales)
        holder.sEmailSales.setText(larea.emailsales)
        holder.sNama.setOnClickListener{
            listener?.buttontap(it,larea)
        }
    }

    override fun getItemCount(): Int {
        return listArea.size
    }
}