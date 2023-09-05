package com.p1.uberfares.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.GeoApiContext
import com.p1.uberfares.R
import com.p1.uberfares.SQL.MyDatabaseHelper
import com.p1.uberfares.provides.GeofireProvider

class WService : Service() {

    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var totalDistance: Float = 0f
    private lateinit var status: String
    private var mGeoFireProvider: GeofireProvider? = null
    private val polylineOptions = PolylineOptions()
    private lateinit var geoApiContext: GeoApiContext
    private lateinit var distanceDatabaseHelper: MyDatabaseHelper

    companion object {
        private const val MIN_TIME_INTERVAL = 1000L // 1 second
        private const val MIN_DISTANCE = 1f // 1 meter
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        var isServiceRunning = false

        fun startService(context: Context) {
            if (!isServiceRunning) {
                val serviceIntent = Intent(context, WService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
                isServiceRunning = true
            }
        }

        fun stopService(context: Context) {
            if (isServiceRunning) {
                val serviceIntent = Intent(context, WService::class.java)
                context.stopService(serviceIntent)
                isServiceRunning = false
            }
        }
    }

    var IdClient = ""

    override fun onCreate() {
        super.onCreate()
        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)
        IdClient = sharedPreference.getString("IdClient", "").toString()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        distanceDatabaseHelper = MyDatabaseHelper(this)
        mGeoFireProvider = GeofireProvider("drivers_working")
        FirebaseDatabase.getInstance().reference.child("ClientBooking").child(IdClient)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    status = snapshot.child("status").getValue(String::class.java).toString()
                    Log.d("farescc", "onDataChange: ${status.toString()}")

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        startForegroundService()
        startLocationUpdates()
        // initGeoApiContext()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        distanceDatabaseHelper.close()
//        mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = createNotificationChannel("wservice_channel", "WService Channel")

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("WService")
            .setContentText("Running")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1, notificationBuilder)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            channelId
        } else {
            ""
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_INTERVAL,
            MIN_DISTANCE,
            locationListener
        )
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY;
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (isServiceRunning) {

                mGeoFireProvider = GeofireProvider("drivers_working")
                mGeoFireProvider!!.saveLocation(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    LatLng(location.latitude, location.longitude)

                )


                if (status == "start") {
                    if (currentLocation != null) {
                        distanceDatabaseHelper.insertData(
                            location.latitude,
                            location.longitude
                        )

                    }
                }else if (status == "null" || status == "cancel"){
                    if (isServiceRunning) {
                        val serviceIntent = Intent(applicationContext, WService::class.java)
                        stopService(serviceIntent)
                        val serviceIntenté = Intent(applicationContext, FService::class.java)
                        startService(serviceIntenté)
                        mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
                        isServiceRunning = false
                    }
                }
                currentLocation = location

            }
        }

        override fun onProviderDisabled(provider: String) {}

        override fun onProviderEnabled(provider: String) {

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

}