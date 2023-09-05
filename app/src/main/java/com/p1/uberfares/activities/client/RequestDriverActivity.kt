package com.p1.uberfares.activities.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.modelos.ClientBooking
import com.p1.uberfares.modelos.FCMBody
import com.p1.uberfares.modelos.FCMResponce
import com.p1.uberfares.modelos.HistoryBooking
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.ClientBookingProvider
import com.p1.uberfares.provides.GeofireProvider
import com.p1.uberfares.provides.GoogleApiProvider
import com.p1.uberfares.provides.HistoryBookingProvider
import com.p1.uberfares.provides.NotificationProvider
import com.p1.uberfares.provides.TokenProvider
import com.p1.uberfares.services.FService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class RequestDriverActivity : AppCompatActivity() {
    private lateinit var mAnimation: LottieAnimationView
    private lateinit var mTextViewLookingFor: TextView
    private lateinit var mButtonCancelRequest: Button
    private var mGeofireProvider: GeofireProvider? = null
    private var mGoogleApiProvider: GoogleApiProvider? = null
    private var mExtraOrigin: String? = null
    private lateinit var database: DatabaseReference
    private var mExtraDestination: String? = null
    private var mExtraOriginLat = 0.0
    private var mExtraOriginLng = 0.0
    private var mExtraDestinationLat = 0.0
    private var mExtraDestinationLng = 0.0
    private var type = ""
    private var mHistoryBookingProvider: HistoryBookingProvider? = null
    private var mHistoryBooking: HistoryBooking? = null
    private var mOriginLatLng: LatLng? = null
    private var mDestinationLatLng: LatLng? = null
    private var mRadius = 0.1
    private var mDriverFound = false
    private var mIdDriverFound = ""
    private var mDriverFoundLatLong: LatLng? = null
    private var mNotificationProvider: NotificationProvider? = null
    private var mTokenProvider: TokenProvider? = null
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mAuthProvider: AuthProvides? = null
    private var mListener: ValueEventListener? = null
    var count = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_driver)
        mAnimation = findViewById(R.id.animation)
        mTextViewLookingFor = findViewById(R.id.textViewLookingFor)
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest)
        mAnimation.playAnimation()
        database = FirebaseDatabase.getInstance().reference
        mExtraOriginLat = intent.getDoubleExtra("origin_lat", 0.0)
        mExtraOriginLng = intent.getDoubleExtra("origin_lng", 0.0)
        mOriginLatLng = LatLng(mExtraOriginLat, mExtraOriginLng)
        mDestinationLatLng = LatLng(mExtraDestinationLat, mExtraDestinationLng)
        mGeofireProvider = GeofireProvider("active_driver")
        mNotificationProvider = NotificationProvider()
        mTokenProvider = TokenProvider()
        mClientBookingProvider = ClientBookingProvider()
        mAuthProvider = AuthProvides()
        mGoogleApiProvider = GoogleApiProvider(this@RequestDriverActivity)
        mExtraOrigin = intent.getStringExtra("origin")
        mExtraDestination = intent.getStringExtra("destination")
        mExtraOriginLat = intent.getDoubleExtra("origin_lat", 0.0)
        mExtraOriginLng = intent.getDoubleExtra("origin_lng", 0.0)
        mExtraDestinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        mExtraDestinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        type = intent.getStringExtra("type").toString()
        searchForClosestDriver()
        mHistoryBookingProvider = HistoryBookingProvider()
        mButtonCancelRequest.setOnClickListener(View.OnClickListener { cancelRequest() })
        chekStatusClientBooking()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelRequest() {

        mClientBookingProvider!!.updateStatus(mAuthProvider!!.id, "cancel")


        var r = UUID.randomUUID().toString()
        mHistoryBookingProvider!!.create(
            HistoryBooking(
                r,
                mAuthProvider!!.id,
                mIdDriverFound.toString(),
                mExtraDestination.toString(),
                mExtraOrigin.toString(),
                "00.00",
                "0.00",
                "cancel",
                mExtraOriginLat,
                mExtraOriginLng,
                mExtraDestinationLat,
                mExtraDestinationLng,
                "00:00",
                "0.00",
                r,

                )
        )
            .addOnSuccessListener(object :
                OnSuccessListener<Void?> {
                @RequiresApi(Build.VERSION_CODES.O)
                public override fun onSuccess(aVoid: Void?) {
                    Toast.makeText(
                        this@RequestDriverActivity,
                        getString(R.string.the_grade_was_saved_correctly),
                        Toast.LENGTH_LONG
                    ).show()
                    mClientBookingProvider!!.delete(
                        mAuthProvider!!.id
                    )
                    sendNotificationCancel()
                    finish()
                }
            })

//        findAnotherDriver()
//        mClientBookingProvider!!.delete(mAuthProvider!!.id)
//            .addOnSuccessListener { sendNotificationCancel() }
//
//        findAnotherDriver()
    }

    private val closesDriver: Unit
        private get() {
            mGeofireProvider!!.getActiveDriver((mOriginLatLng)!!, mRadius)
                .addGeoQueryEventListener(object : GeoQueryEventListener {
                    override fun onKeyEntered(key: String, location: GeoLocation) {
                        if (!mDriverFound) {
                            mDriverFound = true
                            mIdDriverFound = key
                            mDriverFoundLatLong = LatLng(location.latitude, location.longitude)
                            mTextViewLookingFor!!.text =
                                getString(R.string.driver_found_waiting_answer)
                            database.child("Users").child("Drivers")
                                .child(mIdDriverFound).child("type")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val typee = snapshot.getValue(String::class.java)
                                        if (typee == type) {
                                            createClientBooking()

                                        } else {
                                            mGeofireProvider!!.isDriverWorking(mIdDriverFound)
                                        }


                                    }


                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            // sendNotification();
                            Log.d("DRIVER", "ID: $mIdDriverFound")
                        }
                    }

                    override fun onKeyExited(key: String) {}
                    override fun onKeyMoved(key: String, location: GeoLocation) {}
                    override fun onGeoQueryReady() {
                        //INGRESA CUANDO TERMINA LA BuSQUEDA DEL CONNDUCTOR EN UN RADIO DE 0.1 KM
                        if (!mDriverFound) {
                            mRadius = mRadius + 0.1f
                            //NO ENCONTRO NINGUN CONDUCTOR
                            if (mRadius > 5) {
                                mTextViewLookingFor!!.text = "NO DRIVER FOUND"
                                Toast.makeText(
                                    this@RequestDriverActivity,
                                    getString(R.string.no_driver_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            } else {
                                closesDriver
                            }
                        }
                    }

                    override fun onGeoQueryError(error: DatabaseError) {}
                })
        }

    private fun createClientBooking() {
        mGoogleApiProvider!!.getDirections((mOriginLatLng)!!, (mDriverFoundLatLong)!!)!!
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        val jsonObject = JSONObject(response.body())
                        val jsonArray = jsonObject.getJSONArray("routes")
                        val route = jsonArray.getJSONObject(0)
                        val polylines = route.getJSONObject("overview_polyline")
                        val points = polylines.getString("points")
                        val legs = route.getJSONArray("legs")
                        val leg = legs.getJSONObject(0)
                        val distance = leg.getJSONObject("distance")
                        val duration = leg.getJSONObject("duration")
                        val distanceText = distance.getString("text")
                        val durationText = duration.getString("text")
                        sendNotification(durationText, distanceText)
                    } catch (e: Exception) {
                        Log.d("Error", "Error encontrado" + e.message)
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {}
            })
    }

    private fun sendNotificationCancel() {
        mTokenProvider!!.getTokens(mIdDriverFound)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val token = snapshot.child("token").value.toString()
                        val map: MutableMap<String, String> = HashMap()
                        map["title"] = getString(R.string.trip_canceled)
                        map["body"] = getString(R.string.the_client_canceled_the_request)
                        val fcmBody = FCMBody(token, "high", "4500s", map)
                        mNotificationProvider!!.sendNotification(fcmBody)!!
                            .enqueue(object : Callback<FCMResponce?> {
                                override fun onResponse(
                                    call: Call<FCMResponce?>,
                                    response: Response<FCMResponce?>
                                ) {
                                    if (response.body() != null) {
                                        if (response.body()!!.success == 1) {
                                            Toast.makeText(
                                                this@RequestDriverActivity,
                                                getString(R.string.the_request_was_successfully_canceled),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(
                                                this@RequestDriverActivity,
                                                MapClientActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                            //Toast.makeText(RequestDriverActivity.this, "La notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(
                                                this@RequestDriverActivity,
                                                getString(R.string.notification_could_not_be_sent),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@RequestDriverActivity,
                                            getString(R.string.notification_could_not_be_sent),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<FCMResponce?>, t: Throwable) {
                                    Log.d("Error", "Error " + t.message)
                                }
                            })
                    } else {
                        Toast.makeText(
                            this@RequestDriverActivity,
                            getString(R.string.the_notification_could_not_be_sent_because_the_driver_does_not_have_a_session_token),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun sendNotification(time: String, km: String) {
        mTokenProvider!!.getTokens(mIdDriverFound)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val token = snapshot.child("token").value.toString()
                        val map: HashMap<String, String> = HashMap()
                        map["title"] = "SERVICE REQUEST A " + time + "OF YOUR POSITION"
                        map["body"] =
                            (getString(R.string.a_customer_is_requesting_a_service_at_a_distance_of) + km + "\n" +
                                    getString(R.string.pick_up_at) + mExtraOrigin + "\n" +
                                    getString(R.string.destiny) + mExtraDestination)
                        map["idClient"] = mAuthProvider!!.id
                        map["origin"] = mExtraOrigin!!
                        map["destination"] = mExtraDestination!!
                        map["distance"] = km
                        map["min"] = time
                        val fcmbody = FCMBody(token, "high", "4500s", map)
                        mNotificationProvider!!.sendNotification(fcmbody)!!
                            .enqueue(object : Callback<FCMResponce?> {
                                override fun onResponse(
                                    call: Call<FCMResponce?>,
                                    response: Response<FCMResponce?>
                                ) {
                                    var p =
                                        Math.round((Math.round(km.length - 2!!.toDouble()) * 1.06) + 2.5)
                                    if (response.body() != null) {
                                        if (response.body()!!.success == 1) {
                                            val clientBooking = ClientBooking(
                                                mAuthProvider!!.id,
                                                mIdDriverFound,
                                                mExtraDestination,
                                                mExtraOrigin,
                                                time,
                                                km,
                                                "create",
                                                mExtraOriginLat,
                                                mExtraOriginLng,
                                                mExtraDestinationLat,
                                                mExtraDestinationLng,
                                                p.toString(),
                                                ""
                                            )
                                            mClientBookingProvider!!.create(clientBooking)
                                                .addOnSuccessListener(
                                                    OnSuccessListener {
                                                        chekStatusClientBooking()

                                                        //Toast.makeText(RequestDriverActivity.this, "La peticion se creo correctamente", Toast.LENGTH_SHORT).show();
                                                    })

                                            //  Toast.makeText(RequestDriverActivity.this, "La notificacion se  ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(
                                                this@RequestDriverActivity,
                                                getString(R.string.notification_could_not_be_sent),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@RequestDriverActivity,
                                            getString(R.string.notification_could_not_be_sent),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<FCMResponce?>, t: Throwable) {
                                    Log.d("Error", "Error" + t.message)
                                }
                            })
                    } else {
                        Toast.makeText(
                            this@RequestDriverActivity,
                            getString(R.string.the_notification_could_not_be_sent_because_the_driver_does_not_have_a_session_token),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun chekStatusClientBooking() {
        mListener = mClientBookingProvider!!.getstatus(mAuthProvider!!.id)
            .addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.value.toString()
                        if ((status == "accept")) {
                            val intent = Intent(
                                this@RequestDriverActivity,
                                MapClientBookingActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else if ((status == "cancel")) {
                            Toast.makeText(
                                this@RequestDriverActivity,
                                getString(R.string.the_driver_did_not_accept_the_trip),
                                Toast.LENGTH_SHORT
                            ).show()
                            count++
                            if (count <= 3){
                                mDriverFound = false
                                mGeofireProvider!!.removeLocation(mIdDriverFound) // قم بإزالة موقع الكابتن الأول من GeoFire
                                searchForClosestDriver()
                            }else{
                                cancelRequest()
                            }



                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null) {
            mClientBookingProvider!!.getstatus(mAuthProvider!!.id)
                .removeEventListener(mListener!!)
        }
    }


    private fun searchForClosestDriver() {
        mGeofireProvider!!.getActiveDriver(mOriginLatLng!!, mRadius)
            .addGeoQueryEventListener(object : GeoQueryEventListener {
                override fun onKeyEntered(key: String, location: GeoLocation) {
                    if (!mDriverFound) {
                        // تم العثور على الكابتن
                        mDriverFound = true
                        mIdDriverFound = key
                        mDriverFoundLatLong = LatLng(location.latitude, location.longitude)
                        mTextViewLookingFor!!.text = getString(R.string.driver_found_waiting_answer)

                        // استرجاع نوع الكابتن من قاعدة البيانات
                        database.child("Users").child("Drivers").child(mIdDriverFound).child("type")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val typee = snapshot.getValue(String::class.java)
                                    if (typee == type) {
                                        createClientBooking()
                                    } else {
                                        mGeofireProvider!!.isDriverWorking(mIdDriverFound)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // لم يتم تنفيذه
                                }
                            })
                        Log.d("DRIVER", "ID: $mIdDriverFound")
                    }
                }

                override fun onKeyExited(key: String) {
//                    if (key == mIdDriverFound) {
//                        // تم رفض الكابتن الأول، قم بالبحث عن كابتن آخر
//                        mDriverFound = false
//                        mGeofireProvider!!.removeLocation(key) // قم بإزالة موقع الكابتن الأول من GeoFire
//                        searchForClosestDriver() // استدعاء دالة البحث مرة أخرى
//                    }
                }

                override fun onKeyMoved(key: String, location: GeoLocation) {}
                override fun onGeoQueryReady() {
                    if (!mDriverFound) {
                        mRadius += 0.1f
                        if (mRadius > 5) {
                            mTextViewLookingFor!!.text = "NO DRIVER FOUND"
                            Toast.makeText(
                                this@RequestDriverActivity,
                                getString(R.string.no_driver_found),
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        } else {
                            searchForClosestDriver() // استدعاء دالة البحث مرة أخرى
                        }
                    }
                }

                override fun onGeoQueryError(error: DatabaseError) {}
            })
    }

}