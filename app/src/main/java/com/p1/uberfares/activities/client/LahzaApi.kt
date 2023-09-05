package com.p1.uberfares.activities.client

import retrofit2.Call
import retrofit2.http.*

interface LahzaApi {
    @FormUrlEncoded
    @POST("transaction/initialize")
    fun initializeTransaction(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("amount") amount: Double
    ): Call<ApiResponse> // تعويض ApiResponse بالموديل الخاص بالاستجابة المتوقعة من Lahza API
}
