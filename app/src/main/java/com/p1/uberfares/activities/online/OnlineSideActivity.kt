package com.p1.uberfares.activities.online

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.MapClientActivity
import java.time.LocalDateTime
import java.util.*

class OnlineSideActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var full_name_c: TextInputLayout
    private lateinit var register_mobile_num_til_c: TextInputLayout
    private lateinit var register_address_c: TextInputLayout
    private lateinit var register_c_price: TextInputLayout
    private lateinit var btn_register_c: Button
    private lateinit var radiobtn_c: RadioGroup
    private lateinit var spinner_region_c: Spinner
    private lateinit var name: String
    var name2: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_side)
        full_name_c = findViewById(R.id.full_name_c)
        register_mobile_num_til_c = findViewById(R.id.register_mobile_num_til_c)
        register_address_c = findViewById(R.id.register_address_c)
        register_c_price = findViewById(R.id.register_c_price)
        btn_register_c = findViewById(R.id.btn_register_c)
        radiobtn_c = findViewById(R.id.radiobtn_c)
        spinner_region_c = findViewById(R.id.spinner_region_c)
        name2 = "Gaza"
        val languages = resources.getStringArray(R.array.Languages)

        val spinner_region = spinner_region_c
        if (spinner_region != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, languages
            )
            spinner_region.adapter = adapter
            spinner_region.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    name2 = languages[position].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        val mDatabase = FirebaseDatabase.getInstance().getReference("Users")

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rface = dataSnapshot.child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("online")
                    .getValue(String::class.java)

                if (!rface.equals("online working")) {
                    startActivity(Intent(this@OnlineSideActivity, NoOnlineActivity::class.java))
                    finish()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        radiobtn_c.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.yes_c == checkedId) {
                name = "Yes"
            } else if (R.id.no_c == checkedId) {
                name = "No"

            }
        }

        btn_register_c.setOnClickListener {
            var random = UUID.randomUUID().toString()
            if (full_name_c.editText!!.text.toString()
                    .isNotEmpty() && register_mobile_num_til_c.editText!!.text.toString()
                    .isNotEmpty() && register_address_c.editText!!.text.toString()
                    .isNotEmpty() && register_c_price.editText!!.text.toString().isNotEmpty()
            ) {
                val calendar = Calendar.getInstance()

                val current = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
                )
                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("cname")
                    .setValue(full_name_c.editText!!.text.toString())

                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("cphone")
                    .setValue(register_mobile_num_til_c.editText!!.text.toString())


                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("caddress")
                    .setValue(register_address_c.editText!!.text.toString())


                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("cprice")
                    .setValue(register_c_price.editText!!.text.toString())


                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("cregion")
                    .setValue(name2.toString())

                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("crewind")
                    .setValue(name.toString())


                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("storeId")
                    .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("date")
                    .setValue("${current.year} / ${current.month} / ${current.dayOfMonth}")

                FirebaseDatabase.getInstance().reference.child("Online")
                    .child(random)
                    .child("time")
                    .setValue("${current.hour} : ${current.minute} : ${current.second}")


                val hour = current.hour
                val minute = current.minute
                if (hour >= 0 && hour <= 12) {
                    if (hour == 12 && minute <= 10) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("تم استلام طلبك")
                        builder.setMessage("سيتم تسليم طلبك اليوم")
                        builder.setCancelable(false)

                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            var intent =
                                Intent(this@OnlineSideActivity, MapClientActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        builder.show()

                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("تم استلام طلبك")
                        builder.setMessage("سيتم تسليم طلبك غدا")
                        builder.setCancelable(false)


                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            var intent =
                                Intent(this@OnlineSideActivity, MapClientActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        builder.show()
                    }


                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("تم استلام طلبك")
                    builder.setMessage("سيتم تسليم طلبك غدا")
                    builder.setCancelable(false)

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        var intent = Intent(this@OnlineSideActivity, MapClientActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    builder.show()

                }
            }
        }


    }
}