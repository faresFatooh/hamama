package com.p1.uberfares.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.p1.uberfares.R


class TripLogActivity : AppCompatActivity() {
    private lateinit var directionsApiClient: GeoApiContext
    private lateinit var googleMap: GoogleMap
    private lateinit var directionsResult: DirectionsResult


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_log)


        var calificationClient = intent.getStringExtra("calificationClient").toString()
        var calificationDriver = intent.getStringExtra("calificationDriver")
        var destination = intent.getStringExtra("destination")
        var destinationLat = intent.getStringExtra("destinationLat")
        var destinationLng = intent.getStringExtra("destinationLng")
        var km = intent.getStringExtra("km")
        var origin = intent.getStringExtra("origin")
        var originLat = intent.getStringExtra("originLat")
        var originLng = intent.getStringExtra("originLng")
        var price = intent.getStringExtra("price")
        var status = intent.getStringExtra("status")
        var timestamp = intent.getStringExtra("timestamp")
        val pathPoints = mutableListOf<LatLng>()
        val originLatLng = LatLng(originLat!!.toDouble(), originLng!!.toDouble())
        pathPoints.add(originLatLng)
        findViewById<TextView>(R.id.textViewDistance).text = timestamp.toString() + " min"
        findViewById<TextView>(R.id.textViewPrice).text = price.toString() + " ₪"
        findViewById<TextView>(R.id.textViewTimestamp).text = km.toString() + " km"
        findViewById<TextView>(R.id.textViewOrigin).text = origin.toString()
        findViewById<TextView>(R.id.textViewDestination).text = destination.toString()
        findViewById<RatingBar>(R.id.ratingBar).rating = calificationClient.toFloat()
//        val mapFragment =
//            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
//        mapFragment.getMapAsync { map ->
//            googleMap = map
//            // تهيئة مفتاح API الخاص بك
//            directionsApiClient = GeoApiContext.Builder()
//                .apiKey("AIzaSyCsXYZw3o-s2V3dVrdzFBF_7gM1rJzEoi4")
//                .build()
//
//            // تحديد النقاط البداية والنهاية للمسار
//            val originLatLng =
//                com.google.maps.model.LatLng(originLat!!.toDouble(), originLng!!.toDouble())
//            val destinationLatLng = com.google.maps.model.LatLng(
//                destinationLat!!.toDouble(),
//                destinationLng!!.toDouble()
//            )
//            if (destinationLatLng.lat != 0.0 && destinationLatLng.lng != 0.0){
//                 directionsResult = DirectionsApi.newRequest(directionsApiClient)
//                    .mode(TravelMode.DRIVING)
//                    .origin(originLatLng)
//                    .destination(destinationLatLng)
//                    .await()
//            }else{
//                 directionsResult = DirectionsApi.newRequest(directionsApiClient)
//                    .mode(TravelMode.DRIVING)
//                    .origin(originLatLng)
//                    .destination(com.google.maps.model.LatLng(31.528921813476153,34.46512654423714))
//                    .await()
//            }
//
//            // استدعاء خدمة Directions API للحصول على المسار
//
//
//            // استخراج النقاط العابرة للمسار
//            val polylineOptions = PolylineOptions()
//                .color(Color.BLUE)
//                .width(5f)
//
//            val legs = directionsResult.routes[0].legs
//            for (leg in legs) {
//                val steps = leg.steps
//                for (step in steps) {
//                    val points = step.polyline.decodePath()
//                    for (point in points) {
//                        polylineOptions.add(LatLng(point.lat, point.lng))
//                    }
//                }
//            }
//
//            // رسم المسار على الخريطة
//            googleMap.addPolyline(polylineOptions)
//
//
//            // تحريك الكاميرا لعرض المسار بشكل مركز عليه
//            try {
//                val bounds = LatLngBounds.builder()
//                    .include(LatLng(originLat!!.toDouble(), originLng!!.toDouble()))
//                    .include(LatLng(destinationLat!!.toDouble(), destinationLng!!.toDouble()))
//                    .build()
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//                val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
//                googleMap!!.setMapStyle(styleOptions)
//            } catch (e: Exception) {
//
//            }
//
//
//        }
    }
}
