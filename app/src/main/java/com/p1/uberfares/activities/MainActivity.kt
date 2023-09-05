package com.p1.uberfares.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.MapClientActivity
import com.p1.uberfares.activities.driver.LoginDriverActivity
import com.p1.uberfares.activities.driver.MapDriverActivity
import com.p1.uberfares.activities.driver.MapDriverActivityPinding
import com.royrodriguez.transitionbutton.TransitionButton
import com.royrodriguez.transitionbutton.TransitionButton.OnAnimationStopEndListener
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var mPref: SharedPreferences
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        mPref = getSharedPreferences("typeUser", MODE_PRIVATE)
//        Reference
        val Login = findViewById<TransitionButton>(R.id.btnclient)
        val LoginD = findViewById<TransitionButton>(R.id.btnIAmDriver)


        var currentUser = auth.currentUser
        if (currentUser != null) {
            val user = mPref!!.getString("user", "")
            if (user == "activities/client") {
                startActivity(Intent(applicationContext, MapClientActivity::class.java))
                finish()
            } else {
                title = "Hamama app"
                val progressDialog = ProgressDialog(this@MainActivity)
                progressDialog.setTitle("Hamama app")
                progressDialog.setMessage("Application is loading, please wait")
                progressDialog.setCancelable(false)
                progressDialog.show()
                FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                    .child(FirebaseAuth.getInstance().uid!!).child("dstates")
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val dstates = snapshot.getValue(String::class.java)

                            if (dstates == "denied") {
                                intent =
                                    Intent(this@MainActivity, MapDriverActivityPinding::class.java)
                                progressDialog.dismiss()
                                startActivity(intent)
                                finish()
                            } else if (dstates == "accepted") {
                                intent =
                                    Intent(this@MainActivity, MapDriverActivity::class.java)
                                progressDialog.dismiss()
                                startActivity(intent)
                                finish()
                            }


                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

            }


        }

        Login.setOnClickListener{
                // Start the loading animation when the user tap the button
            Login.startAnimation()

                // Do your networking task or background work here.
                val handler = Handler()
                handler.postDelayed(Runnable {
                    var isSuccessful = false
                    val mobileNumber = findViewById<EditText>(R.id.phonenumber)
                    var number = mobileNumber.text.toString().trim()

                    if (!number.isEmpty()) {
                        isSuccessful =true
                    }

                    // Choose a stop animation if your call was succesful or not
                    if (isSuccessful) {
                        Login.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                            OnAnimationStopEndListener {
                                var x = mPref.edit()
                                x.putString("user", "activities/client")
                                x.apply()
                                login()
                            })
                    } else {
                        Login.stopAnimation(
                            TransitionButton.StopAnimationStyle.SHAKE,
                            null
                        )
                    }
                }, 2000)

        }


        LoginD.setOnClickListener {
            LoginD.startAnimation()

            // Do your networking task or background work here.
            val handler = Handler()
            handler.postDelayed(Runnable {
                val isSuccessful = true
                // Choose a stop animation if your call was succesful or not
                if (isSuccessful) {
                    LoginD.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                        OnAnimationStopEndListener {
                            var x = mPref.edit()
                            x.putString("user", "activities/driver")
                            x.apply()
                            startActivity(Intent(applicationContext, LoginDriverActivity::class.java))

                        })
                } else {
                    LoginD.stopAnimation(
                        TransitionButton.StopAnimationStyle.SHAKE,
                        null
                    )
                }
            }, 2000)
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MapClientActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                val mobileNumber = findViewById<EditText>(R.id.phonenumber)
                var number = mobileNumber.text.toString().trim()

                var intent = Intent(applicationContext, VerifyActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("phone","+970" + number)
                startActivity(intent)
            }
        }

    }

    private fun login() {
        val mobileNumber = findViewById<EditText>(R.id.phonenumber)
        var number = mobileNumber.text.toString().trim()

        if (!number.isEmpty()) {
            val user = mPref!!.getString("user", "")
            if (user == "activities/client") {
                number = "+972" + number

            } else {
                number = "+970" + number

            }
            sendVerificationcode(number)
        } else {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}

