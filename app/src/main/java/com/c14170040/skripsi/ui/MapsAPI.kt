package com.c14170040.skripsi.ui

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsAPI {
    @GET("maps/api/directions/json")
    fun getData(@Query("origin") strOrigin:String, @Query("destination") strDest:String, @Query("key") apikey:String) : Call<Direction>
}