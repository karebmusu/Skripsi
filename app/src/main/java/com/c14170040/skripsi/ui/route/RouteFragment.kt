package com.c14170040.skripsi.ui.route

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c14170040.skripsi.API_KeY
import com.c14170040.skripsi.Jarak
import com.c14170040.skripsi.R
import com.c14170040.skripsi.emailuser
import com.c14170040.skripsi.ui.Direction
import com.c14170040.skripsi.ui.DistanceAPI
import com.c14170040.skripsi.ui.MapsAPI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

@Parcelize
data class Mesin(
    val latLng: LatLng,
    val namaMesin : String,
    val provinsi : String
) : Parcelable
var jarak = 0
class RouteFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        var db = FirebaseFirestore.getInstance()
        var area = arrayListOf<String>()
        var mesin = arrayListOf<Mesin>()
        db.collection("Provinsi").get().addOnSuccessListener {
            document ->
            for(doc in document){
                var dataf = doc.data as MutableMap<String,String>
                Log.d("area email", dataf.getValue("Email Sales").toString())
                if(dataf.getValue("Email Sales").toString() == emailuser)
                {
                    area.add(dataf.getValue("Nama").toString())
                    Log.d("area", dataf.getValue("Nama").toString())
                }
            }
            Log.d("area", area.toString())
            db.collection("mesin").get().addOnSuccessListener {
                docu ->
                val boundsBuilder =  LatLngBounds.Builder()
                for(ar in area){
                    Log.d("areas",ar)
                    for (docs in docu) {
                        var datae = docs.data as MutableMap<String,String>
                        if(datae.getValue("Provinsi").toString() == ar)
                        {

                            val latLng = LatLng(datae.getValue("Latitude").toDouble(),datae.getValue("Longitude").toDouble())
                            var m = Mesin(latLng,datae.getValue("Nama Mesin").toString(),datae.getValue("Provinsi").toString())
                            mesin.add(m)
                            boundsBuilder.include(latLng)
                            //googleMap.addMarker(MarkerOptions().position(latLng).title(datae.getValue("Nama").toString()).snippet("Lokasi : "+datae.getValue("Provinsi")))
                        }

                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),1000,1000,0))
                }
                val latLng = LatLng(mesin[0].latLng.latitude,mesin[0].latLng.longitude)
                val des = LatLng(mesin[1].latLng.latitude,mesin[1].latLng.longitude)
                Log.d("TesJarak",RetrofJarak(latLng,des,retrofit).toString())
                for ((i,mesinn) in mesin.withIndex())
                {
                    val latLng = LatLng(mesinn.latLng.latitude,mesin[i].latLng.longitude)
                    if(i!=mesin.size-1){
                        val des = LatLng(mesin[i+1].latLng.latitude,mesin[i+1].latLng.longitude)
                        //Retrof(latLng,des,retrofit)
                        //RetrofJarak(latLng,des)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(mesinn.namaMesin).snippet(mesinn.provinsi))
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route, container, false)
    }

    fun Retrof(origin : LatLng,dest : LatLng, retrofit: Retrofit)
    {
        var dirApi : MapsAPI = retrofit.create(MapsAPI::class.java)
        var call : Call<Direction> = dirApi.getData(origin.latitude.toString()+","+origin.longitude.toString(),dest.latitude.toString()+","+dest.longitude.toString(), API_KeY)
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
    fun RetrofJarak(origin: LatLng, dest: LatLng,retrofit: Retrofit):Int
    {

        var jarAPI : DistanceAPI =retrofit.create(DistanceAPI::class.java)
        var call : Call<Jarak> = jarAPI.getData("metric",origin.latitude.toString()+","+origin.longitude.toString(),dest.latitude.toString()+","+dest.longitude.toString(), API_KeY)
        call.enqueue(object : Callback<Jarak> {
            override fun onResponse(call: Call<Jarak>, response: retrofit2.Response<Jarak>) {
                if(response.code() != 200){
                    Log.d("status","failed")
                }
                var jar : Jarak = response.body()!!
                Log.d("statusJ",jar.status.toString())
                Log.d("Jarak", jar.rows[0]!!.elements[0]!!.distance.text)
                jarak = jar.rows[0]!!.elements[0]!!.distance.value
            }
            override fun onFailure(call: Call<Jarak>, t: Throwable) {
            }
        })
        return jarak
    }
    fun jeda() = runBlocking {
        launch {
            delay(3000L)
            Log.d("jarak","tes")
        }
    }
    private fun addPolyline(results:Direction, mMap:GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[0]!!.overviewPolyline.points)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(R.color.black))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}