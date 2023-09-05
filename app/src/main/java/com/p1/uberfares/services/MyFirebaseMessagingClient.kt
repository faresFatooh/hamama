package com.p1.uberfares.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.p1.uberfares.R
import com.p1.uberfares.activities.driver.MapDriverActivity
import com.p1.uberfares.activities.driver.NotificationBookingActivity
import com.p1.uberfares.channel.NotificationHelper
import com.p1.uberfares.receivers.AcceptReceiver
import com.p1.uberfares.receivers.CancelReceiver

@Suppress("DEPRECATION")
class MyFirebaseMessagingClient : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        val title = data["title"]
        val body = data["body"]
        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (title.contains("SERVICE REQUEST")) {
                    val IdClient = data["idClient"]
                    val origin = data["origin"]
                    val destination = data["destination"]
                    val distance = data["distance"]
                    val min = data["min"]
                    showNotificationApiOreoActions(title, body, IdClient)
                    showNotificationActivity(IdClient, origin, destination, distance, min)

                } else if (title.contains("TRIP CANCELED")) {
                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.cancel(2)
                    showNotificationApiOreo(title, body)
                } else {
                    showNotificationApiOreo(title, body)
                }
            } else {
                if (title.contains("SERVICE REQUEST")) {
                    val IdClient = data["idClient"]
                    showNotificationActions(title, body, IdClient)
                    val origin = data["origin"]
                    val destination = data["destination"]
                    val distance = data["distance"]
                    val min = data["min"]
                    showNotificationActivity(IdClient, origin, destination, distance, min)
                } else if (title.contains("TRIP CANCELED")) {
                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.cancel(2)
                    showNotification(title, body)
                } else {
                    showNotification(title, body)
                }
            }
        }
    }

    private fun showNotificationActivity(
        idClient: String?,
        origin: String?,
        destination: String?,
        distance: String?,
        min: String?
    ) {
        val pm = baseContext.getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        if (!isScreenOn) {
            val wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "AppName:MyLock"
            )
            wakeLock.acquire(10000)
        }
        FirebaseDatabase.getInstance().reference.child("ClientBooking").child(idClient.toString())
            .child("idDriver").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var idc = snapshot.getValue(String::class.java)
                if (idc == FirebaseAuth.getInstance().currentUser!!.uid) {
                    val intent = Intent(baseContext, NotificationBookingActivity::class.java)
                    intent.putExtra("IdClient", idClient)
                    intent.putExtra("origin", origin)
                    intent.putExtra("destination", destination)
                    intent.putExtra("distance", distance)
                    intent.putExtra("min", min)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        })


    }

    //
    private fun showNotification(title: String, body: String?) {
        val intent =
            PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_MUTABLE)
        val Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotificationOldAPI(title, body, Sound)
        notificationHelper.manager!!.notify(1, builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showNotificationActions(title: String, body: String?, IdClient: String?) {
        //ACEPTAR
        val acceptIntent = Intent(this, AcceptReceiver::class.java)
        acceptIntent.putExtra("IdClient", IdClient)
        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.clear()
        editor.putString("IdClient", IdClient)
        editor.apply()
        Log.d("states", IdClient.toString())

        val acceptPendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_CODE,
            acceptIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val acceptAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            getString(R.string.accept),
            acceptPendingIntent
        ).build()


        //CANCELAR
        val cancelIntent = Intent(this, CancelReceiver::class.java)
        cancelIntent.putExtra("IdClient", IdClient)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_CODE,
            cancelIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val cancelAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            getString(R.string.cancel),
            cancelPendingIntent
        ).build()

        //PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        val Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotificationOldAPIActions(
            title,
            body,
            Sound,
            acceptAction,
            cancelAction
        )
        notificationHelper.manager!!.notify(1, builder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showNotificationApiOreo(title: String, body: String?) {
        val intent =
            PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_MUTABLE)
        val Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotification(title, body, intent, Sound)
        notificationHelper.manager!!.notify(1, builder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showNotificationApiOreoActions(title: String, body: String?, IdClient: String?) {
        val acceptIntent = Intent(this, AcceptReceiver::class.java)
        acceptIntent.putExtra("IdClient", IdClient)
        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.clear()
        editor.putString("IdClient", IdClient)
        editor.apply()
        Log.d("states", IdClient.toString())


        val acceptPendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_CODE,
            acceptIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val acceptAction = Notification.Action.Builder(
            R.mipmap.ic_launcher,
            getString(R.string.accept),
            acceptPendingIntent
        ).build()
        val cancelIntent = Intent(this, CancelReceiver::class.java)
        cancelIntent.putExtra("IdClient", IdClient)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_CODE,
            cancelIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val cancelAction = Notification.Action.Builder(
            R.mipmap.ic_launcher,
            getString(R.string.cancel),
            cancelPendingIntent
        ).build()


        //PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        val Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationHelper = NotificationHelper(baseContext)
        val builder = notificationHelper.getNotificationActions(
            title,
            body,
            Sound,
            acceptAction,
            cancelAction
        )
        notificationHelper.manager!!.notify(1, builder.build())
    }

    companion object {
        private const val NOTIFICATION_CODE = 100
    }
}