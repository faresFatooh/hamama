package com.p1.uberfares.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.p1.uberfares.R
import com.p1.uberfares.adapter.HistoryAdapter
import com.p1.uberfares.modelos.HistoryBooking
import java.util.*


class Fragment3 : Fragment() {

    private var OrderAdapter: HistoryAdapter? = null
    private var mOrder: List<HistoryBooking>? = null
    private lateinit var recyclerview_order_pending: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_3, container, false)
        recyclerview_order_pending = view.findViewById(R.id.recyclerview_order_pendingC)
        recyclerview_order_pending.setHasFixedSize(true)
        recyclerview_order_pending.layoutManager = LinearLayoutManager(requireContext())

        mOrder = ArrayList()

        OrderAdapter = HistoryAdapter(requireContext(), mOrder as ArrayList<HistoryBooking>)

        recyclerview_order_pending.adapter = OrderAdapter
        readNotifications()
        return view
    }
    private fun readNotifications() {
        val notificationRef =
            FirebaseDatabase.getInstance().reference.child("HistoryBooking")
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {
                    (mOrder as ArrayList<HistoryBooking>).clear()
                    for (snapshot in datasnapshot.children) {
                        val notification = snapshot.getValue(HistoryBooking::class.java)
                        if (notification?.getIdClient().equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            (mOrder as ArrayList<HistoryBooking>).add(notification!!)
                        }
                    }
                    Collections.reverse(mOrder)
                    OrderAdapter!!.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}