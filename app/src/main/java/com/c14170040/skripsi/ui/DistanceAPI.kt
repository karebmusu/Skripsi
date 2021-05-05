package com.c14170040.skripsi.ui

import com.c14170040.skripsi.Jarak
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceAPI {
    @GET("maps/api/distancematrix/json")
    fun getData(@Query("unit") strUnit:String,@Query("origins") strOrigin:String, @Query("destinations") strDest:String, @Query("key") apikey:String) : Call<Jarak>
}