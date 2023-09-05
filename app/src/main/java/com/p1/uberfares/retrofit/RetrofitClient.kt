package com.p1.uberfares.retrofit

import com.p1.uberfares.modelos.Client
import com.p1.uberfares.modelos.Drivee
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    val Users = "Users"
    val Drivers = "Drivers"
    val Clients = "Clients"
    lateinit var riderKey : String
    lateinit var currentDrive : Drivee
    lateinit var currentClient : Client




    // private static Retrofit retrofit = null;
    @JvmStatic
    fun getClient(url: String?): Retrofit {
        //if (retrofit == null){
        // }
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @JvmStatic
    fun getClientObject(url: String?): Retrofit {
        //if (retrofit == null){
        // }
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}