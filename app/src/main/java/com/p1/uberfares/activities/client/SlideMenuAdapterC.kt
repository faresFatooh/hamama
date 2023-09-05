package com.p1.uberfares.activities.client

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p1.uberfares.R
import com.p1.uberfares.activities.driver.AdapterListener
import com.p1.uberfares.activities.driver.HistoryDriverActivity
import com.p1.uberfares.activities.driver.SlideMenuItem
import com.p1.uberfares.activities.online.OnlineActivity
import com.p1.uberfares.activities.online.OnlineSideActivity

class SlideMenuAdapterC(
    val menuElements: MutableList<SlideMenuItem>,
    context: Context,
    listener: AdapterListener
) :
    RecyclerView.Adapter<SlidingMenuViewHolder>() {
    val context = context
    val listener = listener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SlidingMenuViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.menu_element, parent, false
        )
    )

    override fun getItemCount() = menuElements.size

    override fun onBindViewHolder(holder: SlidingMenuViewHolder, position: Int) {
        holder.bind(menuElements[position])
        holder.itemView.setOnClickListener {
            when (position) {
                0 -> {
                    context.startActivity(Intent(context, RestaurantClientActivity::class.java))
                }

                1 -> {
                    context.startActivity(Intent(context, HistoryClientActivity::class.java))
                }

                2 -> {
                    context.startActivity(Intent(context, HistoryDriverActivity::class.java))
                }

                3 -> {
                    context.startActivity(Intent(context, UpdateProfileActivity::class.java))
                }

                4 -> {
                    context.startActivity(Intent(context, HistoryDriverActivity::class.java))
                }

                5 -> {
                    context.startActivity(Intent(context, OnlineSideActivity::class.java))
                }
                6 ->{
                    context.startActivity(Intent(context, PayActivity::class.java))
                }
                7 ->{
                    listener.logout()
                }
            }


        }
    }


}

class SlidingMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: SlideMenuItem) = with(itemView) {

        findViewById<ImageView>(R.id.element_picture).setImageResource(item.image)
        findViewById<TextView>(R.id.element_text).text = item.text
    }
}
