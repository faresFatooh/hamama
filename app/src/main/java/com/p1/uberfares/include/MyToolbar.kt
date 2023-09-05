package com.p1.uberfares.include

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.p1.uberfares.R


object MyToolbar {

    fun show(activity: AppCompatActivity, title: String?) {
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.title = title
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationIcon(R.drawable.pngegg)

    }
}