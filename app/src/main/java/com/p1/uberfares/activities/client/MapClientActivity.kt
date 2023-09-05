package com.p1.uberfares.activities.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.SphericalUtil
import com.mobeedev.library.SlidingMenuBuilder
import com.mobeedev.library.SlidingNavigation
import com.mobeedev.library.gravity.SlideGravity
import com.p1.uberfares.R
import com.p1.uberfares.activities.MainActivity
import com.p1.uberfares.activities.driver.AdapterListener
import com.p1.uberfares.activities.driver.SlideMenuAdapter
import com.p1.uberfares.activities.driver.SlideMenuItem
import com.p1.uberfares.activities.online.OnlineSideActivity
import com.p1.uberfares.adapter.HistoryAdapter
import com.p1.uberfares.include.MyToolbar
import com.p1.uberfares.modelos.HistoryBooking
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.GeofireProvider
import com.p1.uberfares.provides.TokenProvider
import com.p1.uberfares.utils.MapUtils
import java.util.Arrays
import java.util.Collections
import java.util.Locale

class MapClientActivity : AppCompatActivity(), OnMapReadyCallback,AdapterListener {
    private var mMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mAuth: AuthProvides? = null
    private val mGeofireProvider: AuthProvides? = null
    private var mGeofireProvide: GeofireProvider? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocation: FusedLocationProviderClient? = null
    private var mCurrentLatLng: LatLng? = null
    private val mMarker: Marker? = null
    private var x: LatLng? = null
    private val mDriversMarker: MutableList<Marker> = ArrayList()
    private var mIsFirstTime = true
    private lateinit var database: DatabaseReference
    private var mAutocomplete: AutocompleteSupportFragment? = null
    private var mAutocompleteDestination: AutocompleteSupportFragment? = null
    private var mPlaces: PlacesClient? = null
    private var mOrigin: String? = null
    private var mOriginLatLng: LatLng? = null
    private var mDestination: String? = null
    private var mDestinationLatLng: LatLng? = null
    private var mButtonRequestDriver: Button? = null
    private var mTokenProvider: TokenProvider? = null
    private var OrderAdapter: HistoryAdapter? = null
    private var mOrder: List<HistoryBooking>? = null
    private var mCamaraListener: OnCameraIdleListener? = null
    lateinit var slidingNavigation: SlidingNavigation
    lateinit var imageBtn: ImageButton
    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (applicationContext != null) {
                    mCurrentLatLng = LatLng(location.latitude, location.longitude)

                    mMap!!.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(location.latitude, location.longitude))
                                .zoom(16f)
                                .build()
                        )
                    )
                    if (mIsFirstTime) {
                        mIsFirstTime = false
                        activeDrivers
                        limitSearch()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_client)


        mMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)
        mButtonRequestDriver = findViewById(R.id.btnRequestDriver)
        imageBtn = findViewById(R.id.imageButton)
        mTokenProvider = TokenProvider()
        mGeofireProvide = GeofireProvider("active_driver")
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        database = FirebaseDatabase.getInstance().reference

        mAuth = AuthProvides()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        }


        imageBtn.setOnClickListener {
            setUpMenu(savedInstanceState)
            slidingNavigation.openMenu()
        }

        mPlaces = Places.createClient(this)
        InstanceAutoCompleteOrigin()
        InstanceAutoCompleteDestination()
        OnCameraMove()
        mButtonRequestDriver!!.setOnClickListener(View.OnClickListener { requestDriver() })
        generateToken()
        mOrder = ArrayList()

        OrderAdapter = HistoryAdapter(this@MapClientActivity, mOrder as ArrayList<HistoryBooking>)




    }

    private fun requestDriver() {
        if (mOriginLatLng != null && mDestinationLatLng != null) {
            val intent = Intent(this@MapClientActivity, DetailRequestActivity::class.java)
            intent.putExtra("origin_lat", mOriginLatLng!!.latitude)
            intent.putExtra("origin_lng", mOriginLatLng!!.longitude)
            intent.putExtra("destination_lat", mDestinationLatLng!!.latitude)
            intent.putExtra("destination_lng", mDestinationLatLng!!.longitude)
            intent.putExtra("origin", mOrigin)
            intent.putExtra("destination", mDestination)
            startActivity(intent)
        } else {
            val intent = Intent(this@MapClientActivity, DetailRequestActivity::class.java)
            intent.putExtra("origin_lat", mOriginLatLng!!.latitude)
            intent.putExtra("origin_lng", mOriginLatLng!!.longitude)
            intent.putExtra("destination_lat", "no lng")
            intent.putExtra("destination_lng", "no lng")
            intent.putExtra("origin", mOrigin)
            intent.putExtra("destination", "no destination")
            startActivity(intent)

        }
    }

    private fun limitSearch() {
        val northSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000.0, 0.0)
        val southSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000.0, 180.0)
        mAutocomplete!!.setCountry("PS")
        mAutocomplete!!.setLocationBias(RectangularBounds.newInstance(southSide, northSide))
        mAutocompleteDestination!!.setCountry("PS")
        mAutocompleteDestination!!.setLocationBias(
            RectangularBounds.newInstance(
                southSide,
                northSide
            )
        )
    }

    private fun OnCameraMove() {
        mCamaraListener = OnCameraIdleListener {
            try {
                val geocoder = Geocoder(this@MapClientActivity)
                mOriginLatLng = mMap!!.cameraPosition.target
                val addressList =
                    geocoder.getFromLocation(mOriginLatLng!!.latitude, mOriginLatLng!!.longitude, 1)
                val city = addressList!![0].locality
                val country = addressList[0].countryName
                val address = addressList[0].getAddressLine(0)
                mAutocomplete!!.setText("$address $city")
                x = mOriginLatLng
                mOrigin = "$address $city"
                mAutocompleteDestination!!.setText("$address $city")
                mDestination = "$address $city"
            } catch (e: Exception) {
                Log.d("Error", "error message" + e.message)
            }


        }


    }

    private fun InstanceAutoCompleteOrigin() {
        mAutocomplete =
            supportFragmentManager.findFragmentById(R.id.placeAutoCompleteorigin) as AutocompleteSupportFragment?
        assert(mAutocomplete != null)
        mAutocomplete!!.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
        )
        mAutocomplete!!.setHint("Pickup location")
        mAutocomplete!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mOrigin = place.name
                mOriginLatLng = place.latLng
                Log.d("PLACE", "Name: $mOrigin")
                Log.d("PLACE", "Lat: " + mOriginLatLng!!.latitude)
                Log.d("PLACE", "Long: " + mOriginLatLng!!.longitude)
            }

            override fun onError(status: Status) {}
        })
    }



    private fun InstanceAutoCompleteDestination() {
        mAutocompleteDestination =
            supportFragmentManager.findFragmentById(R.id.placeAutoCompleteDestination) as AutocompleteSupportFragment?

        mAutocomplete!!.a.setTextSize(9.0f)
        mAutocompleteDestination!!.a.setTextSize(9.0f)
        mAutocompleteDestination!!.view?.findViewById<View>(R.id.places_autocomplete_search_button)
            ?.setVisibility(View.GONE)
        mAutocomplete!!.view?.findViewById<View>(R.id.places_autocomplete_search_button)
            ?.setVisibility(View.GONE)
        assert(mAutocompleteDestination != null)
        mAutocompleteDestination!!.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
        )
        mAutocompleteDestination!!.setHint("Destiny")
        mAutocompleteDestination!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mDestination = place.name
                mDestinationLatLng = place.latLng
                Log.d("PLACE", "Name: $mDestination")
                Log.d("PLACE", "Lat: " + mDestinationLatLng!!.latitude)
                Log.d("PLACE", "Long: " + mDestinationLatLng!!.longitude)
            }

            override fun onError(status: Status) {}
        })
    }// ACTUALIZAR LA POSICION DE CADA CONDUCTOR

    //ANADIR LOS MARCADORES DE LOS CONDUTORES QUE COENCTAN A LA APLICACION
    private val activeDrivers: Unit
        private get() {
            val radius = 10.0
            mGeofireProvide!!.getActiveDriver(mCurrentLatLng!!, radius)
                .addGeoQueryEventListener(object : GeoQueryEventListener {
                    override fun onKeyEntered(key: String, location: GeoLocation) {
                        //ANADIR LOS MARCADORES DE LOS CONDUTORES QUE COENCTAN A LA APLICACION
                        for (marker in mDriversMarker) {
                            if (marker.tag != null) {
                                if (marker.tag == key) {
                                    return
                                }
                            }
                        }
                        database.child("Users").child("Drivers")
                            .child(key).child("type")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val type = snapshot.getValue(String::class.java)
                                    val driverlatlng = LatLng(location.latitude, location.longitude)
                                    //val marker = mMap!!.clear()
                                    if (type == "Car") {
                                        val bitmapDescriptor =
                                            BitmapDescriptorFactory.fromBitmap(
                                                MapUtils.getCarBitmap(
                                                    this@MapClientActivity,
                                                    R.drawable.ic_car
                                                )
                                            )
                                        val marker = mMap!!.addMarker(
                                            MarkerOptions().position(driverlatlng)
                                                .title("driver available")
                                                .icon(bitmapDescriptor)
                                        )
                                        marker!!.tag = key
                                        mDriversMarker.add(marker!!)
                                    } else {
                                        val bitmapDescriptor =
                                            BitmapDescriptorFactory.fromBitmap(
                                                MapUtils.getCarBitmap(
                                                    this@MapClientActivity,
                                                    R.drawable.motorcycle
                                                )
                                            )
                                        val marker = mMap!!.addMarker(
                                            MarkerOptions().position(driverlatlng)
                                                .title("driver available")
                                                .icon(bitmapDescriptor)
                                        )
                                        marker!!.tag = key
                                        mDriversMarker.add(marker!!)
                                    }


                                }


                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
                    }

                    override fun onKeyExited(key: String) {
                        for (marker in mDriversMarker) {
                            if (marker.tag != null) {
                                if (marker.tag == key) {
                                    marker.remove()
                                    mDriversMarker.remove(marker)
                                    return
                                }
                            }
                        }
                    }

                    override fun onKeyMoved(key: String, location: GeoLocation) {
                        // ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                        for (marker in mDriversMarker) {
                            if (marker.tag != null) {
                                if (marker.tag == key) {
                                    marker.position = LatLng(location.latitude, location.longitude)
                                }
                            }
                        }
                    }

                    override fun onGeoQueryReady() {}
                    override fun onGeoQueryError(error: DatabaseError) {}
                })
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        val locationButton =
            (mMapFragment!!.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
        mMap!!.setMapStyle(styleOptions)
        val rlp = locationButton.getLayoutParams() as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 30)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(30, 30, 0, 0)
        mMap!!.setOnCameraIdleListener(mCamaraListener)
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = 5f
        startLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (gpsActived()) {
                        mFusedLocation!!.requestLocationUpdates(
                            mLocationRequest!!,
                            mLocationCallBack,
                            Looper.myLooper()
                        )
                        mMap!!.isMyLocationEnabled = true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocation!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallBack,
                    Looper.myLooper()
                )
                mMap!!.isMyLocationEnabled = true
            }
        } else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS()
        }
    }

    private fun showAlertDialogNOGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.please_activate_your_location_to_continue))
            .setPositiveButton(getString(R.string.settings)) { dialog, which ->
                startActivityForResult(
                    Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    ), SETTINGS_REQUEST_CODE
                )
            }
            .create().show()
    }

    private fun gpsActived(): Boolean {
        var isActive = false
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true
        }
        return isActive
    }

    private fun startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (gpsActived()) {
                    mFusedLocation!!.requestLocationUpdates(
                        mLocationRequest!!,
                        mLocationCallBack,
                        Looper.myLooper()
                    )
                    mMap!!.isMyLocationEnabled = true
                } else {
                    showAlertDialogNOGPS()
                }
            } else {
                checkLocationPermission()
            }
        } else {
            if (gpsActived()) {
                mFusedLocation!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallBack,
                    Looper.myLooper()
                )
                mMap!!.isMyLocationEnabled = true
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
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MapClientActivity, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ), LOCATION_REQUEST_CODE
                        )
                    }
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MapClientActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }


    override fun logout() {
        mAuth!!.logout()
        val intent = Intent(this@MapClientActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun generateToken() {
        mTokenProvider!!.create(mAuth!!.id)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        private const val SETTINGS_REQUEST_CODE = 2
    }

    override fun onBackPressed() {


        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpMenu(savedInstanceState: Bundle?) {
        val locale: Locale = resources.configuration.locales.get(0)
        val language: String = locale.language

        if (language == "ar") {
            slidingNavigation = SlidingMenuBuilder(this@MapClientActivity)
                .withMenuOpened(false)
                .withDragDistance(280) //Horizontal translation of a view. Default == 180dp
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.fragment_menu)
                .withGravity(SlideGravity.RIGHT)
                .inject()
        } else {
            slidingNavigation = SlidingMenuBuilder(this@MapClientActivity)
                .withMenuOpened(false)
                .withDragDistance(280) //Horizontal translation of a view. Default == 180dp
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.fragment_menu)
                .withGravity(SlideGravity.LEFT)
                .inject()
        }


        val tmpElements = mutableListOf(SlideMenuItem(R.drawable.wallet,getString(R.string.payment_history)), SlideMenuItem(R.drawable.road,getString(
            R.string.trip_history)), SlideMenuItem(R.drawable.add_user,getString(R.string.invite_friends)),SlideMenuItem(R.drawable.gear,getString(R.string.settings)),SlideMenuItem(R.drawable.contact,getString(R.string.connect)),SlideMenuItem(R.drawable.online_shopping,getString(R.string.online)),SlideMenuItem(R.drawable.icon_car,getString(R.string.payment_history)),SlideMenuItem(R.drawable.power,getString(R.string.log_off)))
        val adapter = SlideMenuAdapterC(tmpElements,this@MapClientActivity,this@MapClientActivity)


        val menu = findViewById<RecyclerView>(R.id.menu_recycler)
        menu.layoutManager = LinearLayoutManager(this)
        menu.isNestedScrollingEnabled = false
        menu.adapter = adapter

        val notificationRef =
            FirebaseDatabase.getInstance().reference.child("ClientBooking")
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {
                    (mOrder as ArrayList<HistoryBooking>).clear()
                    for (snapshot in datasnapshot.children) {
                        val notification = snapshot.getValue(HistoryBooking::class.java)
                        if (notification?.getIdClient() != null) {
                            if (notification?.getIdClient()
                                    .equals(FirebaseAuth.getInstance().currentUser!!.uid) && notification.getStatus()
                                    .equals("accept")
                            ) {
                                (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                                var intent =
                                    Intent(
                                        this@MapClientActivity,
                                        MapClientBookingActivity::class.java
                                    )
                                intent.putExtra("idw", notification.getIdClient())
                                intent.putExtra("idw2", "fares")
                                startActivity(intent)
                            }
                            if (notification?.getIdClient()
                                    .equals(FirebaseAuth.getInstance().currentUser!!.uid) && notification.getStatus()
                                    .equals("start")
                            ) {
                                (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                                var intent =
                                    Intent(
                                        this@MapClientActivity,
                                        MapClientBookingActivity::class.java
                                    )
                                intent.putExtra("idw", notification.getIdClient())
                                intent.putExtra("idw2", "fares")
                                startActivity(intent)
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

        database.child("Users").child("Clients")
            .child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name: String = snapshot.child("name").getValue().toString()
                        findViewById<TextView>(R.id.name_navq).setText(name)
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })

    }



}