package com.p1.uberfares.activities.client

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.p1.uberfares.R

class CheckOutActivity : AppCompatActivity() {
    private var mSuccessAnimation: LottieAnimationView? = null
    private var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        mp = MediaPlayer.create(this, R.raw.success_sound)
        mSuccessAnimation = findViewById(R.id.successAnim)
        mSuccessAnimation!!.setSpeed(0.8f)
        mSuccessAnimation!!.playAnimation()
//        if (!mp!!.isPlaying()) {
        mp!!.start()
//        } else {
        mp!!.stop()
        //}

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@CheckOutActivity, RestaurantClientActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSuccessAnimation!!.cancelAnimation()
    }

}