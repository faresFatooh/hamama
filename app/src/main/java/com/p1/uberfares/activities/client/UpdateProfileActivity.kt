package com.p1.uberfares.activities.client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.activities.online.OnlineActivity
import com.p1.uberfares.include.MyToolbar
import com.p1.uberfares.provides.AuthProvides
import com.p1.uberfares.provides.ClientProvider

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var mImageViewProfile: ImageView
    private lateinit var mButtonUpdate: Button
    private lateinit var mTextViewName: TextView
    private lateinit var mTextViewAddress: TextView
    private lateinit var mTextViewPhone: TextView
    private lateinit var tv_online: TextView
    private var mClientProvider: ClientProvider? = null
    private var mAuthProvider: AuthProvides? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        mImageViewProfile = findViewById(R.id.ImageViewProfile)
        mButtonUpdate = findViewById(R.id.btnUpdateProfile)
        mTextViewName = findViewById(R.id.textInputName)
        mTextViewAddress = findViewById(R.id.textInputAddress)
        mTextViewPhone = findViewById(R.id.textInputPhone)
        tv_online = findViewById(R.id.tv_online)
        mClientProvider = ClientProvider()
        mAuthProvider = AuthProvides()
        clientInfo
        MyToolbar.show(this, "")
        mButtonUpdate.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                updateProfile()
            }
        })

        tv_online.setOnClickListener {
            startActivity(Intent(this@UpdateProfileActivity,OnlineActivity::class.java))
        }
    }

    private val clientInfo: Unit
        private get() {
            mClientProvider!!.getClient(mAuthProvider!!.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var name = snapshot.child("name").getValue(String::class.java)
                            var phone = snapshot.child("email").getValue(String::class.java)
                            var address = snapshot.child("address_name").getValue(String::class.java)
                            mTextViewName!!.setText(name)
                            mTextViewPhone!!.setText(phone)
                            mTextViewAddress!!.setText(address)
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun updateProfile() {

        var name = mTextViewName.text.toString()
        var address = mTextViewAddress.text.toString()
        if (name.isNotEmpty()&& address.isNotEmpty()){
            FirebaseDatabase.getInstance().reference.child("Users").child("Clients")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("name")
                .setValue(name).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this@UpdateProfileActivity,
                            getString(R.string.your_profile_has_been_updating_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        var intent = Intent(this@UpdateProfileActivity, MapClientActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                }
        }else{
            Toast.makeText(
                this@UpdateProfileActivity,
                getString(R.string.all_field_are_required),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}