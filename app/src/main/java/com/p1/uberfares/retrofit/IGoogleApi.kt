package com.p1.uberfares.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleApi {
    @GET
    fun getDirection(@Url url: String?): Call<String?>?
}