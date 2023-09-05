package com.p1.uberfares.activities.driver

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import com.p1.uberfares.activities.MainActivity
import com.p1.uberfares.activities.VerifyActivity
import com.p1.uberfares.activities.client.MapClientActivity
import java.util.concurrent.TimeUnit

class LoginDriverActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var mPref: SharedPreferences
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_driver)
        auth = FirebaseAuth.getInstance()
        mPref = getSharedPreferences("typeUser", MODE_PRIVATE)
//        Reference
        val Login = findViewById<Button>(R.id.btndlient)


        var currentUser = auth.currentUser
        if (currentUser != null) {
            val user = mPref!!.getString("user", "")
            if (user == "activities/client") {
                startActivity(Intent(applicationContext, MapClientActivity::class.java))
                finish()
            } else {
                title = "Hamama app"
                val progressDialog = ProgressDialog(this@LoginDriverActivity)
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
                                    Intent(this@LoginDriverActivity, MapDriverActivityPinding::class.java)
                                progressDialog.dismiss()
                                startActivity(intent)
                                finish()
                            } else if (dstates == "accepted") {
                                intent =
                                    Intent(this@LoginDriverActivity, MapDriverActivity::class.java)
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

        Login.setOnClickListener {
            var x = mPref.edit()
            x.putString("user", "activities/driver")
            x.apply()
            login()
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
                val mobileNumber = findViewById<EditText>(R.id.phonenumberd)
                var number = mobileNumber.text.toString().trim()
                var intent = Intent(applicationContext, VerifyActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("phone","+970" + number)
                startActivity(intent)
            }
        }

    }

    private fun login() {
        val mobileNumber = findViewById<EditText>(R.id.phonenumberd)
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
        startActivity(Intent(this@LoginDriverActivity,MainActivity::class.java))
        finish()
    }


}
