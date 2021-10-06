package com.c14170040.skripsi.ui.maps

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.c14170040.skripsi.R
import com.c14170040.skripsi.namauser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_assign.view.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MapsFragment"
class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        var db = FirebaseFirestore.getInstance()
        db.collection("Provinsi").get().addOnSuccessListener { doz ->
            for(dozz in doz)
            {
                val dataP = dozz.data as MutableMap<String, String>
                if(dataP.getValue("Supervisor").toString() == namauser)
                {
                    db.collection("mesin").get().addOnSuccessListener { document ->
                        val boundsBuilder =  LatLngBounds.Builder()
                        for(doc in document){
                            val dataf = doc.data as MutableMap<String, String>
                            if(dataf.getValue("Provinsi").toString() == dataP.getValue("Nama").toString())
                            {
                                val latLng = LatLng(dataf.getValue("Latitude").toDouble(), dataf.getValue("Longitude").toDouble())
                                boundsBuilder.include(latLng)
                                if(dataf.getValue("Kunjungan") != "")
                                {
                                    val myFormat = "dd-MM-yyy"
                                    val getTgl = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
                                    val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                    var tanggal = sdf1.parse(dataf.getValue("Kunjungan"))
                                    getTgl.add(Calendar.MONTH,-3)
                                    var tglKunjungan = Calendar.getInstance()
                                    tglKunjungan.time=tanggal
                                    if(tglKunjungan.timeInMillis <= getTgl.timeInMillis)
                                    {
                                        val datam = mapOf<String,String>("Status" to "belum")
                                        db.collection("mesin").document(dataf.getValue("Nama Mesin").toString()).update(datam)
                                        Log.d("kunjungan",dataf.getValue("Nama Mesin").toString()+"berhasil")
                                    }

                                }
                                googleMap.addMarker(MarkerOptions().position(latLng).title(dataf.getValue("Nama Mesin").toString()).snippet("Lokasi : " + dataf.getValue("Provinsi") + "\nStatus : " + dataf.getValue("Status")))
                            }
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100, 100, 0))
                    }
                }
            }
        }

        googleMap.setOnInfoWindowClickListener { marker ->
            val placeFormView = LayoutInflater.from(this.context).inflate(R.layout.dialog_reset, null)
            placeFormView.tvDialog.setText(marker.snippet)
            val dialog = android.app.AlertDialog.Builder(this.context).setTitle(marker.title)
                    .setView(placeFormView)
                    .setPositiveButton("Reset Status", null)
                    .setNegativeButton("Cancel", null)
                    .show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val data = mapOf<String, String>("Status" to "belum")
                db.collection("mesin").document(marker.title.toString()).update(data)
                    .addOnSuccessListener {
                        Toast.makeText(this.context, "Berhasil ditandai menjadi selesai", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}