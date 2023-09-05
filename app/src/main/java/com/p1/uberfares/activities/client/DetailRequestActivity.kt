package com.p1.uberfares.activities.client

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.SquareCap
import com.p1.uberfares.R
import com.p1.uberfares.provides.GoogleApiProvider
import com.p1.uberfares.utils.DecodePoints.decodePoly
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailRequestActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mExtraOriginLat = 0.0
    private var mExtraOriginLng = 0.0
    private var mExtraDestinationLat = 0.0
    private var mExtraDestinationLng = 0.0
    private var mExtraOrigin: String? = null
    private var mExtraDestination: String? = null
    private var mOriginLatLng: LatLng? = null
    private var mDestinationLatLng: LatLng? = null
    private var type: String? = null
    private var mGoogleApiProvider: GoogleApiProvider? = null
    private var mPolylineList: List<LatLng>? = null
    private var mPolylineOptions: PolylineOptions? = null

    private lateinit var mRlRechargeAlipay: ConstraintLayout
    private lateinit var mRlRechargeWechat: ConstraintLayout
    private lateinit var mButtonRequest: Button

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_request)
        mMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)
        mExtraOriginLat = intent.getDoubleExtra("origin_lat", 0.0)
        mExtraOriginLng = intent.getDoubleExtra("origin_lng", 0.0)
        mExtraDestinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        mExtraDestinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        mOriginLatLng = LatLng(mExtraOriginLat, mExtraOriginLng)
        mDestinationLatLng = LatLng(mExtraDestinationLat, mExtraDestinationLng)
        mGoogleApiProvider = GoogleApiProvider(this@DetailRequestActivity)
        // mTextViewDestination = findViewById(R.id.textViewDestination)
        mExtraOrigin = intent.getStringExtra("origin")
        mExtraDestination = intent.getStringExtra("destination")
        mButtonRequest = findViewById(R.id.btnRequestNow)
//        mRgRecharge = findViewById(R.id.mRgRecharge)
        type = "Motorcycle"
        mRlRechargeWechat = findViewById(R.id.mRlRechargeWechat)
        mRlRechargeAlipay = findViewById(R.id.mRlRechargeAlipay)
        val myLayout = findViewById<LinearLayout>(R.id.linearLayout3)
        myLayout.visibility = View.VISIBLE
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_up_animation)
        myLayout.startAnimation(animation)
        mRlRechargeWechat.setOnClickListener {

            val colorStateList =
                ColorStateList.valueOf(getColor(R.color.colorAccent)) // تعيين لون الخلفية هنا (في هذا المثال اللون الأحمر)
            mRlRechargeWechat.setBackgroundTintList(colorStateList)

            val colorStateList2 =
                ColorStateList.valueOf(getColor(R.color.d6d6d6)) // تعيين لون الخلفية هنا (في هذا المثال اللون الأحمر)
            mRlRechargeAlipay.setBackgroundTintList(colorStateList2)
            type = "Car"
        }

        mRlRechargeAlipay.setOnClickListener {

            val colorStateList12 =
                ColorStateList.valueOf(getColor(R.color.d6d6d6)) // تعيين لون الخلفية هنا (في هذا المثال اللون الأحمر)
            mRlRechargeWechat.setBackgroundTintList(colorStateList12)

            val colorStateList13 =
                ColorStateList.valueOf(getColor(R.color.colorAccent)) // تعيين لون الخلفية هنا (في هذا المثال اللون الأحمر)
            mRlRechargeAlipay.setBackgroundTintList(colorStateList13)
            type = "Motorcycle"
        }




        mButtonRequest.setOnClickListener(
            { goToRequestDiver() }
        )

    }

    private fun goToRequestDiver() {
        val intent = Intent(this@DetailRequestActivity, RequestDriverActivity::class.java)
        intent.putExtra("origin_lat", mOriginLatLng!!.latitude)
        intent.putExtra("origin_lng", mOriginLatLng!!.longitude)
        intent.putExtra("origin", mExtraOrigin)
        intent.putExtra("destination", mExtraDestination)
        intent.putExtra("destination_lat", mDestinationLatLng!!.latitude)
        intent.putExtra("destination_lng", mDestinationLatLng!!.longitude)
        intent.putExtra("type", type)
        startActivity(intent)
        finish()
    }

    private fun drawRoute() {
        mGoogleApiProvider!!.getDirections(mOriginLatLng!!, mDestinationLatLng!!)!!
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        val jsonObject = JSONObject(response.body())
                        val jsonArray = jsonObject.getJSONArray("routes")
                        val route = jsonArray.getJSONObject(0)
                        val polylines = route.getJSONObject("overview_polyline")
                        val points = polylines.getString("points")
                        mPolylineList = decodePoly(points)
                        mPolylineOptions = PolylineOptions()
                        mPolylineOptions!!.color(Color.DKGRAY)
                        mPolylineOptions!!.width(8f)
                        mPolylineOptions!!.startCap(SquareCap())
                        mPolylineOptions!!.jointType(JointType.ROUND)
                        mPolylineOptions!!.addAll(mPolylineList!!)
                        mMap!!.addPolyline(mPolylineOptions!!)
                        val legs = route.getJSONArray("legs")
                        val leg = legs.getJSONObject(0)
                        val distance = leg.getJSONObject("distance")
                        val duration = leg.getJSONObject("duration")
                        val distanceText = distance.getString("text")
                        val durationText = duration.getString("text")
                    } catch (e: Exception) {
                        Log.d("Error", "bug found" + e.message)
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {}
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_stule)
        mMap!!.setMapStyle(styleOptions)
        mMap!!.addMarker(
            MarkerOptions().position(mOriginLatLng!!).title("Origen")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_100))
        )
        mMap!!.addMarker(
            MarkerOptions().position(mDestinationLatLng!!).title("Destino")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin))
        )
        mMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(mOriginLatLng!!)
                    .zoom(14f).build()
            )
        )
        drawRoute()
    }

}