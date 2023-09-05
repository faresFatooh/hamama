package com.p1.uberfares.provides

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.modelos.ClientBooking
import java.time.LocalTime

class ClientBookingProvider {
    private val mDatabase: DatabaseReference

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("ClientBooking")
    }

    fun create(clientBooking: ClientBooking): Task<Void> {
        return mDatabase.child(clientBooking.idClient!!).setValue(clientBooking)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatus(IdClientBooking: String?, status: String): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["status"] = status

        return mDatabase.child(IdClientBooking!!).updateChildren(map)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatus2(IdClientBooking: String?, endTime: String): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["idDriver"] = endTime
        return mDatabase.child(IdClientBooking!!).updateChildren(map)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatus3(IdClientBooking: String?, starTtime: LocalTime): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["starttime"] = starTtime
        return mDatabase.child(IdClientBooking!!).updateChildren(map)
    }

    fun updateIdHistoryBooking(IdClientBooking: String?): Task<Void> {
        val idPush = mDatabase.push().key
        val map: MutableMap<String, Any?> = HashMap()
        map["idHistoryBooking"] = idPush
        return mDatabase.child(IdClientBooking!!).updateChildren(map)
    }

    fun getstatus(idClientBooking: String?): DatabaseReference {
        return mDatabase.child(idClientBooking!!).child("status")
    }

    fun getClientBooking(idClientBooking: String?): DatabaseReference {
        return mDatabase.child(idClientBooking!!)
    }

    fun delete(IdClientBooking: String?): Task<Void> {
        return mDatabase.child(IdClientBooking!!).removeValue()
    }
}