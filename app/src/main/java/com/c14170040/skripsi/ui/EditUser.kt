package com.c14170040.skripsi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.c14170040.skripsi.R
import com.c14170040.skripsi.typeuser
import com.c14170040.skripsi.user
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_user.*

class EditUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
    }

    override fun onStart() {
        super.onStart()
        val db=FirebaseFirestore.getInstance()
        var detuser = intent.getParcelableExtra<user>("user")
        et_editEmail.setText(detuser!!.email)
        et_editHp.setText(detuser.nohp)
        et_editNama.setText(detuser.nama)
        var type = "user"
        if(typeuser =="superadmin")
        {
            type="admin"
        }
        bt_edit.setOnClickListener {
            if(et_editPasswordadduser.text.toString()=="")
            {
                Toast.makeText(this@EditUser,"Form Belum Lengkap",Toast.LENGTH_SHORT)
            }
            else
            {

                val data = hashMapOf(
                    "email" to et_editEmail.text.toString(),
                    "password" to et_editPasswordadduser.text.toString(),
                    "nama" to et_editNama.text.toString(),
                    "nohp" to et_editHp.text.toString(),
                    "type" to type
                )
                db.collection("user").document(et_editEmail.text.toString()).set(data)
                    .addOnSuccessListener {
                        Log.d("user","edit Masuk")
                        Toast.makeText(this, "Edit Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
        }
        bt_delete.setOnClickListener {
            db.collection("user").document(detuser.email).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

    }
}