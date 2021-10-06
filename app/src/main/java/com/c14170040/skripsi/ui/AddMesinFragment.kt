package com.c14170040.skripsi.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.c14170040.skripsi.R
import com.c14170040.skripsi.listarea
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_mesin.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMesinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMesinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_mesin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner.adapter=ArrayAdapter(activity?.applicationContext!!,R.layout.support_simple_spinner_dropdown_item, listarea)
        bt_addMesin.setOnClickListener {
            var db = FirebaseFirestore.getInstance()
            var data = hashMapOf<String,String>(
                "Kunjungan" to "",
                "Latitude" to etLat.text.toString(),
                "Longitude" to etLong.text.toString(),
                "Nama Mesin" to etNamaMesin.text.toString(),
                "Provinsi" to spinner.selectedItem.toString(),
                "Status" to "belum"
            )
            db.collection("mesin").document(etNamaMesin.text.toString())
                .set(data).addOnSuccessListener {
                    Toast.makeText(context,"Add Mesin Success", Toast.LENGTH_SHORT).show()
                    etLat.text.clear()
                    etLong.text.clear()
                    etNamaMesin.text.clear()
                }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddMesinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AddMesinFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}