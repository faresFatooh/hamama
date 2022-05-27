package com.p1.uber.activities.client;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.p1.uber.R;
import com.p1.uber.provides.AuthProvides;
import com.p1.uber.provides.ClientBookingProvider;
import com.p1.uber.provides.DriverProvider;
import com.p1.uber.provides.GeofireProvider;
import com.p1.uber.provides.GoogleApiProvider;
import com.p1.uber.provides.TokenProvider;
import com.p1.uber.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvides mAuth;
    private AuthProvides mGeofireProvider;
    private GeofireProvider mGeofireProvide;

    private Marker mMarkerDriver;

    private boolean mIsFirstTime=true;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;
    private LatLng mDriverLatLng;
    private TokenProvider mTokenProvider;

    private ClientBookingProvider mClientBookingProvider;
    private DriverProvider mDriverProvider;
    private TextView mTextViewClientBooking;
    private TextView mTextViewEmailClientBooking;
    private TextView mTextViewOriginClientBooking;
    private TextView mTextViewDestinationClientBooking;
    private TextView mTextViewStatusBooking;

    private GoogleApiProvider mGoogleApiProvider;

    private List mPolylineList;
    private PolylineOptions mPolylineOptions;

    private ValueEventListener mListener;
    private String mIdDriver;
    private ValueEventListener mListenerStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);
        mMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mGeofireProvide = new GeofireProvider("drivers_working");
        mGoogleApiProvider = new GoogleApiProvider(MapClientBookingActivity.this);
        mDriverProvider = new DriverProvider();

        mAuth= new AuthProvides();
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }

        mTextViewClientBooking = findViewById(R.id.textViewDriverBooking);
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailDriverBooking);
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginDriverBooking);
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationDriverBooking);
        mTextViewStatusBooking = findViewById(R.id.textViewStatusBooking);
        getStatus();
        getClientBooking();

    }

    private void getStatus() {
        mListenerStatus = mClientBookingProvider.getstatus(mAuth.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue().toString();

                    if (status.equals("accept")){
                        mTextViewStatusBooking.setText("Estado: Aceptado");
                    }
                    if (status.equals("start")){
                        mTextViewStatusBooking.setText("Estado: Viaje Iniciado");
                        startBooking();
                    }
                    else if (status.equals("finish")){
                        mTextViewStatusBooking.setText("Estado: Viaje Finalizado");
                        finishBooking();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void finishBooking() {
        Intent intent = new Intent(MapClientBookingActivity.this, CalificationDriverActivity.class);
        startActivity(intent);
        finish();
    }

    private void startBooking() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin)));
        drawRoute(mDestinationLatLng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mGeofireProvide.getDriverLocation(mIdDriver).removeEventListener(mListener);
        }
        if (mListenerStatus != null){
            mClientBookingProvider.getstatus(mAuth.getId()).removeEventListener(mListenerStatus);
        }
    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mAuth.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String destination = snapshot.child("destination").getValue().toString();
                    String origin = snapshot.child("origin").getValue().toString();
                    String idDriver = snapshot.child("idDriver").getValue().toString();
                    mIdDriver = idDriver;
                    double destinationLat = Double.parseDouble(snapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(snapshot.child("destinationLng").getValue().toString());

                    double originLat = Double.parseDouble(snapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(snapshot.child("originLng").getValue().toString());
                    mOriginLatLng = new LatLng(originLat,originLng);
                    mDestinationLatLng = new LatLng(destinationLat,destinationLng);
                    mTextViewOriginClientBooking.setText("recoger en: " + origin);
                    mTextViewDestinationClientBooking.setText("destino: " + destination);
                    mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_100)));
                    getDriver(idDriver);
                    getDriverLocation(idDriver);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDriver(String idDriver) {
        mDriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    mTextViewClientBooking.setText(name);
                    mTextViewEmailClientBooking.setText(email);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDriverLocation(String idDriver) {
        mListener = mGeofireProvide.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double lat = Double.parseDouble(snapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(snapshot.child("1").getValue().toString());
                    mDriverLatLng = new LatLng(lat,lng);
                    if (mMarkerDriver != null ){
                        mMarkerDriver.remove();
                    }
                    mMarkerDriver = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(lat,lng))
                            .title("Tu conductor")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_50)));
                            if (mIsFirstTime){
                                mIsFirstTime = false;
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                        new CameraPosition.Builder().target(mDriverLatLng)
                                                .zoom(14f).build()
                                ));
                                drawRoute(mOriginLatLng);
                            }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void drawRoute(LatLng latLng){
        mGoogleApiProvider.getDirections(mDriverLatLng, latLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray =  jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(8f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");




                } catch (Exception e) {
                    Log.d("Error", "Error encontrado" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

}