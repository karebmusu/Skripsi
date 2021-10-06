package com.c14170040.skripsi

import com.c14170040.skripsi.ui.area
import com.c14170040.skripsi.ui.laporan

class varGlobal {
    var arUser = arrayListOf<user>()
    var arLapor = arrayListOf<laporan>()
    var arArea = arrayListOf<area>()
    fun adddata(isi : user){
        arUser.add(isi)
    }
    fun addarea(isi : area){
        arArea.add(isi)
    }
    fun addlapor(isi : laporan)
    {
        arLapor.add(isi)
    }
    fun getdata():ArrayList<user>{
        return arUser
    }
    fun getarea():ArrayList<area>{
        return arArea
    }
    fun getlapor():ArrayList<laporan>{
        return arLapor
    }
    fun getsize():Int {
        return arUser.size
    }
    fun getsizeArea():Int{
        return arArea.size
    }
    fun getsizeLapor():Int{
        return arLapor.size
    }
    fun cleardata()
    {
        arUser.clear()
    }
    fun cleararea(){
        arArea.clear()
    }
    fun clearlapor(){
        arLapor.clear()
    }


}