package com.p1.uberfares.activities.client

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.modelos.ClientBooking
import com.p1.uberfares.modelos.HistoryBooking
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.ClientBookingProvider
import com.p1.uberfares.provides.HistoryBookingProvider
import java.time.Duration
import java.time.LocalTime
import kotlin.properties.Delegates

class CalificationDriverActivity : AppCompatActivity() {
    private lateinit var mTextViewOrigin: TextView
    private lateinit var mTextViewDestination: TextView
    private lateinit var price: TextView
    private lateinit var allprice: String
    private var time by Delegates.notNull<Long>()
    private lateinit var mRatingBar: RatingBar
    private lateinit var database: FirebaseDatabase
    private lateinit var mButtonCalification: Button
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mAuthProvider: AuthProvides? = null
    private var mHistoryBooking: HistoryBooking? = null
    private var mHistoryBookingProvider: HistoryBookingProvider? = null
    private var mCalification: Float = 0f
    private var typee = ""
    var percentage: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calification_driver)
        mTextViewOrigin = findViewById(R.id.textViewOriginCalification)
        mTextViewDestination = findViewById(R.id.textViewDestinationCalification)
        price = findViewById(R.id.tv_price_d)
        mRatingBar = findViewById(R.id.ratingbarCalification)
        mButtonCalification = findViewById(R.id.btnCalification)
        mClientBookingProvider = ClientBookingProvider()
        mHistoryBookingProvider = HistoryBookingProvider()
        allprice = "0"
        var driverId = getIntent().getStringExtra("driverId")
        time = 0
        mAuthProvider = AuthProvides()
        mRatingBar.setOnRatingBarChangeListener(object : OnRatingBarChangeListener {
            public override fun onRatingChanged(
                ratingBar: RatingBar,
                calification: Float,
                fromUser: Boolean
            ) {
                mCalification = calification
            }
        })
        mButtonCalification.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                calificate()

            }
        })
        database = FirebaseDatabase.getInstance()
        database.reference.child("Users").child("Drivers")
            .child(driverId.toString()).child("type")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        typee = snapshot.getValue(String::class.java).toString()
                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        time2()
        price()
        clientBooking

    }

    private val clientBooking: Unit
        private get() {
            mClientBookingProvider!!.getClientBooking(mAuthProvider!!.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val clientBooking: ClientBooking? = snapshot.getValue(
                                ClientBooking::class.java
                            )

                            mTextViewOrigin!!.setText(clientBooking!!.origin)
                            mTextViewDestination!!.setText(clientBooking.destination)
                            price.setText(allprice)

                            if (clientBooking.destination == null) {
                                mHistoryBooking = HistoryBooking(
                                    clientBooking.idHistoryBooking!!,
                                    clientBooking.idClient!!,
                                    clientBooking.idDriver!!,
                                    "حمامة",
                                    clientBooking.dis,
                                    clientBooking.time!!,
                                    clientBooking.km!!,
                                    clientBooking.status!!,
                                    clientBooking.originLat,
                                    clientBooking.originLng,
                                    clientBooking.destinationLat,
                                    clientBooking.destinationLng,
                                    time.toString(),
                                    allprice,
                                    clientBooking.random
                                )
                            } else {
                                mHistoryBooking = HistoryBooking(
                                    clientBooking.idHistoryBooking!!,
                                    clientBooking.idClient!!,
                                    clientBooking.idDriver!!,
                                    clientBooking.destination!!,
                                    clientBooking.origin!!,
                                    clientBooking.time!!,
                                    clientBooking.km!!,
                                    clientBooking.status!!,
                                    clientBooking.originLat,
                                    clientBooking.originLng,
                                    clientBooking.destinationLat,
                                    clientBooking.destinationLng,
                                    time.toString(),
                                    allprice,
                                    clientBooking.random
                                )
                            }
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun calificate() {
        if (mCalification > 0) {
            mHistoryBooking!!.setCalificationDriver(mCalification.toDouble())
            mHistoryBookingProvider!!.getHistoryBooking(mHistoryBooking!!.getIdHistoryBooking())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            mHistoryBookingProvider!!.updateCalificationDriver(
                                mHistoryBooking!!.getIdHistoryBooking(),
                                mCalification
                            ).addOnSuccessListener(object : OnSuccessListener<Void?> {
                                public override fun onSuccess(aVoid: Void?) {
                                    Toast.makeText(
                                        this@CalificationDriverActivity,
                                        getString(R.string.the_grade_was_saved_correctly),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent: Intent = Intent(
                                        this@CalificationDriverActivity,
                                        MapClientActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                            })
                        } else {
                            mHistoryBookingProvider!!.create((mHistoryBooking)!!)
                                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                    public override fun onSuccess(aVoid: Void?) {
                                        Toast.makeText(
                                            this@CalificationDriverActivity,
                                            getString(R.string.the_grade_was_saved_correctly),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        mClientBookingProvider!!.delete(mHistoryBooking!!.getIdClient())
                                        val intent: Intent = Intent(
                                            this@CalificationDriverActivity,
                                            MapClientActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    }
                                })
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            Toast.makeText(this, getString(R.string.you_must_enter_the_rating), Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun price() {
        mClientBookingProvider!!.getClientBooking(mAuthProvider!!.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        val destinationLat: Double =
                            snapshot.child("destinationLat").getValue()
                                .toString().toDouble()
                        val destinationLng: Double =
                            snapshot.child("destinationLng").getValue()
                                .toString().toDouble()
                        val originLat: Double =
                            snapshot.child("originLat").getValue().toString()
                                .toDouble()
                        val originLng: Double =
                            snapshot.child("originLng").getValue().toString()
                                .toDouble()
                        var mDestinationLatLng =
                            LatLng(destinationLat, destinationLng)
                        var mCurrentLatLng =
                            LatLng(originLat, originLng)
                        var km = distanceInKm(
                            mCurrentLatLng!!.latitude,
                            mCurrentLatLng!!.longitude,
                            mDestinationLatLng!!.latitude,
                            mDestinationLatLng!!.longitude
                        )
                        getType(km)
                        Toast.makeText(
                            this@CalificationDriverActivity,
                            typee,
                            Toast.LENGTH_LONG
                        ).show()

                        price.setText(allprice.toString() + getString(R.string.your_bonus_is) + (allprice!!.toDouble() - percentage))

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    fun distanceInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(
                deg2rad(
                    lat1
                )
            ) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun getType(km: Double) {


        if (typee == "Car") {
            allprice =
                Math.round((Math.round(km) * 1.06) + 2.5).toString()
            if (allprice!!.toDouble() < 5) {
                allprice = "5"
            }
            percentage = (allprice!!.toDouble() * 10) / 100

            if (time.toInt() > 5) {
                allprice =
                    (allprice!!.toDouble() + ((time!!.toDouble() - 5) * 0.25)).toString()
                if (allprice!!.toDouble() < 5) {
                    allprice = "5"
                }
                percentage = (allprice!!.toDouble() * 10) / 100
            }

        } else if (typee == "Motorcycle") {
            allprice =
                Math.round((Math.round(km) * 1.32) + 1.1).toString()
            if (allprice!!.toDouble() < 4) {
                allprice = "4"
            }
            percentage = (allprice!!.toDouble() * 14) / 100

//            if (time.toInt() > 4) {
//                allprice =
//                    (allprice!!.toDouble() + ((time!!.toDouble() - 5) * 0.25)).toString()
//                if (allprice!!.toDouble() < 4) {
//                    allprice = "4"
//                }
//                percentage = (allprice!!.toDouble() * 10) / 100
//            }


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun time2() {
        var start: LocalTime? = null
        var end: LocalTime? = null
        mClientBookingProvider!!.getClientBooking(mAuthProvider!!.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val h: String =
                            snapshot.child("starttime").child("hour").getValue()
                                .toString()
                        val m: String =
                            snapshot.child("starttime").child("minute").getValue()
                                .toString()
                        val s: String =
                            snapshot.child("starttime").child("second").getValue()
                                .toString()
                        val n: String =
                            snapshot.child("starttime").child("nano").getValue()
                                .toString()
                        start =
                            LocalTime.of(h.toInt(), m.toInt(), s.toInt(), n.toInt())

                        val h2: String =
                            snapshot.child("endtime").child("hour").getValue()
                                .toString()
                        val m2: String =
                            snapshot.child("endtime").child("minute").getValue()
                                .toString()
                        val s2: String =
                            snapshot.child("endtime").child("second").getValue()
                                .toString()
                        val n2: String =
                            snapshot.child("endtime").child("nano").getValue()
                                .toString()

                        end = LocalTime.of(
                            h2.toInt(),
                            m2.toInt(),
                            s2.toInt(),
                            n2.toInt()
                        )

                        time = difference2(start!!, end!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun difference2(start: LocalTime, stop: LocalTime): Long {
        val start = start
        val end = stop
        val duration = Duration.between(start, end)
        var hours = duration.seconds / 3600;
        var minutes = (duration.seconds % 3600) / 60;
        var seconds = duration.seconds % 60;

        return minutes
    }


}