package com.p1.uberfares.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.*
import com.p1.uberfares.activities.driver.MapDriverBookingActivity
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.ClientBookingProvider
import com.p1.uberfares.provides.GeofireProvider

class AcceptReceiver : BroadcastReceiver() {
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mGeofireProvider: GeofireProvider? = null
    private var mAuthProvider: AuthProvides? = null
    private lateinit var mDatabase: DatabaseReference


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreference = context.getSharedPreferences("Clients", Context.MODE_PRIVATE)

        var IdClient = sharedPreference.getString("IdClient", "")
        // var IdClient = ""

        mAuthProvider = AuthProvides()
        mGeofireProvider = GeofireProvider("active_driver")
        mGeofireProvider!!.removeLocation(mAuthProvider!!.id)
        mDatabase = FirebaseDatabase.getInstance().reference.child("ClientBooking")

        mClientBookingProvider = ClientBookingProvider()
        mClientBookingProvider!!.updateStatus(IdClient, "accept")
        Toast.makeText(context, IdClient, Toast.LENGTH_SHORT).show()

        // FirebaseDatabase.getInstance().reference.child("ClientBooking")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        for (snapshot in snapshot.children) {
//                            val notification = snapshot.getValue(ClientBooking::class.java)
//                            if (notification!!.idClient == IdClient){
//                                Toast.makeText(context,notification.random,Toast.LENGTH_SHORT).show()
//                                 mDatabase.child(notification.random!!).child("status").setValue("accept")
//                                IdClient = notification.random
//                                Toast.makeText(context, IdClient, Toast.LENGTH_SHORT).show()
//
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(2)
        val intent1 = Intent(context, MapDriverBookingActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent1.action = Intent.ACTION_RUN
        var editor = sharedPreference.edit()
        editor.clear()
        editor.putString("IdClient", IdClient)
        editor.apply()
        intent1.putExtra("IdClient", IdClient)
        Toast.makeText(context, IdClient.toString() + "FARES", Toast.LENGTH_SHORT).show()
        context.startActivity(intent1)
    }
}