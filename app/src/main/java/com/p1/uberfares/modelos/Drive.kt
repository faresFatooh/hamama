package com.p1.uberfares.modelos

import com.google.firebase.auth.FirebaseAuth

class Drivee {
    var id = ""
    var name = ""
    var phone = ""
    var registration = ""
    var address = ""
    var mius = ""
    var idNumber = ""
    var region = ""
    var type = ""
    var model = ""
    var color = ""
    var dstates = ""
}

class Drive(
    var id: String,
    var name: String,
    var email: String,
    var vehicle: String,
    var place: String,
    var mius: String
)

data class Driver(
    val lat: Double,
    val lng: Double,
    val driverId: String = FirebaseAuth.getInstance().uid.toString()
)

