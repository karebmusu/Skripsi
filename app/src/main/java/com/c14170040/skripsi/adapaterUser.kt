package com.c14170040.skripsi

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.itemuser.view.*

@Parcelize
data class user(
    val nama : String,
    val email : String,
    val nohp : String
) : Parcelable

class adapaterUser(private val listUser : ArrayList<user>) : RecyclerView.Adapter<adapaterUser.ListViewHolder>() {
    var listener:RecylerViewClickListener?=null
    interface RecylerViewClickListener{
        fun buttonKlik(view: View, datUser: user)
    }
    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var sNama : TextView = itemView.tv_nama
        var sEmail : TextView = itemView.tv_email
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapaterUser.ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.itemuser,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: adapaterUser.ListViewHolder, position: Int) {
        var luser = listUser[position]
        holder.sEmail.setText(luser.email)
        holder.sNama.setText(luser.nama)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

}