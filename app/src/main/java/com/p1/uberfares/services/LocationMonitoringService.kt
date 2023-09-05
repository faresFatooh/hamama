package com.p1.uberfares.services


import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.p1.uberfares.R
import com.p1.uberfares.provides.GeofireProvider
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError


class FService : Service() {
    companion object {
        private const val CHANNEL_ID: String = "58475"
        private const val CHANNEL_NAME: String = "CHANNELL"
        private var mGeoFireProvider: GeofireProvider? = null

        private const val SERVICE_ID: Int = 1
        var IS_RUNNING: Boolean = false

        var tracker: LocationTracker? = null



        @RequiresApi(Build.VERSION_CODES.O)
        fun strtService(context: Activity) {
            if (!IS_RUNNING) {
                val hasFineLocation = ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val hasCoarseLocation = ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasFineLocation || !hasCoarseLocation) {
                    return ActivityCompat.requestPermissions(
                        context, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 1337
                    )
                }

                

                tracker = LocationTracker(
                    minTimeBetweenUpdates = 10000L, // one second
                    minDistanceBetweenUpdates = 3F, // one meter
                    shouldUseGPS = true, shouldUseNetwork = true, shouldUsePassive = true
                ).also {
                    it.addListener(object : LocationTracker.Listener {
                        override fun onLocationFound(location: Location) {
                            mGeoFireProvider = GeofireProvider("active_driver")
                            mGeoFireProvider!!.saveLocation(
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                LatLng(location.latitude, location.longitude)
                            )
                            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("is_connected").setValue("true")

                        }

                        override fun onProviderError(providerError: ProviderError) {
                        }
                    })
                }



                tracker!!.startListening(context = context)
                val startIntent = Intent(context, FService::class.java)
                context.startForegroundService(startIntent)

            }
            IS_RUNNING = true

        }

        fun stpService(context: Context) {

            val stopIntent = Intent(context, FService::class.java)
            context.stopService(stopIntent)
            tracker!!.stopListening(clearListeners = true)
            mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
            IS_RUNNING = false


        }
    }

    override fun onCreate() {
        super.onCreate()
        mGeoFireProvider = GeofireProvider("active_driver")
        startForegroundService()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY;
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )

            nChannel.lightColor = Color.BLUE
            nChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(nChannel)
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startFS()
    }

    private fun startFS() {
        val description = getString(R.string.app_name)
        val title = String.format("active")
        startForeground(SERVICE_ID, getStickyNotification(title, description))
        IS_RUNNING = true
    }


    private fun getStickyNotification(title: String, message: String): Notification? {
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, Intent(), PendingIntent.FLAG_MUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setContentTitle(title)
        builder.setContentText(message)

        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setFullScreenIntent(pendingIntent, true)

        return builder.build()
    }

    override fun onDestroy() {
        tracker!!.stopListening(clearListeners = true)
        IS_RUNNING = false
    }

}