package com.c14170040.skripsi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.c14170040.skripsi.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_laporan.*

class detailLaporan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)
        val db= FirebaseFirestore.getInstance()
        var detlapor = intent.getParcelableExtra<laporan>("laporan")
        Glide.with(this).load(detlapor!!.linkfoto).into(imageView2)
        tvNamaDetail.setText(detlapor.mesin+"\nJenis Mesin : "+detlapor.jenismesin)
        tvDeskripsiDetail.setText("Pemilik : "+detlapor.pemilik+"\nPetugas : "+detlapor.namasales+"\nEmail Petugas : "+detlapor.emailsales+"\nTanggal Pelaporan : "+detlapor.tanggal+"\nCatatan : "+detlapor.catatan)
    }
}