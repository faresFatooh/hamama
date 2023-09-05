package com.p1.uberfares.provides

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GeofireProvider(reference: String?) {
    private val mDatabase: DatabaseReference
    private val mGeoFire: GeoFire

    init {
        mDatabase = FirebaseDatabase.getInstance().reference.child(reference!!)
        mGeoFire = GeoFire(mDatabase)
    }

    fun saveLocation(IdDriver: String?, latlng: LatLng) {
        mGeoFire.setLocation(IdDriver, GeoLocation(latlng.latitude, latlng.longitude))
    }

    fun removeLocation(IdDriver: String?) {
        mGeoFire.removeLocation(IdDriver)
    }

    fun getActiveDriver(latLng: LatLng, radius: Double): GeoQuery {
        val geoQuery =
            mGeoFire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), radius)
        geoQuery.removeAllListeners()
        return geoQuery
    }

    fun getActiveDriverExcept(latLng: LatLng, radius: Double): GeoQuery {
        val geoQuery =
            mGeoFire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), radius)
        geoQuery.removeAllListeners()
        return geoQuery
    }

    fun getDriverLocation(idDriver: String?): DatabaseReference {
        return mDatabase.child(idDriver!!).child("l")
    }



    fun isDriverWorking(idDriver: String?): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("drivers_working").child(idDriver!!)
    }
}