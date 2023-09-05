package com.p1.uberfares.provides

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.modelos.Drive

class DriverProvider {
    var mDatabase: DatabaseReference

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
    }

    fun create(driver: Drive): Task<Void> {
        return mDatabase.child(driver.id).setValue(driver)
    }

    fun getDriver(idDriver: String?): DatabaseReference {
        return mDatabase.child(idDriver!!)
    }
}