package com.c14170040.skripsi.ui.maps

import android.content.DialogInterface
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.c14170040.skripsi.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "MapsFragment"
class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        var db = FirebaseFirestore.getInstance()
        db.collection("mesin").get().addOnSuccessListener {
            document ->
            for(doc in document){
                val dataf = doc.data as MutableMap<String,String>
                val latLng = LatLng(dataf.getValue("Latitude").toDouble(),dataf.getValue("Longitude").toDouble())
                googleMap.addMarker(MarkerOptions().position(latLng).title(dataf.getValue("Nama Mesin").toString()).snippet("Lokasi : "+dataf.getValue("Provinsi")))
            }
        }

    }
    private fun showEditDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(context).inflate(R.layout.dialog_assign,null)
        val dialog =
            context?.let {
                AlertDialog.Builder(it).setTitle("Assign Sales")
                    .setView(placeFormView)
                    .setNegativeButton("Cancel",null)
                    .setPositiveButton("OK",null)
                    .show()
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