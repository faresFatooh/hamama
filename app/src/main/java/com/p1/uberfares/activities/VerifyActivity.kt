package com.p1.uberfares.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.MapClientActivity
import com.p1.uberfares.activities.driver.MapDriverActivity
import com.p1.uberfares.activities.driver.MapDriverActivityPinding
import com.p1.uberfares.modelos.Client
import com.p1.uberfares.modelos.Drivee
import com.p1.uberfares.retrofit.RetrofitClient
import java.util.Timer
import kotlin.concurrent.schedule

class VerifyActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var riderRef: DatabaseReference
    private lateinit var driverRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var button: Button
    private lateinit var tv: TextView
    private lateinit var listener: FirebaseAuth.AuthStateListener
    lateinit var mPref: SharedPreferences
    var name: String? = null
    var name2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        firebaseAuth = FirebaseAuth.getInstance()
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE)

        name = "Car"
        name2 = "Gaza"

        button = findViewById(R.id.verifyBtn)
        tv = findViewById<TextView>(R.id.vjk)
        tv.text = (tv.text.toString() + " " + intent.getStringExtra("phone").toString())
        val otpGiven = findViewById<EditText>(R.id.id_otp)
        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        button.setOnClickListener {
            var otp = otpGiven.text.toString().trim()
            if (!otp.isEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        listener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {

                RetrofitClient.riderKey = it.currentUser!!.uid
                checkUserFromFirebase()

            } else {
                Toast.makeText(this, "s", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, it.currentUser.toString(), Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: String? = mPref!!.getString("user", "")
                    if (user == "activities/client") {
                        init()
                        Timer().schedule(3000) {
                            firebaseAuth.addAuthStateListener(listener)
                        }


                    } else {
                        init()
                        Timer().schedule(3000) {
                            firebaseAuth.addAuthStateListener(listener)
                        }
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun init() {
        database = FirebaseDatabase.getInstance()
        riderRef = database.getReference(RetrofitClient.Users)
        driverRef = database.getReference(RetrofitClient.Users)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun checkUserFromFirebase() {
        val user = mPref!!.getString("user", "")
        if (user == "activities/client") {
            riderRef.child(RetrofitClient.Clients).child(firebaseAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(Client::class.java)
                            goToHomeActivityC(model!!)
                        } else {
                            showRegisterLayout()


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@VerifyActivity, error.message, Toast.LENGTH_LONG).show()
                    }

                })


        } else {
            driverRef.child(RetrofitClient.Drivers).child(firebaseAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(Drivee::class.java)
                            goToHomeActivityD(model!!)
                        } else {
                            showRegisterLayout()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@VerifyActivity, error.message, Toast.LENGTH_LONG).show()
                    }

                })

        }


    }


    private fun showRegisterLayout() {

        val builder = AlertDialog.Builder(this, R.style.AppTheme)

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)

        val edt_first_name = itemView.findViewById<TextInputLayout>(R.id.full_name)
        val edt_phone_number = itemView.findViewById<TextInputLayout>(R.id.register_mobile_num_til)
        val register_Id_num = itemView.findViewById<TextInputLayout>(R.id.register_Id_num)
        val register_address = itemView.findViewById<TextInputLayout>(R.id.register_address)
        val register_color = itemView.findViewById<TextInputLayout>(R.id.register_color)
        val register_car_type = itemView.findViewById<TextInputLayout>(R.id.register_car_type)
        val register_registration =
            itemView.findViewById<TextInputLayout>(R.id.register_registration)
        val spinner_region = itemView.findViewById<Spinner>(R.id.spinner_region)
        val btn_continue = itemView.findViewById<Button>(R.id.btn_register)
        val lay1 = itemView.findViewById<View>(R.id.lay1)
        val radiobtn = itemView.findViewById<RadioGroup>(R.id.radiobtn)

        radiobtn.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.car == checkedId) {
                name = "Car"
                lay1.visibility = View.VISIBLE
            } else if (R.id.motorcycle == checkedId) {
                name = "Motorcycle"
                lay1.visibility = View.GONE
            }
        }
        val languages = resources.getStringArray(R.array.Languages)

        if (spinner_region != null) {
            val adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, languages
            )
            spinner_region.adapter = adapter
            spinner_region.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    name2 = languages[position].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        edt_phone_number.editText!!.setText(firebaseAuth.currentUser!!.phoneNumber)
        val user = mPref!!.getString("user", "")
        if (user == "activities/client") {
            register_Id_num.visibility = View.GONE
            register_address.visibility = View.GONE
            register_color.visibility = View.GONE
            register_car_type.visibility = View.GONE
            register_registration.visibility = View.GONE
            spinner_region.visibility = View.GONE
            lay1.visibility = View.GONE
            radiobtn.visibility = View.GONE
        }


        builder.setView(itemView)
        val dialog = builder.create()
        dialog.show()

        btn_continue.setOnClickListener {
            val user = mPref!!.getString("user", "")
            if (user == "activities/client") {

                if (edt_first_name.editText!!.text.toString()
                        .isEmpty() || edt_phone_number.editText!!.text.toString().isEmpty()
                ) {
                    edt_first_name.error = "error"
                    edt_phone_number.error = "error"
                } else {
                    val model = Client()
                    model.name = edt_first_name.editText!!.text.toString()
                    model.email = edt_phone_number.editText!!.text.toString()
                    model.id = firebaseAuth.currentUser!!.uid


                    riderRef.child(RetrofitClient.Clients).child(RetrofitClient.riderKey)
                        .setValue(model).addOnFailureListener { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }.addOnSuccessListener {
                            goToHomeActivityC(model)


                        }
                    dialog.dismiss()
                }

            } else {
                if (edt_first_name.editText!!.text.toString()
                        .isEmpty() || edt_phone_number.editText!!.text.toString()
                        .isEmpty() || register_Id_num.editText!!.text.toString()
                        .isEmpty() || register_address.editText!!.text.toString()
                        .isEmpty() || register_registration.editText!!.text.toString().isEmpty()
                ) {
                    edt_first_name.error = "error"
                    edt_phone_number.error = "error"
                    register_address.error = "error"
                    register_color.error = "error"
                    if (name == "Car") {
                        if (register_color.editText!!.text.toString()
                                .isEmpty() || register_car_type.editText!!.text.toString().isEmpty()
                        ) {
                            register_color.error = "error"
                            register_car_type.error = "error"
                        }

                    }


                } else {

                    val model = Drivee()
                    model.name = edt_first_name.editText!!.text.toString()
                    model.phone = edt_phone_number.editText!!.text.toString()
                    model.address = register_address.editText!!.text.toString()
                    model.registration = register_registration.editText!!.text.toString()
                    model.mius = "0.00"
                    model.id = firebaseAuth.currentUser!!.uid
                    model.idNumber = register_Id_num.editText!!.text.toString()
                    model.region = name2.toString()
                    model.model = register_car_type.editText!!.text.toString()
                    model.type = name.toString()
                    model.color = register_color.editText!!.text.toString()
                    model.dstates = "denied"


                    riderRef.child(RetrofitClient.Drivers).child(RetrofitClient.riderKey)
                        .setValue(model).addOnFailureListener { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }.addOnSuccessListener {
                            goToHomeActivityD(model)


                        }
                    dialog.dismiss()
                }

            }
        }


    }


    private fun goToHomeActivityD(model: Drivee) {
        RetrofitClient.currentDrive = model
        if (firebaseAuth!!.currentUser != null) {
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                .child(FirebaseAuth.getInstance().uid!!).child("dstates")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val dstates = snapshot.getValue(String::class.java)
                        if (dstates == "denied") {
                            intent =
                                Intent(this@VerifyActivity, MapDriverActivityPinding::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else if (dstates == "accepted") {
                            intent = Intent(this@VerifyActivity, MapDriverActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


        } else {
            Toast.makeText(this@VerifyActivity, "new user 3", Toast.LENGTH_SHORT).show()

        }

    }

    private fun goToHomeActivityC(model: Client) {
        RetrofitClient.currentClient = model
        if (firebaseAuth!!.currentUser != null) {

            intent = Intent(this, MapClientActivity::class.java)

            startActivity(intent)
            finishAffinity()
        } else {
            Toast.makeText(this@VerifyActivity, "new user 3", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onDestroy() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            firebaseAuth.removeAuthStateListener(listener)
        }, 3000)
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@VerifyActivity, MainActivity::class.java))
        finishAffinity()


    }


}