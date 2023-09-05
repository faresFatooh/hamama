package com.p1.uberfares.provides

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.modelos.Clientt

class ClientProvider {
    var mDatabase: DatabaseReference

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child("Clients")
    }

    fun create(client: Clientt): Task<Void> {
        val Map = HashMap<String, Any>()
        Map["email"] = client.email
        Map["name"] = client.name
        return mDatabase.child(client.id).setValue(Map)
    }

    fun getClient(idClient: String?): DatabaseReference {
        return mDatabase.child(idClient!!)
    }
}