package com.p1.uberfares.activities.client

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.activities.ChatActivity
import com.p1.uberfares.activities.ConversationsActivity
import com.p1.uberfares.provides.*
import com.p1.uberfares.utils.DecodePoints.decodePoly
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

class MapClientBookingActivity constructor() : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mAuth: AuthProvides? = null
    private val mGeofireProvider: AuthProvides? = null
    private var mGeofireProvide: GeofireProvider? = null
    private var mMarkerDriver: Marker? = null
    private var mIsFirstTime: Boolean = true
    private val mOrigin: String? = null
    private var mOriginLatLng: LatLng? = null
    private val mDestination: String? = null
    private var mDestinationLatLng: LatLng? = null
    private var mDriverLatLng: LatLng? = null
    private var mTokenProvider: TokenProvider? = null
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mDriverProvider: DriverProvider? = null
    private lateinit var mTextViewClientBooking: TextView
    private lateinit var mTextViewEmailClientBooking: TextView
    private lateinit var mTextViewOriginClientBooking: TextView
    private lateinit var mTextViewDestinationClientBooking: TextView
    private lateinit var mTextViewStatusBooking: TextView
    private lateinit var btn_call: Button
    private lateinit var btnChat: Button
    private var mGoogleApiProvider: GoogleApiProvider? = null
    private var mPolylineList: List<LatLng>? = null
    private var mPolylineOptions: PolylineOptions? = null
    private var mListener: ValueEventListener? = null
    private var mIdDriver: String? = null
    var start: LocalTime? = null
    var stop: LocalTime? = null
    private var mListenerStatus: ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_client_booking)
        mMapFragment = getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)
        mTokenProvider = TokenProvider()
        mClientBookingProvider = ClientBookingProvider()
        mGeofireProvide = GeofireProvider("drivers_working")
        mGoogleApiProvider = GoogleApiProvider(this@MapClientBookingActivity)
        mDriverProvider = DriverProvider()
        mAuth = AuthProvides()
        if (!Places.isInitialized()) {
            Places.initialize(
                getApplicationContext(),
                getResources().getString(R.string.google_maps_key)
            )
        }
        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)

        mTextViewClientBooking = findViewById(R.id.textViewDriverBooking)
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailDriverBooking)
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginDriverBooking)
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationDriverBooking)
        mTextViewStatusBooking = findViewById(R.id.textViewStatusBooking)
        btn_call = findViewById(R.id.btn_call)
        btnChat = findViewById(R.id.btn_chat_Driver)

        var xx = intent.getStringExtra("idw2")
        if (xx.equals("fares")) {
            mIdDriver = intent.getStringExtra("idw")
        }
        btnChat.setOnClickListener {
            var intent = Intent(this, ConversationsActivity::class.java)
            intent.putExtra("id",FirebaseAuth.getInstance().currentUser!!.uid.toString())
            startActivity(intent)
        }

        status

        clientBooking
    }

    private val status: Unit
        private get() {
            mListenerStatus = mClientBookingProvider!!.getstatus(mAuth!!.id)
                .addValueEventListener(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val status: String = snapshot.getValue().toString()
                            if ((status == "accept")) {
                                mTextViewStatusBooking!!.setText(getString(R.string.status_accepted))
                            }
                            if ((status == "start")) {
                                mTextViewStatusBooking!!.setText(getString(R.string.status_trip_started))
                                start = LocalTime.now()
                                startBooking()

                            } else if ((status == "finish")) {
                                stop = LocalTime.now()
                                mTextViewStatusBooking!!.setText(getString(R.string.status_trip_finished))
                                finishBooking()
                            }
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun finishBooking() {
        val intent: Intent =
            Intent(this@MapClientBookingActivity, CalificationDriverActivity::class.java)
        intent.putExtra("driverId", mIdDriver)
        startActivity(intent)
        finish()
    }

    private fun startBooking() {
        mMap!!.clear()
        if (mDestinationLatLng != null) {
            mMap!!.addMarker(
                MarkerOptions().position((mDestinationLatLng)!!).title("Destiny")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin))
            )
        }
        if (mDestinationLatLng != null) {
            drawRoute(mDestinationLatLng)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null) {
            mGeofireProvide!!.getDriverLocation(mIdDriver).removeEventListener(mListener!!)
        }
        if (mListenerStatus != null) {
            mClientBookingProvider!!.getstatus(mAuth!!.id).removeEventListener(mListenerStatus!!)
        }
    }

    private val clientBooking: Unit
        private get() {
            mClientBookingProvider!!.getClientBooking(mAuth!!.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val destination: String =
                                snapshot.child("destination").getValue().toString()
                            val origin: String = snapshot.child("origin").getValue().toString()
                            val idDriver: String = snapshot.child("idDriver").getValue().toString()
                            mIdDriver = idDriver
                            val destinationLat: Double =
                                snapshot.child("destinationLat").getValue().toString().toDouble()
                            val destinationLng: Double =
                                snapshot.child("destinationLng").getValue().toString().toDouble()
                            val originLat: Double =
                                snapshot.child("originLat").getValue().toString().toDouble()
                            val originLng: Double =
                                snapshot.child("originLng").getValue().toString().toDouble()
                            mOriginLatLng = LatLng(originLat, originLng)
                            mDestinationLatLng = LatLng(destinationLat, destinationLng)
                            if (origin!= "null"){
                                mTextViewOriginClientBooking!!.setText(getString(R.string.from) + ": " + origin)
                                mTextViewDestinationClientBooking!!.setText(getString(R.string.to) + destination)
                            }else{
                                mTextViewOriginClientBooking!!.setText(getString(R.string.from) + ": " + "حمامة")
                                val mDatabase = FirebaseDatabase.getInstance().getReference("Users")

                                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val rface = dataSnapshot.child("Clients").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child("address_name")
                                            .getValue(String::class.java)

                                        mTextViewDestinationClientBooking!!.setText(getString(R.string.to) + rface)

                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })

                            }

                            mMap!!.addMarker(
                                MarkerOptions().position(mOriginLatLng!!).title(getString(R.string.pick_up_here))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_100))
                            )
                            getDriver(idDriver)
                            getDriverLocation(idDriver)
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun getDriver(idDriver: String) {
        mDriverProvider!!.getDriver(idDriver)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name: String = snapshot.child("name").getValue().toString()
                        val phone: String = snapshot.child("phone").getValue().toString()
                        mTextViewClientBooking!!.setText(name)
                        mTextViewEmailClientBooking!!.setText(phone)
                        btn_call.setOnClickListener {
                            val phone = phone
                            val intent =
                                Intent(Intent.ACTION_DIAL, Uri.fromParts(getString(R.string.tel), phone, null))
                            startActivity(intent)
                        }
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getDriverLocation(idDriver: String) {
        mListener = mGeofireProvide!!.getDriverLocation(idDriver)
            .addValueEventListener(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lat: Double = snapshot.child("0").getValue().toString().toDouble()
                        val lng: Double = snapshot.child("1").getValue().toString().toDouble()
                        mDriverLatLng = LatLng(lat, lng)
                        if (mMarkerDriver != null) {
                            mMarkerDriver!!.remove()
                        }
                        mMarkerDriver = mMap!!.addMarker(
                            MarkerOptions().position(
                                LatLng(lat, lng)
                            )
                                .title(getString(R.string.your_driver))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_50))
                        )
                        if (mIsFirstTime) {
                            mIsFirstTime = false
                            mMap!!.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder().target(mDriverLatLng!!)
                                        .zoom(14f).build()
                                )
                            )
                            drawRoute(mOriginLatLng)
                        }
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun drawRoute(latLng: LatLng?) {
        mGoogleApiProvider!!.getDirections((mDriverLatLng)!!, (latLng)!!)!!
            .enqueue(object : Callback<String?> {
                public override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        val jsonObject: JSONObject = JSONObject(response.body())
                        val jsonArray: JSONArray = jsonObject.getJSONArray("routes")
                        val route: JSONObject = jsonArray.getJSONObject(0)
                        val polylines: JSONObject = route.getJSONObject("overview_polyline")
                        val points: String = polylines.getString("points")
                        mPolylineList = decodePoly(points)
                        mPolylineOptions = PolylineOptions()
                        mPolylineOptions!!.color(Color.DKGRAY)
                        mPolylineOptions!!.width(8f)
                        mPolylineOptions!!.startCap(SquareCap())
                        mPolylineOptions!!.jointType(JointType.ROUND)
                        mPolylineOptions!!.addAll(mPolylineList!!)
                        mMap!!.addPolyline(mPolylineOptions!!)
                        val legs: JSONArray = route.getJSONArray("legs")
                        val leg: JSONObject = legs.getJSONObject(0)
                        val distance: JSONObject = leg.getJSONObject("distance")
                        val duration: JSONObject = leg.getJSONObject("duration")
                        val distanceText: String = distance.getString("text")
                        val durationText: String = duration.getString("text")
                    } catch (e: Exception) {
                        Log.d("Error", getString(R.string.bug_found) + e.message)
                    }
                }

                public override fun onFailure(call: Call<String?>, t: Throwable) {}
            })
    }

    public override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        mMap!!.getUiSettings().setZoomControlsEnabled(true)
        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
        mMap!!.setMapStyle(styleOptions)
    }


}