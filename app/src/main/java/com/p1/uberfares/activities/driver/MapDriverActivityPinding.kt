package com.p1.uberfares.activities.driver

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.services.FService

class MapDriverActivityPinding : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_driver_pinding)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        if ("xiaomi" == Build.MANUFACTURER.lowercase()) {
                            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                            intent.setClassName(
                                "com.miui.securitycenter",
                                "com.miui.permcenter.permissions.PermissionsEditorActivity"
                            )
                            intent.putExtra("extra_pkgname", packageName)
                            AlertDialog.Builder(this)
                                .setTitle("Please Enable the additional permissions")
                                .setMessage("You will not receive notifications while the app is in background if you disable these permissions")
                                .setPositiveButton(
                                    "Go to Settings"
                                ) { dialog, which -> startActivity(intent) }
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setCancelable(false)
                                .show()
                        } else {
                            val overlaySettings = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                                    "package:$packageName"
                                )
                            )
                            startActivityForResult(overlaySettings, 123)
                        }
                    }
                }
            }
        }

        FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
            .child(FirebaseAuth.getInstance().uid!!).child("dstates")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val dstates = snapshot.getValue(String::class.java)
                    if (dstates == "denied") {
                        Toast.makeText(this@MapDriverActivityPinding,"denied",Toast.LENGTH_LONG).show()
                        if (FService.IS_RUNNING){
                            FService.stpService(this@MapDriverActivityPinding)

                        }
                        FirebaseDatabase.getInstance().reference.child("drivers_working")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (!FService.IS_RUNNING){
                                        snapshot.child(FirebaseAuth.getInstance().uid.toString()).ref.removeValue();

                                    }
                                }
                            })

                        FirebaseDatabase.getInstance().reference.child("active_driver")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (!FService.IS_RUNNING){
                                        snapshot.child(FirebaseAuth.getInstance().uid.toString()).ref.removeValue();

                                    }

                                }
                            })

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })




    }


}