package com.c14170040.skripsi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.c14170040.skripsi.R
import com.c14170040.skripsi.adapaterUser
import com.c14170040.skripsi.user
import com.c14170040.skripsi.varGlobal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), adapaterUser.RecylerViewClickListener {
    lateinit var tampil : ArrayList<user>
    var vGlobal = varGlobal()
    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : adapaterUser
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        db.collection("user")
            .get()
            .addOnSuccessListener {
                documents->
                vGlobal.cleardata()
                for(doc in documents)
                {
                    var datau = doc.data as MutableMap<String,String>
                    val data = user(
                        datau.getValue("nama"),
                        datau.getValue("email"),
                        datau.getValue("nohp")
                    )
                    if(datau.getValue("type")!="admin")
                    {
                        vGlobal.adddata(data)
                    }
                }
                tampil = vGlobal.getdata()
                Showdata()
                adapter.listener=this
            }
        super.onViewCreated(view, savedInstanceState)
    }
    private fun Showdata(){
        rvUser.layoutManager = LinearLayoutManager(this.context)
        rvUser.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = adapaterUser(tampil)
        rvUser.adapter = adapter
    }

    override fun buttonKlik(view: View, datUser: user) {
//        TODO("Not yet implemented")
    }
}