package com.p1.uberfares.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p1.uberfares.R
import com.p1.uberfares.activities.TripLogActivity
import com.p1.uberfares.modelos.HistoryBooking

class HistoryAdapter(
    mContext: Context,
    mHistory: List<HistoryBooking>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder?>() {
    private val mContext: Context
    private val mHistory: List<HistoryBooking>

    init {
        this.mHistory = mHistory
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false)
        return HistoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order: HistoryBooking = mHistory[position]
        //holder.in_progress.text = order!!.getStatus()
        holder.from.text = order.getOrigin()
        holder.to.text = order.getDestination()
        holder.our_rezference.text = order.getIdHistoryBooking()

        holder.itemView.setOnClickListener {
            var intent = Intent(mContext,TripLogActivity::class.java)
            intent.putExtra("calificationClient",order.getCalificationClient().toString())
            intent.putExtra("calificationDriver",order.getCalificationDriver().toString())
            intent.putExtra("destination",order.getDestination())
            intent.putExtra("destinationLat",order.getDestinationLat().toString())
            intent.putExtra("destinationLng",order.getDestinationLng().toString())
            intent.putExtra("km",order.getKm())
            intent.putExtra("origin",order.getOrigin())
            intent.putExtra("originLat",order.getOriginLat().toString())
            intent.putExtra("originLng",order.getOriginLng().toString())
            intent.putExtra("price",order.getPrice())
            intent.putExtra("status",order.getStatus())
            intent.putExtra("timestamp",order.getTimestamp())
            mContext.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return mHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //var in_progress: TextView
        var to: TextView
        var from: TextView
        //var btn_View_Details: Button
        var our_rezference: TextView

        init {
            //in_progress = itemView.findViewById(R.id.in_progress)
            to = itemView.findViewById(R.id.to)
            from = itemView.findViewById(R.id.from)
            //btn_View_Details = itemView.findViewById(R.id.btn_View_Details)
            our_rezference = itemView.findViewById(R.id.our_rezference)
        }

    }


}