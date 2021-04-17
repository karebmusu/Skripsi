package com.c14170040.skripsi

import com.c14170040.skripsi.ui.area

class varGlobal {
    var arUser = arrayListOf<user>()
    var arArea = arrayListOf<area>()
    fun adddata(isi : user){
        arUser.add(isi)
    }
    fun addarea(isi : area){
        arArea.add(isi)
    }
    fun getdata():ArrayList<user>{
        return arUser
    }
    fun getarea():ArrayList<area>{
        return arArea
    }
    fun getsize():Int {
        return arUser.size
    }
    fun getsizeArea():Int{
        return arArea.size
    }
    fun cleardata()
    {
        arUser.clear()
    }
    fun cleararea(){
        arArea.clear()
    }


}