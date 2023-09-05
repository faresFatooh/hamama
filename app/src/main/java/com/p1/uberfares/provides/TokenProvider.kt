package com.p1.uberfares.provides

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.p1.uberfares.modelos.Token

class TokenProvider {
    var mDatabase: DatabaseReference

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Tokens")
    }

    fun create(idUser: String?) {
        if (idUser == null) return
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = Token(instanceIdResult.token)
            mDatabase.child(idUser).setValue(token)
        }
    }

    fun getTokens(IdUser: String?): DatabaseReference {
        return mDatabase.child(IdUser!!)
    }
}