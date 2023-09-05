package com.p1.uberfares.activities.driver

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobeedev.library.SlidingMenuBuilder
import com.mobeedev.library.SlidingNavigation
import com.mobeedev.library.gravity.SlideGravity
import com.p1.uberfares.R
import com.p1.uberfares.activities.MainActivity
import com.p1.uberfares.adapter.HistoryAdapter
import com.p1.uberfares.include.MyToolbar
import com.p1.uberfares.modelos.HistoryBooking
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.GeofireProvider
import com.p1.uberfares.provides.TokenProvider
import com.p1.uberfares.services.FService
import com.p1.uberfares.utils.MapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MapDriverActivity : AppCompatActivity(), OnMapReadyCallback, AdapterListener,
    SensorEventListener {
    private var mMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mAuth: AuthProvides? = null
    private lateinit var database: FirebaseDatabase
    private var mGeoFireProvider: GeofireProvider? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocation: FusedLocationProviderClient? = null
    private var mMarker: Marker? = null
    private lateinit var mButtonConnect: Button
    private var mIsConnect: Boolean = false
    private var mCurrentLatLng: LatLng? = null
    private var mTokenProvider: TokenProvider? = null
    private var mListener: ValueEventListener? = null
    private var azimuth: Float = 0.0f
    private lateinit var sensorManager: SensorManager

   // lateinit var toolbar: Toolbar
    lateinit var slidingNavigation: SlidingNavigation

    private var OrderAdapter: HistoryAdapter? = null
    private var mOrder: List<HistoryBooking>? = null
    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location: Location in locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLng = LatLng(location.getLatitude(), location.getLongitude())
                    if (mMarker != null) {
                        mMarker!!.remove()
                    }
                    FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                        .child(FirebaseAuth.getInstance().uid!!).child("type")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val type = snapshot.getValue(String::class.java)
                                if (type == "Car") {
                                    val bitmapDescriptor =
                                        BitmapDescriptorFactory.fromBitmap(
                                            MapUtils.getCarBitmap(
                                                this@MapDriverActivity,
                                                R.drawable.ic_car
                                            )
                                        )
                                    mMarker = mMap!!.addMarker(
                                        MarkerOptions().position(
                                            LatLng(location.getLatitude(), location.getLongitude())
                                        ).title(getString(R.string.your_position)).flat(true)
                                            .icon(bitmapDescriptor)
                                    )
                                    mMarker!!.rotation = azimuth


                                } else {
                                    val bitmapDescriptor =
                                        BitmapDescriptorFactory.fromBitmap(
                                            MapUtils.getCarBitmap(
                                                this@MapDriverActivity,
                                                R.drawable.motorcycle
                                            )
                                        )

                                    mMarker = mMap!!.addMarker(
                                        MarkerOptions().position(
                                            LatLng(location.getLatitude(), location.getLongitude())
                                        ).title(getString(R.string.your_position)).flat(true)
                                            .icon(bitmapDescriptor)
                                    )
                                    mMarker!!.rotation = azimuth


                                }

                            }


                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })


                    // obtenie la localizacion del usuario en tiempo real
                    if (mMap!= null){
                        val cameraPosition = mMap!!.cameraPosition

                        val bearing = (azimuth)


                        mMap!!.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder(cameraPosition).bearing(bearing)
                                    .target(LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f).build()
                            )
                        )
                    }

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_driver)
        database = FirebaseDatabase.getInstance()
        mButtonConnect = findViewById(R.id.btnConnect)
        val animator = ObjectAnimator.ofFloat(mButtonConnect, "alpha", 1f, 0f, 1f)
        animator.duration = 2000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mMapFragment = getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)


        //toolbar = findViewById<Toolbar>(R.id.toolbar)

       // setSupportActionBar(toolbar)

        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            setUpMenu(savedInstanceState)
            slidingNavigation.openMenu()
        }

       // setUpMenu(savedInstanceState)


        mOrder = ArrayList()

        OrderAdapter = HistoryAdapter(this@MapDriverActivity, mOrder as ArrayList<HistoryBooking>)

        if (FService.IS_RUNNING) {
            mButtonConnect!!.setText(getString(R.string.log_off))
            animator.resume()
            animator.cancel()
            mButtonConnect.alpha = 1.0f
            mIsConnect = true
        }
        val notificationRef =
            FirebaseDatabase.getInstance().reference.child("ClientBooking")
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {
                    (mOrder as ArrayList<HistoryBooking>).clear()
                    for (snapshot in datasnapshot.children) {
                        val notification = snapshot.getValue(HistoryBooking::class.java)
                        if (notification?.getIdClient() != null) {
                            if (notification?.getIdDriver()
                                    .equals(FirebaseAuth.getInstance().currentUser!!.uid) && notification.getStatus()
                                    .equals("accept")
                            ) {
                                (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                                var intent =
                                    Intent(
                                        this@MapDriverActivity,
                                        MapDriverBookingActivity::class.java
                                    )
                                intent.putExtra("idw", notification.getIdClient())
                                intent.putExtra("idw2", "fares")

                                startActivity(intent)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                finish()
                            }
                            if (notification?.getIdDriver()
                                    .equals(FirebaseAuth.getInstance().currentUser!!.uid) && notification.getStatus()
                                    .equals("start")
                            ) {
                                (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                                var intent =
                                    Intent(
                                        this@MapDriverActivity,
                                        MapDriverBookingActivity::class.java
                                    )
                                intent.putExtra("idw", notification.getIdClient())
                                intent.putExtra("idw2", "fares")
                                startActivity(intent)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                finish()
                            }
                            if (notification?.getIdDriver()
                                    .equals(FirebaseAuth.getInstance().currentUser!!.uid) && notification.getStatus()
                                    .equals("finish")
                            ) {
                                (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                                var intent =
                                    Intent(
                                        this@MapDriverActivity,
                                        CalificationClientActivity::class.java
                                    )
                                // startActivity(intent)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                finish()
                            }
                        }
                    }
                    Collections.reverse(mOrder)
                    OrderAdapter!!.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        database.reference.child("Users").child("Drivers")
            .child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name: String = snapshot.child("name").getValue().toString()
                        val mns: String = snapshot.child("mius").getValue().toString()
                        //  var v = navView.getHeaderView(0)
//                        v.findViewById<TextView>(R.id.mns)
//                            .setText(getString(R.string.deserved_amount) + mns)
//                        v.findViewById<TextView>(R.id.name_nav).setText(name)
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })


        mAuth = AuthProvides()
        mGeoFireProvider = GeofireProvider("active_driver")
        mTokenProvider = TokenProvider()
        mButtonConnect.setOnClickListener {
            if (mIsConnect) {
                disconnect()
                FService.stpService(this)
                animator.start()

            } else {
                startLocation()
                FService.strtService(this)
                animator.resume()
                animator.cancel()
                mButtonConnect.alpha = 1.0f

            }

        }


        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        generateToken()
        isDriverWorking

    }


    private val isDriverWorking: Unit
        private get() {
            mListener = mGeoFireProvider!!.isDriverWorking(mAuth!!.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            disconnect()
                            if (FService.IS_RUNNING) {
                                FService.stpService(this@MapDriverActivity)
                            }
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }



    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        mMap!!.getUiSettings().setZoomControlsEnabled(true)
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isCompassEnabled = true

        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
        mMap!!.setMapStyle(styleOptions)
        val locationButton =
            (mMapFragment!!.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val rlp = locationButton.getLayoutParams() as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(1000)
        mLocationRequest!!.setFastestInterval(1000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setSmallestDisplacement(5f)
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.setMyLocationEnabled(false)
        }

        if (FService.IS_RUNNING) {
            mButtonConnect!!.setText(getString(R.string.log_off))
            mIsConnect = true
            mFusedLocation!!.requestLocationUpdates(
                mLocationRequest!!, mLocationCallBack, Looper.myLooper()
            )
        }

    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // لا يتطلب التنفيذ
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuth = (azimuth + 360) % 360



        }
    }


    public override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (gpsActived()) {
                        mFusedLocation!!.requestLocationUpdates(
                            mLocationRequest!!, mLocationCallBack, Looper.myLooper()
                        )
                    } else {
                        showAlertDialogNOGPS()
                    }
                } else {
                    checkLocationPermission()
                }
            } else {
                checkLocationPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocation!!.requestLocationUpdates(
                    mLocationRequest!!, mLocationCallBack, Looper.myLooper()
                )
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
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE
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

    private fun disconnect() {
        if (mFusedLocation != null) {
            mButtonConnect!!.setText(getString(R.string.connect))
            mIsConnect = false
            mFusedLocation!!.removeLocationUpdates(mLocationCallBack)
            if (mAuth!!.existSesion()) {
                mGeoFireProvider!!.removeLocation(mAuth!!.id)

            }
        } else {
            Toast.makeText(this, getString(R.string.you_can_t_disconnect), Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                if (gpsActived()) {
                    mButtonConnect!!.setText(getString(R.string.log_off))
                    mIsConnect = true
                    mFusedLocation!!.requestLocationUpdates(
                        mLocationRequest!!, mLocationCallBack, Looper.myLooper()
                    )
                } else {
                    showAlertDialogNOGPS()
                }
            } else {
                checkLocationPermission()
            }
        } else {
            if (gpsActived()) {
                mFusedLocation!!.requestLocationUpdates(
                    mLocationRequest!!, mLocationCallBack, Looper.myLooper()
                )
            } else {
                showAlertDialogNOGPS()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.provide_permissions_to_continue))
                    .setMessage(getString(R.string.this_application_requires_location_permissions_to_be_used))
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        public override fun onClick(dialog: DialogInterface, which: Int) {
                            ActivityCompat.requestPermissions(
                                this@MapDriverActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                LOCATION_REQUEST_CODE
                            )
                        }
                    }).create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MapDriverActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }


    public override fun logout() {
        disconnect()
        mAuth!!.logout()
        val intent = Intent(this@MapDriverActivity, MainActivity::class.java)
        startActivity(intent)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        finish()
        val intent2 = Intent(this@MapDriverActivity, FService::class.java)
        stopService(intent2)
    }

    fun generateToken() {
        mTokenProvider!!.create(mAuth!!.id)
    }

    companion object {
        private val LOCATION_REQUEST_CODE: Int = 1
        private val SETTINGS_REQUEST_CODE: Int = 2
    }

    override fun onBackPressed() {
//
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//
//
//        } else if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            finish()
//        } else
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpMenu(savedInstanceState: Bundle?) {
       // slidingNavigation = SlidingMenuBuilder(this)
        val locale: Locale = resources.configuration.locales.get(0)
        val language: String = locale.language

        if (language == "ar") {
            slidingNavigation = SlidingMenuBuilder(this@MapDriverActivity)
                .withMenuOpened(false)
                .withDragDistance(280) //Horizontal translation of a view. Default == 180dp
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.fragment_menu)
                .withGravity(SlideGravity.RIGHT)
                .inject()
        } else {
            slidingNavigation = SlidingMenuBuilder(this@MapDriverActivity)
                .withMenuOpened(false)
                .withDragDistance(280) //Horizontal translation of a view. Default == 180dp
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.fragment_menu)
                .withGravity(SlideGravity.LEFT)
                .inject()
        }

        val tmpElements = mutableListOf(
            SlideMenuItem(R.drawable.wallet, getString(R.string.payment_history)),
            SlideMenuItem(
                R.drawable.road, getString(
                    R.string.trip_history
                )
            ),
            SlideMenuItem(R.drawable.add_user, getString(R.string.invite_friends)),
            SlideMenuItem(
                R.drawable.settings, getString(R.string.settings)
            ),
            SlideMenuItem(R.drawable.contact, getString(R.string.connect)),
            SlideMenuItem(R.drawable.power, getString(R.string.log_off))
        )
        val adapter = SlideMenuAdapter(tmpElements, this@MapDriverActivity, this@MapDriverActivity)


        val menu = findViewById<RecyclerView>(R.id.menu_recycler)
        menu.layoutManager = LinearLayoutManager(this)
        menu.isNestedScrollingEnabled = false
        menu.adapter = adapter

    }

}