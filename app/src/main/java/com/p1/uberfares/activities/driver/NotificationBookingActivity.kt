package com.p1.uberfares.activities.driver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.ClientBookingProvider
import com.p1.uberfares.provides.GeofireProvider

class NotificationBookingActivity : AppCompatActivity() {
    private lateinit var mTextViewDestination: TextView
    private lateinit var mTextViewOrigin: TextView
    private lateinit var mTextViewMin: TextView
    private lateinit var mTextViewDistance: TextView
    private lateinit var mTextViewCounter: TextView
    private lateinit var mButtonAccept: Button
    private lateinit var mButtonCancel: Button
    private var mClientBookingProvider: ClientBookingProvider? = null
    private var mGeofireProvider: GeofireProvider? = null
    private var mAuthProvider: AuthProvides? = null
    private var mExtraIdClient: String? = null
    private var mExtraOrigin: String? = null
    private var mExtraDestination: String? = null
    private var mExtraDistance: String? = null
    private var mExtraMin: String? = null
    private lateinit var mMediaPlayer: MediaPlayer
    private var mCounter: Int = 120
    private var mHandler: Handler? = null
    var runnable: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        public override fun run() {
            mCounter = mCounter - 1
            mTextViewCounter!!.setText(mCounter.toString())
            checkIfClientCancelBooking()
            if (mCounter > 0) {
                initTime()
            } else {
                cancelBooking()
            }
        }
    }
    private var mListener: ValueEventListener? = null
    private fun initTime() {
        mHandler = Handler()
        mHandler!!.postDelayed(runnable, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_booking)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        mTextViewDestination = findViewById(R.id.textViewDestination)
        mTextViewOrigin = findViewById(R.id.textViewOrigin)
        mTextViewMin = findViewById(R.id.textViewMin)
        mTextViewCounter = findViewById(R.id.textViewCounter)
        mTextViewDistance = findViewById(R.id.textViewDistance)
        mButtonAccept = findViewById(R.id.btnAcceptBooking)
        mButtonCancel = findViewById(R.id.btnCancelBooking)
        val sharedPreference = getSharedPreferences("Clients", Context.MODE_PRIVATE)
        mExtraIdClient = sharedPreference.getString("IdClient", "")
        mExtraOrigin = getIntent().getStringExtra("origin")
        mExtraDestination = getIntent().getStringExtra("destination")
        mExtraDistance = getIntent().getStringExtra("distance")
        mExtraMin = getIntent().getStringExtra("min")
        mTextViewDestination.setText(mExtraDestination)
        mTextViewOrigin.setText(mExtraOrigin)
        mTextViewDistance.setText(mExtraDistance)
        mTextViewMin.setText(mExtraMin)
        mClientBookingProvider = ClientBookingProvider()
        mMediaPlayer = MediaPlayer.create(this, R.raw.ringtone)
        mMediaPlayer.setLooping(true)
        getWindow().addFlags(
            (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        )
        initTime()
        mButtonAccept.setOnClickListener {
            acceptBooking()
            // FService.stpService(this)

        }

        mButtonCancel.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                cancelBooking()
            }
        })
    }

    private fun checkIfClientCancelBooking() {
        FirebaseDatabase.getInstance().reference.child("ClientBooking").child(mExtraIdClient.toString())
            .addValueEventListener(object : ValueEventListener {
                public override fun onDataChange(snapshot: DataSnapshot) {
                   var s =snapshot.child("status").getValue(String::class.java)
                    if (s.equals("cancel")){
                            Toast.makeText(
                                this@NotificationBookingActivity,
                                getString(R.string.the_client_canceled_the_trip),
                                Toast.LENGTH_LONG
                            ).show()
                            if (mHandler != null) mHandler!!.removeCallbacks(runnable)
                            val intent =
                                Intent(this@NotificationBookingActivity, MapDriverActivity::class.java)
                            startActivity(intent)
                            finish()
                    }
                }

                public override fun onCancelled(error: DatabaseError) {}
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelBooking() {
        if (mHandler != null) mHandler!!.removeCallbacks(runnable)
        mClientBookingProvider!!.updateStatus(mExtraIdClient, "cancel")
        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(2)
        val intent = Intent(this@NotificationBookingActivity, MapDriverActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptBooking() {
        if (mHandler != null) mHandler!!.removeCallbacks(runnable)
        mAuthProvider = AuthProvides()
        mGeofireProvider = GeofireProvider("active_driver")
        mGeofireProvider!!.removeLocation(mAuthProvider!!.id)
        mClientBookingProvider = ClientBookingProvider()
        mClientBookingProvider!!.updateStatus(mExtraIdClient, "accept")
        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(2)
        val intent1 = Intent(this@NotificationBookingActivity, MapDriverBookingActivity::class.java)
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent1.setAction(Intent.ACTION_RUN)
        intent1.putExtra("idClient", mExtraIdClient)

        startActivity(intent1)
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.pause()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.release()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mMediaPlayer != null) {
            if (!mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null) mHandler!!.removeCallbacks(runnable)
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.pause()
            }
        }
        if (mListener != null) {
            mClientBookingProvider!!.getClientBooking(mExtraIdClient)
        }
    }
}