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
import com.p1.uberfares.adapter.UsersOrderHistoryAdapter
import com.p1.uberfares.modelos.UsersOrderHistory
import java.util.*


class Fragment4 : Fragment() {
    private var OrderAdapter: UsersOrderHistoryAdapter? = null
    private var mOrder: List<UsersOrderHistory>? = null
    private lateinit var recyclerview_order_pending: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_4, container, false)

        recyclerview_order_pending = view.findViewById(R.id.recyclerview_order_HisN)
        recyclerview_order_pending.setHasFixedSize(true)
        recyclerview_order_pending.layoutManager = LinearLayoutManager(requireContext())

        mOrder = ArrayList()

        OrderAdapter = UsersOrderHistoryAdapter(requireContext(), mOrder as ArrayList<UsersOrderHistory>)

        recyclerview_order_pending.adapter = OrderAdapter
        readNotifications()
        return view
    }

    private fun readNotifications() {
        val notificationRef =
            FirebaseDatabase.getInstance().reference.child("UsersOrderHistory").child(FirebaseAuth.getInstance().currentUser!!.uid)
        notificationRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {
                    (mOrder as ArrayList<UsersOrderHistory>).clear()
                    for (snapshot in datasnapshot.children) {
                        val notification = snapshot.getValue(UsersOrderHistory::class.java)
                            (mOrder as ArrayList<UsersOrderHistory>).add(notification!!)

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