package com.c14170040.skripsi.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c14170040.skripsi.*
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

const val API_Key = "AIzaSyDOi70He9zLBusrKm1j6RIhu50_cZ5yr00"
class AssignActivity : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var emailspinner = arrayListOf<String>()
        var isispinner = arrayListOf<String>()
        var db = FirebaseFirestore.getInstance()
        db.collection("user").get()
            .addOnSuccessListener { documents->
                for(doc in documents)
                {
                    var datau = doc.data as MutableMap<String, String>
                    val data = user(
                            datau.getValue("nama"),
                            datau.getValue("email"),
                            datau.getValue("nohp")
                    )
                    if(typeuser=="superadmin")
                    {
                        if(datau.getValue("type")=="admin")
                        {
                            isispinner.add(datau.getValue("nama"))
                            emailspinner.add(datau.getValue("email"))
                        }
                    }
                    else
                    {
                        if(datau.getValue("type")!="superadmin" && datau.getValue("type")!="admin")
                        {
                            if(datau.getValue("supervisor").toString() == namauser)
                            {
                                isispinner.add(datau.getValue("nama"))
                                emailspinner.add(datau.getValue("email"))
                            }
                        }
                    }


                }
                spinner_sales.adapter=ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, isispinner)
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
            if(typeuser=="superadmin")
            {
                val data = hashMapOf(
                        "Nama" to intent.getStringExtra("namaArea").toString(),
                        "Nama Sales" to "",
                        "Email Sales" to "",
                        "Supervisor" to spinner_sales.selectedItem
                )
                db.collection("Provinsi").document(intent.getStringExtra("namaArea").toString()).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Assign Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
            else
            {
                val data = hashMapOf(
                        "Nama" to intent.getStringExtra("namaArea").toString(),
                        "Nama Sales" to spinner_sales.selectedItem,
                        "Email Sales" to emailspinner[count],
                        "Supervisor" to namauser
                )
                db.collection("Provinsi").document(intent.getStringExtra("namaArea").toString()).set(data)
                    .addOnSuccessListener {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "message/rfc822"
                        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailspinner[count]))
                        i.putExtra(Intent.EXTRA_SUBJECT, "Penempatan")
                        i.putExtra(Intent.EXTRA_TEXT, "Anda ditugaskan di "+intent.getStringExtra("namaArea").toString())
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."))
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this, "Assign Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
        }
    }
    fun Retrof(origin: LatLng, dest: LatLng)
    {
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        var dirApi : MapsAPI = retrofit.create(MapsAPI::class.java)
        var call : Call<Direction> = dirApi.getData(origin.latitude.toString() + "," + origin.longitude.toString(), dest.latitude.toString() + "," + dest.longitude.toString(), API_Key)

        call.enqueue(object : Callback<Direction> {
            override fun onResponse(call: Call<Direction>, response: retrofit2.Response<Direction>) {
                if (response.code() != 200) {
                    Log.d("status", "failed")
                    return
                }
                var dir: Direction = response.body()!!
                Log.d("status", dir.status)
                addPolyline(dir, mMap)
            }

            override fun onFailure(call: Call<Direction>, t: Throwable) {

            }
        })
    }
    private fun addPolyline(results: Direction, mMap: GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[0]!!.overviewPolyline.points)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(R.color.black))
    }

    override fun onStart() {
        super.onStart()
        var detarea= intent.getStringExtra("namaArea").toString()
        tv_area.setText(detarea)
    }


}