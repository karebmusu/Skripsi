package com.c14170040.skripsi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.StructuredQuery
import kotlinx.android.synthetic.main.activity_login.*

lateinit var sP : SharedPreferences
class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val db = FirebaseFirestore.getInstance()
        sP = getSharedPreferences("login", Context.MODE_PRIVATE)
        var keyEmail = ""
        var keyPass = ""
        if (sP.getString("keyEmail", null).toString() != null) {
            keyEmail = sP.getString("keyEmail", null).toString()
            keyPass = sP.getString("keyPass", null).toString()
            db.collection("user").document(keyEmail).get()
                    .addOnSuccessListener { doc ->
                        Log.d("logininfo", doc.toString())
                        if (doc.data != null) {
                            var data = doc.data as MutableMap<String, String>
                            var email = data.getValue("email").toString()
                            var password = data.getValue("password").toString()
                            var type = data.getValue("type").toString()
                            if (email == keyEmail && password == keyPass) {
                                Log.d("loginabc", email)
                                var pI = Intent(this@login, MainActivity::class.java)
                                pI.putExtra("namauser",data.getValue("nama"))
                                pI.putExtra("emailuser",email)
                                pI.putExtra("typeuser",type)
                                startActivity(pI)
                            }
                        }
                    }
            btLogin.setOnClickListener {
                db.collection("user").document(et_id.text.toString()).get()
                        .addOnSuccessListener { doc ->
                            Log.d("logininfo", doc.toString())
                            if (doc.data != null) {
                                var data = doc.data as MutableMap<String, String>
                                var email = data.getValue("email").toString()
                                var password = data.getValue("password").toString()
                                var type = data.getValue("type").toString()
                                Log.d("loginabc", email)
                                if (email == et_id.text.toString() && password == et_password.text.toString()) {
                                        var pI = Intent(this@login, MainActivity::class.java)
                                        val editor: SharedPreferences.Editor = sP.edit()
                                        editor.putString("keyEmail", et_id.text.toString())
                                        editor.putString("keyPass", et_password.text.toString())
                                        editor.apply()
                                        et_id.text.clear()
                                        et_password.text.clear()
                                        pI.putExtra("namauser",data.getValue("nama"))
                                        pI.putExtra("emailuser",email)
                                        pI.putExtra("typeuser",type)
                                        startActivity(pI)
                                }
                            }
                        }
                        .addOnFailureListener { Log.d("logininfo", "gagal") }
            }
        }

    }
}