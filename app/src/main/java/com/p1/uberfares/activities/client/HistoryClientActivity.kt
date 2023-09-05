package com.p1.uberfares.activities.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.p1.uberfares.R
import com.p1.uberfares.helper.PageAdapterC

class HistoryClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_client)
        val viewPager = findViewById<ViewPager>(R.id.viewPager2)
        viewPager.adapter = PageAdapterC(supportFragmentManager,this@HistoryClientActivity)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout2)
        tabLayout.setupWithViewPager(viewPager)
    }
}