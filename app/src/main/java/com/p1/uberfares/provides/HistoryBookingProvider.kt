package com.p1.uberfares.provides

import com.firebase.geofire.GeoFire
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.modelos.HistoryBooking

class HistoryBookingProvider {
    private val mDatabase: DatabaseReference
    private lateinit var mGeoFire: GeoFire
    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("HistoryBooking")


    }

    fun create(historyBooking: HistoryBooking): Task<Void> {
        return mDatabase.child(historyBooking.getIdHistoryBooking()).setValue(historyBooking)
    }


    fun updateCalificationClient(idHistoryBooking: String?, calificationClient: Float): Task<Void> {
        val map = HashMap<String, Any>()
        map["calificationClient"] = calificationClient
        return mDatabase.child(idHistoryBooking!!).updateChildren(map)
    }

    fun updateCalificationDriver(idHistoryBooking: String?, calificationDriver: Float): Task<Void> {
        val map = HashMap<String, Any>()
        map["calificationDriver"] = calificationDriver
        return mDatabase.child(idHistoryBooking!!).updateChildren(map)
    }

    fun getHistoryBooking(idHistoryBooking: String?): DatabaseReference {
        return mDatabase.child(idHistoryBooking!!)
    }
}