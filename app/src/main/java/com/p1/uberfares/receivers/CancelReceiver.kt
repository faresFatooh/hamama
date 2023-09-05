package com.p1.uberfares.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.p1.uberfares.provides.ClientBookingProvider

class CancelReceiver : BroadcastReceiver() {
    private var mClientBookingProvider: ClientBookingProvider? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreference = context.getSharedPreferences("Clients", Context.MODE_PRIVATE)

        var IdClient = sharedPreference.getString("IdClient", "")
        mClientBookingProvider = ClientBookingProvider()
        mClientBookingProvider!!.updateStatus(IdClient, "cancel")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(2)
    }
}