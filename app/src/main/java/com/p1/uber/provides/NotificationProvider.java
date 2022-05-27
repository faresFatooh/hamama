package com.p1.uber.provides;

import com.p1.uber.modelos.FCMBody;
import com.p1.uber.modelos.FCMResponce;
import com.p1.uber.retrofit.IFCMApi;
import com.p1.uber.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";



    public NotificationProvider() {

    }

    public Call<FCMResponce> sendNotification(FCMBody body){
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
