package com.p1.uberfares.provides

import com.google.firebase.auth.FirebaseAuth

class AuthProvides {
    var mAuth: FirebaseAuth


    init {
        mAuth = FirebaseAuth.getInstance()
    }


    fun logout() {
        mAuth.signOut()
    }

    val id: String
        get() = mAuth.currentUser!!.uid

    fun existSesion(): Boolean {
        var exist = false
        if (mAuth.currentUser != null) {
            exist = true
        }
        return exist
    }
}