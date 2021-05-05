package com.c14170040.skripsi.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.c14170040.skripsi.R
import com.c14170040.skripsi.user

import com.c14170040.skripsi.varGlobal
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.activity_assign.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
const val API_Key = "AIzaSyBm-XqprlC4FyC3SIIyA3AB2nBXv9B8pWw"
class AssignActivity : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var emailspinner = arrayListOf<String>()
        var isispinner = arrayListOf<String>()
        var db = FirebaseFirestore.getInstance()
        db.collection("user").get()
            .addOnSuccessListener {
                    documents->
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
                spinner_sales.adapter=ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,isispinner)
            }
        setContentView(R.layout.activity_assign)
        btassign.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            var count = 0
            for(isi in isispinner)
            {
                if(isi == spinner_sales.selectedItem.toString())
                {
                    Log.d("indexspin", count.toString())
                    break
                }
                count++
            }
            val data = hashMapOf(
                "Nama" to intent.getStringExtra("namaArea").toString(),
                "Nama Sales" to spinner_sales.selectedItem,
                "Email Sales" to emailspinner[count]
            )
            db.collection("Provinsi").document(intent.getStringExtra("namaArea").toString()).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Assign Success", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

    }
    fun Retrof(origin : LatLng, dest : LatLng)
    {
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        var dirApi : MapsAPI = retrofit.create(MapsAPI::class.java)
        var call : Call<Direction> = dirApi.getData(origin.latitude.toString()+","+origin.longitude.toString(),dest.latitude.toString()+","+dest.longitude.toString(), API_Key)

        call.enqueue(object : Callback<Direction> {
            override fun onResponse(call: Call<Direction>, response: retrofit2.Response<Direction>) {
                if(response.code() != 200){
                    Log.d("status","failed")
                    return
                }
                var dir : Direction = response.body()!!
                Log.d("status",dir.status)
                addPolyline(dir,mMap)
            }
            override fun onFailure(call: Call<Direction>, t: Throwable) {

            }
        })
    }
    private fun addPolyline(results:Direction, mMap:GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[0]!!.overviewPolyline.points)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(R.color.black))
    }

    override fun onStart() {
        super.onStart()
        var detarea= intent.getStringExtra("namaArea").toString()
        tv_area.setText(detarea)
    }


}