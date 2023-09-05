package com.p1.uberfares.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.OrderDitailsActivity
import com.p1.uberfares.modelos.UsersOrderHistory

class UsersOrderHistoryAdapter
    (
    mContext: Context,
    mHistory: List<UsersOrderHistory>
) : RecyclerView.Adapter<UsersOrderHistoryAdapter.ViewHolder?>() {
    private val mContext: Context
    private val mHistory: List<UsersOrderHistory>

    init {
        this.mHistory = mHistory
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.order_h_item, parent, false)
        return UsersOrderHistoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order: UsersOrderHistory = mHistory[position]
        holder.tv_s.text = order!!.states
        holder.tv_num.text = "Order number ${position + 1}"
        holder.itemView.setOnClickListener {
            var Intent = Intent(mContext, OrderDitailsActivity::class.java)
            Intent.putExtra("ran", order!!.ran)
            mContext.startActivity(Intent)
        }


    }

    override fun getItemCount(): Int {
        return mHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var tv_s: TextView
        var tv_num: TextView


        init {
            tv_s = itemView.findViewById(R.id.tv_s)
            tv_num = itemView.findViewById(R.id.tv_num)

        }

    }


}
