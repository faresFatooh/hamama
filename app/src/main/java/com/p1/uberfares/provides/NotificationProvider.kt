package com.p1.uberfares.provides

import com.p1.uberfares.modelos.FCMBody
import com.p1.uberfares.modelos.FCMResponce
import com.p1.uberfares.retrofit.IFCMApi
import com.p1.uberfares.retrofit.RetrofitClient.getClientObject
import retrofit2.Call

class NotificationProvider {
    private val url = "https://fcm.googleapis.com"
    fun sendNotification(body: FCMBody?): Call<FCMResponce?>? {
        return getClientObject(url).create(IFCMApi::class.java).send(body)
    }
}