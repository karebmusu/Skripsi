package com.c14170040.skripsi.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.c14170040.skripsi.R
import com.c14170040.skripsi.namauser
import com.c14170040.skripsi.typeuser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_slideshow.*

class SlideshowFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_add.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            var type = "user"
            if(typeuser=="superadmin")
            {
                type="admin"
            }
            val data = hashMapOf(
                "email" to et_email.text.toString(),
                "password" to et_passwordadduser.text.toString(),
                "nama" to et_nama.text.toString(),
                "nohp" to et_hp.text.toString(),
                "type" to type,
                "supervisor" to namauser
            )
            db.collection("user").document(et_email.text.toString()).set(data)
                .addOnSuccessListener {
                    Log.d("user","Regis Masuk")
                    Toast.makeText(context, "Register Success", Toast.LENGTH_SHORT).show()
                    et_email.text.clear()
                    et_passwordadduser.text.clear()
                    et_nama.text.clear()
                    et_hp.text.clear()
                }
        }
    }
}