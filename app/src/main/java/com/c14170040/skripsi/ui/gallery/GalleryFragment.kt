package com.c14170040.skripsi.ui.gallery

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.c14170040.skripsi.R
import com.c14170040.skripsi.ui.adapterArea
import com.c14170040.skripsi.ui.area
import com.c14170040.skripsi.user
import com.c14170040.skripsi.varGlobal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_assign.*
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment(), adapterArea.RecyclerViewClickListener {
    lateinit var show : ArrayList<area>
    var vGlobal = varGlobal()
    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : adapterArea
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        db.collection("Provinsi")
                .get()
                .addOnSuccessListener {
                    documents->
                    vGlobal.cleararea()
                    var jumlahmesin=0
                    var count=0
                    for(doc in documents)
                    {
                        var dataArea = doc.data as MutableMap<String,String>
                        db.collection("mesin")
                                .get()
                                .addOnSuccessListener {
                                    document->
                                    for(doz in document)
                                    {
                                        var datam = doz.data as MutableMap<String,String>
                                        if(dataArea.getValue("Nama").toString() == datam.getValue("Provinsi").toString())
                                        {
                                            count++
                                        }
                                    }
                                    Log.d("Mesin",dataArea.getValue("Nama")+" "+count.toString())
                                    jumlahmesin=count
                                    var data = area(
                                            dataArea.getValue("Nama"),
                                            dataArea.getValue("Nama Sales"),
                                            dataArea.getValue("Email Sales"),
                                            jumlahmesin
                                    )
                                    Log.d("jumlahluar","test")
                                    vGlobal.addarea(data)
                                    show = vGlobal.getarea()
                                    ShowData()
                                    adapter.listener=this
                                    count=0
                                }
                    }

                }
        super.onViewCreated(view, savedInstanceState)
    }
    private fun ShowData(){
        rv_area.layoutManager = LinearLayoutManager(this.context)
        rv_area.addItemDecoration(
                DividerItemDecoration(
                        activity,
                        DividerItemDecoration.VERTICAL
                )
        )
        adapter = adapterArea(show)
        rv_area.adapter=adapter
    }

    private fun showAlertDialog(darae : area)
    {
        val placeFormView = LayoutInflater.from(this.context).inflate(R.layout.dialog_assign,null)
        val dialog =
            AlertDialog.Builder(this.context).setTitle("Assign Sales")
                .setView(placeFormView)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK",null)
                .show()
        var emailspinner = arrayListOf<String>()
        var isispinner = arrayListOf<String>()
        db.collection("user").get()
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
                        isispinner.add(datau.getValue("nama"))
                        emailspinner.add(datau.getValue("email"))
                    }
                }
            }
        dialog.spin_sales.adapter = ArrayAdapter(activity?.applicationContext!!, R.layout.spinneritem,isispinner)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
//            var isi = hashMapOf(
//                "Nama" to darae.nama,
//                "Nama Sales" to namasales,
//                "Email Sales" to emailsales
//            )
//            db.collection("Provinsi").document(darae.nama).set(isi)
//                .addOnSuccessListener {
//                    Toast.makeText(context,"Berhasil Assign",Toast.LENGTH_SHORT).show()
//                }
            Log.d("assign",dialog.spin_sales.selectedItem.toString())
            dialog.dismiss()
        }

    }
    override fun buttontap(view: View, datArea: area) {

        showAlertDialog(datArea)
    }
}




