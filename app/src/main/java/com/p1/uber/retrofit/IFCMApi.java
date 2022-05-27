package com.p1.uber.retrofit;

import com.p1.uber.modelos.FCMBody;
import com.p1.uber.modelos.FCMResponce;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAnzRSOfI:APA91bG_1njFbm_eI7rhcHFcriEEfRkyv-Uro4G8bCDPtZ8PMkH3tegsT6WcoZTbhuVw1KxwqZ2U-c1rLgoCsYV6RdeaXHFVwC7t0BFzbcZicXUFNJuK8TwY-eQoP0OnOUgloeKBAgir"
    })
    @POST("fcm/send")
    Call<FCMResponce> send(@Body FCMBody body);

}
