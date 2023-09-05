package com.p1.uberfares.provides

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.p1.uberfares.R
import com.p1.uberfares.retrofit.IGoogleApi
import com.p1.uberfares.retrofit.RetrofitClient.getClient
import retrofit2.Call
import java.util.Date

class GoogleApiProvider(private val context: Context) {
    fun getDirections(originLatLng: LatLng, destinationLatLng: LatLng): Call<String?>? {
        val baseUrl = "https://maps.googleapis.com"
        val query =
            ("/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                    + "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&"
                    + "destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&"
                    + "departure_time=" + (Date().time + 60 * 60 * 1000) + "&"
                    + "traffic_model=best_guess&"
                    + "key=" + context.resources.getString(R.string.google_maps_key))
        return getClient(baseUrl).create(IGoogleApi::class.java).getDirection(baseUrl + query)
    }
}