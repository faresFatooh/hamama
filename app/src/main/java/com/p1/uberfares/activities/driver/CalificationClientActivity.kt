package com.p1.uberfares.activities.driver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.p1.uberfares.R
import com.p1.uberfares.SQL.MyDatabaseHelper
import com.p1.uberfares.activities.client.Point
import com.p1.uberfares.modelos.*
import com.p1.uberfares.provides.ClientBookingProvider
import com.p1.uberfares.provides.GeofireProvider
import com.p1.uberfares.provides.HistoryBookingProvider
import com.p1.uberfares.retrofit.RetrofitClient
import com.p1.uberfares.services.FService
import java.time.Duration
import java.time.LocalTime
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CalificationClientActivity : AppCompatActivity() {
    private var mTextViewOrigin: TextView? = null
    private var mTextViewDestination: TextView? = null
    private var price: String? = null
    private lateinit var driverRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var database2: FirebaseDatabase
    private lateinit var mRatingBar: RatingBar
    private lateinit var mButtonCalification: Button
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mExtraClientId: String? = null
    private var mHistoryBooking: HistoryBooking? = null
    private var mHistoryBookingProvider: HistoryBookingProvider? = null
    private var mCalification: Float = 0f
    private var mGeoFireProvider: GeofireProvider? = null
    private var time = ""
    private var time2: Long? = null
    private var typee = ""
    var percentage: Double = 0.0
    private var km: Double? = null
    private lateinit var distanceDatabaseHelper: MyDatabaseHelper


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calification_client)
        distanceDatabaseHelper = MyDatabaseHelper(this)
        mTextViewOrigin = findViewById(R.id.textViewOriginCalification)
        mTextViewDestination = findViewById(R.id.textViewDestinationCalification)
        mRatingBar = findViewById(R.id.ratingbarCalification)
        mButtonCalification = findViewById(R.id.btnCalification)


        mClientBookingProvider = ClientBookingProvider()
        mHistoryBookingProvider = HistoryBookingProvider()
        mGeoFireProvider = GeofireProvider("drivers_working")
        mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)

        mExtraClientId = getIntent().getStringExtra("idClient")
        if (mExtraClientId == null) {
            val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)
            mExtraClientId = sharedPreference.getString("IdClient", "")
        }


        price = "0"
        typee = ""
        database = FirebaseDatabase.getInstance()
        database2 = FirebaseDatabase.getInstance()
        driverRef = database.getReference(RetrofitClient.Users)



        val dataList = distanceDatabaseHelper.getData()
        val dataList2 = distanceDatabaseHelper.getData2()


        val totalDistance = calculateTotalDistance(dataList, dataList2)


        km = totalDistance

        database.reference.child("Users").child("Drivers")
            .child(FirebaseAuth.getInstance().uid.toString()).child("type")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        typee = snapshot.getValue(String::class.java).toString()
                        if (typee == "Car") {
                            price =
                                Math.round((Math.round(km!!.toDouble()) * 1.06) + 2.5).toString()
                            if (price!!.toDouble() < 5) {
                                price = "5"
                                percentage = (price!!.toDouble() * 10) / 100
                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)

                            } else {
                                percentage = (price!!.toDouble() * 10) / 100
                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)
                            }

                            if (time2!!.toInt() > 5) {
                                price =
                                    (price!!.toDouble() + ((time2!!.toDouble() - 5) * 0.25)).toString()
                                percentage = (price!!.toDouble() * 10) / 100
                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)
                                if (price!!.toDouble() < 5) {
                                    price = "5"
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price_c).text =
                                        price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)
                                } else {
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price_c).text =
                                        price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)
                                }
                            } else {
                                percentage = (price!!.toDouble() * 10) / 100
                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)
                            }


                        } else if (typee == "Motorcycle") {
                            price =
                                Math.round((Math.round(km!!.toDouble()) * 1.32) + 0.6).toString()
                            if (price!!.toDouble() < 4) {
                                price = "4"
                                percentage = (price!!.toDouble() * 14) / 100
                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)

                            } else {
                                percentage = (price!!.toDouble() * 14) / 100

                                findViewById<TextView>(R.id.tv_price_c).text =
                                    price.toString() + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)

                            }

                        }


                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        time2 = 25
        timeE()
        time2()



        mRatingBar.setOnRatingBarChangeListener(object : OnRatingBarChangeListener {
            public override fun onRatingChanged(
                ratingBar: RatingBar,
                calification: Float,
                fromUser: Boolean
            ) {
                mCalification = calification
            }
        })
        mButtonCalification.setOnClickListener {
            database2.reference.child("Users").child("Drivers")
                .child(FirebaseAuth.getInstance().uid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val name: String =
                                snapshot.child("name").getValue().toString()
                            val email: String =
                                snapshot.child("email").getValue().toString()
                            val mns: String =
                                snapshot.child("mius").getValue().toString()
                            if (mns == "") {
                                var percentage = (price!!.toDouble() * 10) / 100
                                driverRef.child(RetrofitClient.Drivers)
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("mius")
                                    .setValue((0.0 + percentage).toString())

                            } else {
                                var percentage = (price!!.toDouble() * 10) / 100
                                driverRef.child(RetrofitClient.Drivers)
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("mius")
                                    .setValue((mns.toDouble() + percentage).toString())
                            }

                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })

            calificate()
        }
        clientBooking
    }




    private fun calificate() {
        if (mCalification > 0) {
            mHistoryBooking!!.setCalificationClient(mCalification.toDouble())
            mHistoryBookingProvider!!.getHistoryBooking(mHistoryBooking!!.getIdHistoryBooking())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            mHistoryBookingProvider!!.updateCalificationClient(
                                mHistoryBooking!!.getIdHistoryBooking(),
                                mCalification
                            ).addOnSuccessListener(object :
                                OnSuccessListener<Void?> {
                                @RequiresApi(Build.VERSION_CODES.O)
                                public override fun onSuccess(aVoid: Void?) {
                                    Toast.makeText(
                                        this@CalificationClientActivity,
                                        getString(R.string.the_grade_was_saved_correctly),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent: Intent = Intent(
                                        this@CalificationClientActivity,
                                        MapDriverActivity::class.java
                                    )
                                    FService.strtService(this@CalificationClientActivity)
                                    startActivity(intent)
                                    finish()
                                }
                            })
                        } else {
                            mHistoryBookingProvider!!.create(mHistoryBooking!!)
                                .addOnSuccessListener(object :
                                    OnSuccessListener<Void?> {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    public override fun onSuccess(aVoid: Void?) {
                                        Toast.makeText(
                                            this@CalificationClientActivity,
                                            getString(R.string.the_grade_was_saved_correctly),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        mClientBookingProvider!!.delete(
                                            mHistoryBooking!!.getRandom()
                                        )
                                        val intent: Intent = Intent(
                                            this@CalificationClientActivity,
                                            MapDriverActivity::class.java
                                        )
                                        FService.strtService(this@CalificationClientActivity)
                                        startActivity(intent)
                                        finish()
                                    }
                                })
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            Toast.makeText(
                this,
                getString(R.string.you_must_enter_the_rating),
                Toast.LENGTH_SHORT
            ).show()
        }
        distanceDatabaseHelper.deleteAllData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun difference(start: LocalTime, stop: LocalTime): String {
        val start = start
        val end = stop
        val duration = Duration.between(start, end)
        var hours = duration.seconds / 3600;
        var minutes = (duration.seconds % 3600) / 60;
        var seconds = duration.seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeE() {

        var start: LocalTime? = null
        var end: LocalTime? = null
        mClientBookingProvider!!.getClientBooking(mExtraClientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
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
                        time = difference(start!!, end!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun time2() {
        var start: LocalTime? = null
        var end: LocalTime? = null
        mClientBookingProvider!!.getClientBooking(mExtraClientId)
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

                        time2 = difference2(start!!, end!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }


    private val clientBooking: Unit
        @RequiresApi(Build.VERSION_CODES.O) private get() {
            time2()
            mClientBookingProvider!!.getClientBooking(mExtraClientId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    @SuppressLint("SetTextI18n")
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val clientBooking: ClientBooking? = snapshot.getValue(
                                ClientBooking::class.java
                            )
                            // var p = Math.round((Math.round(clientBooking!!.km!!.toDouble()) * 1.06) + 2.5)
                            mTextViewOrigin!!.setText(clientBooking!!.origin)
                            mTextViewDestination!!.setText(clientBooking.destination)
                            // price!!.setText(p.toString())
                            if (clientBooking.destination == null) {
                                mHistoryBooking = HistoryBooking(
                                    clientBooking.idHistoryBooking!!,
                                    clientBooking.idClient!!,
                                    clientBooking.idDriver!!,
                                    "حمامة",
                                    clientBooking.dis,
                                    clientBooking.time!!,
                                    km!!.toString(),
                                    clientBooking.status!!,
                                    clientBooking.originLat,
                                    clientBooking.originLng,
                                    clientBooking.destinationLat,
                                    clientBooking.destinationLng,
                                    time2.toString(),
                                    price.toString(),
                                    clientBooking.random,

                                    )

                            } else {

                                mHistoryBooking = HistoryBooking(
                                    clientBooking.idHistoryBooking!!,
                                    clientBooking.idClient!!,
                                    clientBooking.idDriver!!,
                                    clientBooking.destination!!,
                                    clientBooking.origin!!,
                                    clientBooking.time!!,
                                    km!!.toString(),
                                    clientBooking.status!!,
                                    clientBooking.originLat,
                                    clientBooking.originLng,
                                    clientBooking.destinationLat,
                                    clientBooking.destinationLng,
                                    time2.toString(),
                                    price.toString(),
                                    clientBooking.random,
                                )
                            }
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

    override fun onDestroy() {
        super.onDestroy()
        mCalification = 5F
        calificate()
        distanceDatabaseHelper.deleteAllData()
        mGeoFireProvider = GeofireProvider("drivers_working")
        mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)

    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // نصف قطر الأرض بالكيلومترات

        val lat1Radians = Math.toRadians(lat1)
        val lon1Radians = Math.toRadians(lon1)
        val lat2Radians = Math.toRadians(lat2)
        val lon2Radians = Math.toRadians(lon2)

        val dLat = lat2Radians - lat1Radians
        val dLon = lon2Radians - lon1Radians

        val a = sin(dLat / 2).pow(2) + cos(lat1Radians) * cos(lat2Radians) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c // المسافة بالكيلومترات

        return distance
    }

    fun calculateTotalDistance(latList: List<Double>, lonList: List<Double>): Double {
        var totalDistance = 0.0

        for (i in 0 until latList.size - 1) {
            val lat1 = latList[i]
            val lon1 = lonList[i]
            val lat2 = latList[i + 1]
            val lon2 = lonList[i + 1]

            val distance = calculateDistance(lat1, lon1, lat2, lon2)
            totalDistance += distance
        }

        return totalDistance
    }


}