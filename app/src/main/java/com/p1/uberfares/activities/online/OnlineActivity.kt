package com.p1.uberfares.activities.online

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.MapClientActivity

class OnlineActivity : AppCompatActivity() {
    private lateinit var online_mobile_num_til: TextInputLayout
    private lateinit var page_name: TextInputLayout
    private lateinit var online_address: TextInputLayout
    private lateinit var btn_register_online: Button
    var name2: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        online_mobile_num_til = findViewById(R.id.online_mobile_num_til)
        page_name = findViewById(R.id.page_name)
        online_address = findViewById(R.id.online_address)
        btn_register_online = findViewById(R.id.btn_register_online)
        name2 = "Gaza"
        val languages = resources.getStringArray(R.array.Languages)

        val spinner_region = findViewById<Spinner>(R.id.spinner_region_online)
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

        btn_register_online.setOnClickListener {

            if (online_mobile_num_til.editText!!.text.isNotEmpty() && page_name.editText!!.text.isNotEmpty() && online_address.editText!!.text.isNotEmpty()) {

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("online")
                    .setValue("online working")

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("store_name")
                    .setValue(page_name.editText!!.text.toString())

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("online_phone")
                    .setValue(online_mobile_num_til.editText!!.text.toString())

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("online Address")
                    .setValue(online_address.editText!!.text.toString())

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child("Clients")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("online region")
                    .setValue(spinner_region.selectedItem.toString())

                var intent = Intent(this@OnlineActivity, MapClientActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                online_mobile_num_til.error = "error"
                page_name.error = "error"
                online_address.error = "error"
            }
        }


    }
}