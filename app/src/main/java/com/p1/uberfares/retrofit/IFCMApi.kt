package com.p1.uberfares.retrofit

import com.p1.uberfares.modelos.FCMBody
import com.p1.uberfares.modelos.FCMResponce
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMApi {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAc7WIMBE:APA91bE45DC4PIHmUayqa16nqED03Q9Kj7D7KvRWzktyyBpYQC1_fuJ49MOXrc0IyEsLAbjyIY60Slo0G7Lq7I3kxLG5PmEVBcH7hvHklfI8RhNTG5b6j3nPxgraCGMFgZAbqTTood9c"
    )
    @POST("fcm/send")
    fun send(@Body body: FCMBody?): Call<FCMResponce?>?
}