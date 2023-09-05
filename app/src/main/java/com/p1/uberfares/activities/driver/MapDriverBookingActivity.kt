package com.p1.uberfares.activities.driver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.ebanx.swipebtn.SwipeButton
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.p1.uberfares.R
import com.p1.uberfares.SQL.MyDatabaseHelper
import com.p1.uberfares.activities.ChatActivity
import com.p1.uberfares.activities.TripLogActivity
import com.p1.uberfares.modelos.ClientBooking
import com.p1.uberfares.modelos.FCMBody
import com.p1.uberfares.modelos.FCMResponce
import com.p1.uberfares.modelos.HistoryBooking
import com.p1.uberfares.provides.*
import com.p1.uberfares.retrofit.RetrofitClient
import com.p1.uberfares.services.FService
import com.p1.uberfares.services.WService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalTime
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class MapDriverBookingActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mAuth: AuthProvides? = null
    private var mClientProvider: ClientProvider? = null
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocation: FusedLocationProviderClient? = null
    private var mCurrentLatLng: LatLng? = null
    private var mTokenProvider: TokenProvider? = null
    private var mTextViewClientBooking: TextView? = null
    private var mMarker: Marker? = null
    private var price: String? = null
    private var typee = ""
    var percentage: Double = 0.0
    private var km: Double? = null


    var p: String? = null
    private lateinit var mDatabase: DatabaseReference
    private var mTextViewEmailClientBooking: TextView? = null
    private var mTextViewOriginClientBooking: TextView? = null
    private var mTextViewDestinationClientBooking: TextView? = null
    private var tv_price: TextView? = null
    private var btn_call: Button? = null
    private var mOriginLatLng: LatLng? = null
    private var mDestinationLatLng: LatLng? = null
    private var mGoogleApiProvider: GoogleApiProvider? = null
    private lateinit var mPolylineList: List<LatLng>
    private var mPolylineOptions: PolylineOptions? = null
    private var mHistoryBooking: HistoryBooking? = null
    private var mHistoryBookingProvider: HistoryBookingProvider? = null
    private var mGeoFireProvider: GeofireProvider? = null


    private lateinit var driverRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var database2: FirebaseDatabase
    private lateinit var distanceDatabaseHelper: MyDatabaseHelper
    private var s: String? = null

    private var mExtraClientId: String? = null
    private var mIsFirstTime: Boolean = true
    private var mIsCLoseToClient: Boolean = false
    private lateinit var mButtonStartBooking: SwipeButton
    private lateinit var mButtonFinishBooking: SwipeButton
    private lateinit var btn_chat: Button
    private var time = ""
    private var time2: Long? = null
    private var mNotificationProvider: NotificationProvider? = null
    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        public override fun onLocationResult(locationResult: LocationResult) {

            for (location: Location in locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLng = LatLng(location.getLatitude(), location.getLongitude())
                    if (mMarker != null) {
                        mMarker!!.remove()
                    }
                    mMarker = mMap!!.addMarker(
                        MarkerOptions().position(
                            LatLng(location.getLatitude(), location.getLongitude())
                        )
                            .title(getString(R.string.your_position))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_50))
                    )


                    // obtenie la localizacion del usuario en tiempo real
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(16f)
                                .build()
                        )
                    )
                    if (mIsFirstTime) {
                        mIsFirstTime = false
                        clientBooking
                    }
                    updateLocation()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_driver_booking)
        mMapFragment = getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)
        mAuth = AuthProvides()
        //distanceDatabaseHelper.updateTotalDistance(00.00)
        mTokenProvider = TokenProvider()
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mClientProvider = ClientProvider()
        mClientBookingProvider = ClientBookingProvider()
        mTextViewClientBooking = findViewById(R.id.textViewClientBooking)
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailClientBooking)
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginClientBooking)
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationClientBooking)
        mButtonStartBooking = findViewById(R.id.btnStartBooking)
        mButtonFinishBooking = findViewById(R.id.btnFinishBooking)
        tv_price = findViewById(R.id.tv_price)
        btn_chat = findViewById(R.id.btn_chat)
        btn_call = findViewById(R.id.btn_call)
        mDatabase = FirebaseDatabase.getInstance().reference.child("ClientBooking")
        // mButtonStartBooking.setEnabled(false);
        mNotificationProvider = NotificationProvider()
        mGoogleApiProvider = GoogleApiProvider(this@MapDriverBookingActivity)
        distanceDatabaseHelper = MyDatabaseHelper(this)
        mClientBookingProvider = ClientBookingProvider()
        mHistoryBookingProvider = HistoryBookingProvider()
        mGeoFireProvider = GeofireProvider("drivers_working")
        mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
        s= "accept"

        price = "0"
        typee = ""
        database = FirebaseDatabase.getInstance()
        database2 = FirebaseDatabase.getInstance()

        //tv_price.text =

        if (FService.IS_RUNNING) {
            FService.stpService(this@MapDriverBookingActivity)
        }


        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)

        var xx = intent.getStringExtra("idw2")
        if (xx.equals("fares")) {
            mExtraClientId = intent.getStringExtra("idw")
            mExtraClientId = sharedPreference.getString("IdClient", "")

        } else {
            mExtraClientId = sharedPreference.getString("IdClient", "")
        }

        client
        mButtonStartBooking.setOnStateChangeListener {
            startBooking()
        }
        mButtonFinishBooking.setOnStateChangeListener {

            finishBooking()
        }

        btn_chat.setOnClickListener {
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("id", mExtraClientId.toString())
            startActivity(intent)
        }
        FirebaseDatabase.getInstance().reference.child("ClientBooking")
            .child(mExtraClientId.toString())
            .addValueEventListener(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    checkConnection()
                    s = snapshot.child("status").getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



        check()


    }

    private fun updateLocation() {

        val dataList = distanceDatabaseHelper.getData()
        val dataList2 = distanceDatabaseHelper.getData2()


        val totalDistance = calculateTotalDistance(dataList, dataList2)


        km = totalDistance

        price =
            Math.round((Math.round(km!!.toDouble()) * 1.32) + 0.6)
                .toString()
        if (price!!.toDouble() < 4) {
            price = "4"
            percentage = (price!!.toDouble() * 14) / 100
            findViewById<TextView>(R.id.tv_price).text = price.toString() + " ₪ "
            //+ getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)

        } else {
            percentage = (price!!.toDouble() * 14) / 100

            findViewById<TextView>(R.id.tv_price).text = price.toString() + " ₪ "

            //+ getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage)

        }
        if (mAuth!!.existSesion() && mCurrentLatLng != null) {
            if (!mIsCLoseToClient) {
                if (mOriginLatLng != null && mCurrentLatLng != null) {
                    val distance: Double = getDistanceBetween(mOriginLatLng!!, mCurrentLatLng!!)
                    if (distance <= 200) {
                        //  mButtonStartBooking.setEnabled(true);
                        mIsCLoseToClient = true


//                        Toast.makeText(
//                            this,
//                            getString(R.string.are_you_close_to_the_pickup_position),
//                            Toast.LENGTH_SHORT
//                        ).show()


                    }
                }
            }
        }
    }

    private fun getDistanceBetween(clientLatLng: LatLng, driverLatLng: LatLng): Double {
        var distance: Double = 0.0
        val clientLocation: Location = Location("")
        val driverLocation: Location = Location("")
        clientLocation.setLatitude(clientLatLng.latitude)
        clientLocation.setLongitude(clientLatLng.longitude)
        driverLocation.setLatitude(driverLatLng.latitude)
        driverLocation.setLongitude(driverLatLng.longitude)
        distance = clientLocation.distanceTo(driverLocation).toDouble()
        return distance
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun finishBooking() {

        if (s.equals("cancel")) {
            if (WService.isServiceRunning) {
                distanceDatabaseHelper.deleteAllData()
                WService.stopService(this@MapDriverBookingActivity)
            }
            if (FService.IS_RUNNING) {
                FService.stpService(this@MapDriverBookingActivity)
            }
            FService.strtService(this@MapDriverBookingActivity)

            val intent =
                Intent(this@MapDriverBookingActivity, MapDriverActivity::class.java)
            startActivity(intent)
            finish()
        }else{

            mClientBookingProvider!!.updateStatus(mExtraClientId, "finish")
            //mClientBookingProvider!!.updateStatus2(mExtraClientId,LocalTime.now())
            mDatabase.child(mExtraClientId!!).child("endtime").setValue(LocalTime.now())
            if (ActivityCompat.checkSelfPermission(
                    this@MapDriverBookingActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MapDriverBookingActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mDatabase.child(mExtraClientId!!).child("destinationLat")
                .setValue(mCurrentLatLng!!.latitude)
            mDatabase.child(mExtraClientId!!).child("destinationLng")
                .setValue(mCurrentLatLng!!.longitude)
            mClientBookingProvider!!.updateIdHistoryBooking(mExtraClientId)

            sendNotification(getString(R.string.finished_trip))

            val dataList = distanceDatabaseHelper.getData()
            val dataList2 = distanceDatabaseHelper.getData2()


            val totalDistance = calculateTotalDistance(dataList, dataList2)


            km = totalDistance

            val handler = Handler()

            val progressDialog = ProgressDialog(this@MapDriverBookingActivity)
            progressDialog.setMessage("Loading...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            clientBookingFinish
            handler.postDelayed({
                calificate1()
            }, 5000) // تأخير لمدة 5 ثوانٍ (5000 مللي ثانية)


            handler.postDelayed({
                calificate2()
            }, 5000) // تأخير لمدة 5 ثوانٍ (5000 مللي ثانية)


            handler.postDelayed({
                calificate3()
            }, 5000) // تأخير لمدة 5 ثوانٍ (5000 مللي ثانية)


            WService.stopService(this@MapDriverBookingActivity)

            distanceDatabaseHelper.deleteAllData()
            mGeoFireProvider = GeofireProvider("drivers_working")
            mGeoFireProvider!!.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)


            handler.postDelayed({
                progressDialog.dismiss()
                mClientBookingProvider!!.getClientBooking(mExtraClientId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        @SuppressLint("SetTextI18n")
                        public override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val clientBooking: ClientBooking? = snapshot.getValue(
                                    ClientBooking::class.java
                                )
                                val intent =
                                    Intent(
                                        this@MapDriverBookingActivity,
                                        TripLogActivity::class.java
                                    )

                                intent.putExtra(
                                    "calificationClient",
                                    5f.toDouble().toString()
                                )
                                intent.putExtra(
                                    "destination",
                                    clientBooking!!.dis.toString()
                                )
                                intent.putExtra(
                                    "destinationLat",
                                    clientBooking!!.destinationLat.toString()
                                )
                                intent.putExtra(
                                    "destinationLng",
                                    clientBooking!!.destinationLng.toString()
                                )
                                intent.putExtra("km", km.toString())
                                intent.putExtra("origin", "حمامة")
                                intent.putExtra(
                                    "originLat",
                                    clientBooking!!.originLat.toString()
                                )
                                intent.putExtra(
                                    "originLng",
                                    clientBooking!!.originLng.toString()
                                )
                                intent.putExtra("price", price.toString())
                                intent.putExtra(
                                    "status",
                                    clientBooking!!.status.toString()
                                )
                                intent.putExtra("timestamp", time.toString())
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }, 5000) // تأخير لمدة 5 ثوانٍ (5000 مللي ثانية)
        }



    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startBooking() {

                    if (s.equals("cancel")) {
                        if (WService.isServiceRunning) {
                            distanceDatabaseHelper.deleteAllData()
                            WService.stopService(this@MapDriverBookingActivity)
                        }
                        if (FService.IS_RUNNING) {
                            FService.stpService(this@MapDriverBookingActivity)
                        }
                        FService.strtService(this@MapDriverBookingActivity)

                        val intent =
                            Intent(this@MapDriverBookingActivity, MapDriverActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        mClientBookingProvider!!.updateStatus(mExtraClientId, "start")
                        //mClientBookingProvider!!.updateStatus3(, )
                        mDatabase.child(mExtraClientId!!).child("starttime")
                            .setValue(LocalTime.now())
                        mButtonStartBooking!!.setVisibility(View.GONE)
                        mButtonFinishBooking!!.setVisibility(View.VISIBLE)
                        mMap!!.clear()
                        mMap!!.addMarker(
                            MarkerOptions().position((mDestinationLatLng)!!)
                                .title(getString(R.string.destiny))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin))
                        )

                        //drawRoute(mDestinationLatLng)
                        sendNotification(getString(R.string.journey_started))
                    }
                }



    private val clientBooking: Unit
        private get() {
            mClientBookingProvider!!.getClientBooking(mExtraClientId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val destination: String =
                                snapshot.child("destination").getValue().toString()
                            val origin: String = snapshot.child("origin").getValue().toString()
                            val destinationLat: Double =
                                snapshot.child("destinationLat").getValue().toString().toDouble()
                            val destinationLng: Double =
                                snapshot.child("destinationLng").getValue().toString().toDouble()
                            val originLat: Double =
                                snapshot.child("originLat").getValue().toString().toDouble()
                            val originLng: Double =
                                snapshot.child("originLng").getValue().toString().toDouble()
                            val dis: String =
                                snapshot.child("dis").getValue().toString()
                            val phone: String =
                                snapshot.child("phone").getValue().toString()
                            mOriginLatLng = LatLng(originLat, originLng)
                            mDestinationLatLng = LatLng(destinationLat, destinationLng)
                            if (origin != "null") {
                                mTextViewOriginClientBooking!!.setText(getString(R.string.from) + origin)
                                mTextViewDestinationClientBooking!!.setText(getString(R.string.to) + destination)
                            } else {
                                mTextViewOriginClientBooking!!.setText(
                                    getString(R.string.from) + getString(
                                        R.string.hamama
                                    )
                                )
                                val mDatabase = FirebaseDatabase.getInstance().getReference("Users")

                                mDatabase.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val rface = dataSnapshot.child("Clients")
                                            .child(mExtraClientId.toString())
                                            .child("address_name")
                                            .getValue(String::class.java)

                                        mTextViewDestinationClientBooking!!.setText(getString(R.string.to) + rface)
                                        if (rface.equals(null)) {
                                            mTextViewClientBooking!!.visibility = View.GONE
//
                                            btn_call!!.setOnClickListener {
                                                val emails = phone

                                                val intent =
                                                    Intent(
                                                        Intent.ACTION_DIAL,
                                                        Uri.fromParts(
                                                            "tel",
                                                            emails,
                                                            null
                                                        )
                                                    )
                                                startActivity(intent)
                                            }

                                            mTextViewDestinationClientBooking!!.setText(dis)
                                            mTextViewEmailClientBooking!!.setText(phone)

                                        }

                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })

                            }
                            mMap!!.addMarker(
                                MarkerOptions().position(mOriginLatLng!!)
                                    .title(getString(R.string.pick_up_here))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_100))
                            )


                            //drawRoute(mOriginLatLng)
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

//    private fun drawRoute(latLng: LatLng?) {
//        mGoogleApiProvider!!.getDirections((mCurrentLatLng)!!, (latLng)!!)!!
//            .enqueue(object : Callback<String?> {
//                public override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                    try {
//                        val jsonObject: JSONObject = JSONObject(response.body())
//                        val jsonArray: JSONArray = jsonObject.getJSONArray("routes")
//                        val route: JSONObject = jsonArray.getJSONObject(0)
//                        val polylines: JSONObject = route.getJSONObject("overview_polyline")
//                        val points: String = polylines.getString("points")
//                        mPolylineList = decodePoly(points)
//                        mPolylineOptions = PolylineOptions()
//                        mPolylineOptions!!.color(Color.RED)
//                        mPolylineOptions!!.width(8f)
//                        mPolylineOptions!!.startCap(SquareCap())
//                        mPolylineOptions!!.jointType(JointType.ROUND)
//                        mPolylineOptions!!.addAll(mPolylineList)
//                        mMap!!.addPolyline(mPolylineOptions!!)
//                        val legs: JSONArray = route.getJSONArray("legs")
//                        val leg: JSONObject = legs.getJSONObject(0)
//                        val distance: JSONObject = leg.getJSONObject("distance")
//                        val duration: JSONObject = leg.getJSONObject("duration")
//                        val distanceText: String = distance.getString("text")
//                        val durationText: String = duration.getString("text")
//                    } catch (e: Exception) {
//                        Log.d("Error", getString(R.string.bug_found) + e.message)
//                    }
//                }
//
//                public override fun onFailure(call: Call<String?>, t: Throwable) {}
//            })
//    }


    private val client: Unit
        private get() {
            mClientProvider!!.getClient(mExtraClientId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val email: String = snapshot.child("email").getValue().toString()
                            val name: String = snapshot.child("name").getValue().toString()
                            mTextViewClientBooking!!.setText(name)
                            mTextViewEmailClientBooking!!.setText(email)
                            btn_call!!.setOnClickListener {

                                val phone2 = email
                                val intent =
                                    Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.fromParts("tel", phone2, null)
                                    )

                                startActivity(intent)


                            }
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {

                    }
                })
        }


    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        mMap!!.getUiSettings().setZoomControlsEnabled(true)
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(1000)
        mLocationRequest!!.setFastestInterval(1000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setSmallestDisplacement(5f)
        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
        mMap!!.setMapStyle(styleOptions)
        startLocation()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.setMyLocationEnabled(false)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationRequest?.let {
                    mFusedLocation!!.requestLocationUpdates(
                        it,
                        mLocationCallBack,
                        Looper.myLooper()
                    )
                }

            }
        } else {
            showAlertDialogNOGPS()
        }
    }

    private fun showAlertDialogNOGPS() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.please_activate_your_location_to_continue))
            .setPositiveButton(
                getString(R.string.settings),
                object : DialogInterface.OnClickListener {
                    public override fun onClick(dialog: DialogInterface, which: Int) {
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            SETTINGS_REQUEST_CODE
                        )
                    }
                }).create().show()
    }

    private fun gpsActived(): Boolean {
        var isActive: Boolean = false
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true
        }
        return isActive
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (gpsActived()) {
                    mLocationRequest?.let {
                        mFusedLocation!!.requestLocationUpdates(
                            it,
                            mLocationCallBack,
                            Looper.myLooper()
                        )
                    }

                    WService.startService(this@MapDriverBookingActivity)
                } else {
                    showAlertDialogNOGPS()
                }
            } else {
                checkLocationPermission()
            }
        } else {
            if (gpsActived()) {
                mLocationRequest?.let {
                    mFusedLocation!!.requestLocationUpdates(
                        it,
                        mLocationCallBack,
                        Looper.myLooper()
                    )
                }
                WService.startService(this@MapDriverBookingActivity)
            } else {
                showAlertDialogNOGPS()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.provide_permissions_to_continue))
                    .setMessage(getString(R.string.this_application_requires_location_permissions_to_be_used))
                    .setPositiveButton(
                        getString(R.string.ok),
                        object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                ActivityCompat.requestPermissions(
                                    this@MapDriverBookingActivity, arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ), LOCATION_REQUEST_CODE
                                )
                            }
                        }).create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MapDriverBookingActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    private fun sendNotification(status: String) {
        mTokenProvider!!.getTokens(mExtraClientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val token: String = snapshot.child("token").getValue().toString()
                        val map: MutableMap<String, String> = HashMap()
                        map.put("title", getString(R.string.trip_status))
                        map.put(
                            "body",
                            getString(R.string.your_trip_status_is) + status
                        )
                        val fcmbody: FCMBody = FCMBody(token, "high", "4500s", map)
                        mNotificationProvider!!.sendNotification(fcmbody)!!
                            .enqueue(object : Callback<FCMResponce?> {
                                public override fun onResponse(
                                    call: Call<FCMResponce?>,
                                    response: Response<FCMResponce?>
                                ) {
                                    if (response.body() != null) {
                                        if (response.body()!!.success != 1) {
                                            Toast.makeText(
                                                this@MapDriverBookingActivity,
                                                getString(R.string.notification_could_not_be_sent),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@MapDriverBookingActivity,
                                            getString(R.string.notification_could_not_be_sent),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                public override fun onFailure(
                                    call: Call<FCMResponce?>,
                                    t: Throwable
                                ) {
                                    Log.d("Error", "Error" + t.message)
                                }
                            })
                    } else {
                        Toast.makeText(
                            this@MapDriverBookingActivity,
                            getString(R.string.the_notification_could_not_be_sent_because_the_driver_does_not_have_a_session_token),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        private val LOCATION_REQUEST_CODE: Int = 1
        private val SETTINGS_REQUEST_CODE: Int = 2
    }


    private val clientBookingFinish: Unit
        @RequiresApi(Build.VERSION_CODES.O) private get() {

            database.reference.child("Users").child("Drivers")
                .child(FirebaseAuth.getInstance().uid.toString()).child("type")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            typee = snapshot.getValue(String::class.java).toString()
                            if (typee == "Car") {
                                price =
                                    Math.round((Math.round(km!!.toDouble()) * 1.06) + 2.5)
                                        .toString()
                                if (price!!.toDouble() < 5) {
                                    price = "5"
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "

                                } else {
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "
                                }

                                if (time2!!.toInt() > 5) {
                                    price =
                                        (price!!.toDouble() + ((time2!!.toDouble() - 5) * 0.25)).toString()
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "
                                    if (price!!.toDouble() < 5) {
                                        price = "5"
                                        percentage = (price!!.toDouble() * 10) / 100
                                        findViewById<TextView>(R.id.tv_price).text =
                                            price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "
                                    } else {
                                        percentage = (price!!.toDouble() * 10) / 100
                                        findViewById<TextView>(R.id.tv_price).text =
                                            price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "
                                    }
                                } else {
                                    percentage = (price!!.toDouble() * 10) / 100
                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "
                                }


                            } else if (typee == "Motorcycle") {
                                price =
                                    Math.round((Math.round(km!!.toDouble()) * 1.32) + 0.6)
                                        .toString()
                                if (price!!.toDouble() < 4) {
                                    price = "4"
                                    percentage = (price!!.toDouble() * 14) / 100
                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "

                                } else {
                                    percentage = (price!!.toDouble() * 14) / 100

                                    findViewById<TextView>(R.id.tv_price).text =
                                        price.toString() + " ₪ " + getString(R.string.your_bonus_is) + (price!!.toDouble() - percentage) + " ₪ "

                                }

                            }


                        }

                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
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
                            if (clientBooking!!.destination == null) {
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
                                    clientBooking!!.idHistoryBooking!!,
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calificate1() {
        database = FirebaseDatabase.getInstance()
        database2 = FirebaseDatabase.getInstance()
        driverRef = database.getReference(RetrofitClient.Users)


        time2 = 25
        timeE()
        time2()


    }

    private fun calificate2() {

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


    }

    private fun calificate3() {


        mHistoryBooking!!.setCalificationClient(5f.toDouble())

        mHistoryBookingProvider!!.getHistoryBooking(mHistoryBooking!!.getIdHistoryBooking())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        mHistoryBookingProvider!!.updateCalificationClient(
                            mHistoryBooking!!.getIdHistoryBooking(),
                            5f
                        ).addOnSuccessListener(object :
                            OnSuccessListener<Void?> {
                            @RequiresApi(Build.VERSION_CODES.O)
                            public override fun onSuccess(aVoid: Void?) {
                                Toast.makeText(
                                    this@MapDriverBookingActivity,
                                    getString(R.string.the_grade_was_saved_correctly),
                                    Toast.LENGTH_LONG
                                ).show()
//                                val intent: Intent = Intent(
//                                    this@MapDriverBookingActivity,
//                                    MapDriverActivity::class.java
//                                )
                                FService.strtService(this@MapDriverBookingActivity)
//                                startActivity(intent)
//                                finish()
                            }
                        })
                    } else {
                        mHistoryBookingProvider!!.create(mHistoryBooking!!)
                            .addOnSuccessListener(object :
                                OnSuccessListener<Void?> {
                                @RequiresApi(Build.VERSION_CODES.O)
                                public override fun onSuccess(aVoid: Void?) {
                                    Toast.makeText(
                                        this@MapDriverBookingActivity,
                                        getString(R.string.the_grade_was_saved_correctly),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    mClientBookingProvider!!.delete(
                                        mHistoryBooking!!.getRandom()
                                    )
//                                    val intent: Intent = Intent(
//                                        this@MapDriverBookingActivity,
//                                        MapDriverActivity::class.java
//                                    )
                                    FService.strtService(this@MapDriverBookingActivity)
                                    //startActivity(intent)
                                    //finish()
                                }
                            })
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
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
        try {
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

        } catch (e: Exception) {

        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun check() {
        mClientBookingProvider!!.getstatus(mExtraClientId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.value.toString()
                        if (status == "start") {
                            mButtonStartBooking!!.setVisibility(View.GONE)
                            mButtonFinishBooking!!.setVisibility(View.VISIBLE)
                            mMap!!.clear()
                            mClientBookingProvider!!.getClientBooking(mExtraClientId)
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
                                            mDestinationLatLng =
                                                LatLng(destinationLat, destinationLng)
                                            mCurrentLatLng =
                                                LatLng(originLat, originLng)

                                            mMap!!.addMarker(
                                                MarkerOptions().position((mDestinationLatLng)!!)
                                                    .title(getString(R.string.destiny))
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin))
                                            )
                                            mGoogleApiProvider =
                                                GoogleApiProvider(this@MapDriverBookingActivity)

//                                            mGoogleApiProvider!!.getDirections(
//                                                (mCurrentLatLng)!!,
//                                                (mDestinationLatLng)!!
//                                            )!!
//                                                .enqueue(object : Callback<String?> {
//                                                    public override fun onResponse(
//                                                        call: Call<String?>,
//                                                        response: Response<String?>
//                                                    ) {
//                                                        try {
//                                                            val jsonObject: JSONObject =
//                                                                JSONObject(response.body())
//                                                            val jsonArray: JSONArray =
//                                                                jsonObject.getJSONArray("routes")
//                                                            val route: JSONObject =
//                                                                jsonArray.getJSONObject(0)
//                                                            val polylines: JSONObject =
//                                                                route.getJSONObject("overview_polyline")
//                                                            val points: String =
//                                                                polylines.getString("points")
//                                                            mPolylineList = decodePoly(points)
//                                                            mPolylineOptions = PolylineOptions()
//                                                            mPolylineOptions!!.color(Color.RED)
//                                                            mPolylineOptions!!.width(8f)
//                                                            mPolylineOptions!!.startCap(SquareCap())
//                                                            mPolylineOptions!!.jointType(JointType.ROUND)
//                                                            mPolylineOptions!!.addAll(mPolylineList)
//                                                            mMap!!.addPolyline(mPolylineOptions!!)
//                                                            val legs: JSONArray =
//                                                                route.getJSONArray("legs")
//                                                            val leg: JSONObject =
//                                                                legs.getJSONObject(0)
//                                                            val distance: JSONObject =
//                                                                leg.getJSONObject("distance")
//                                                            val duration: JSONObject =
//                                                                leg.getJSONObject("duration")
//                                                            val distanceText: String =
//                                                                distance.getString("text")
//                                                            val durationText: String =
//                                                                duration.getString("text")
//                                                        } catch (e: Exception) {
//                                                            Log.d(
//                                                                "Error",
//                                                                "Error encontrado" + e.message
//                                                            )
//                                                        }
//                                                    }
//
//                                                    public override fun onFailure(
//                                                        call: Call<String?>,
//                                                        t: Throwable
//                                                    ) {
//                                                    }
//                                                })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

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
    private fun checkConnection() {
        val manager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netWorkeInfo = manager.activeNetworkInfo
        if (null != netWorkeInfo) {
            if (netWorkeInfo.type == ConnectivityManager.TYPE_WIFI) {
               // Toast.makeText(this, "Wifi Connected ", Toast.LENGTH_SHORT).show()
            } else if (netWorkeInfo.type == ConnectivityManager.TYPE_MOBILE) {
                //Toast.makeText(this, "Mobile Data Connected ", Toast.LENGTH_SHORT).show()

            }

        } else {
            val dialog = Dialog(this)

            dialog.setContentView(R.layout.alert_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.findViewById<Button>(R.id.btn_try_again).setOnClickListener {
                recreate()
            }
            dialog.show()


        }

    }
}
