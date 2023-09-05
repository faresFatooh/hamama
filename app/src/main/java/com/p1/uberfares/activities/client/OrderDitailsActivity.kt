package com.p1.uberfares.activities.client

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.p1.uberfares.R
import com.p1.uberfares.adapter.AdapterCartH
import com.p1.uberfares.modelos.CardModel

class OrderDitailsActivity : AppCompatActivity() {
    private lateinit var courseRV: RecyclerView
    private var dataModalArrayList: ArrayList<CardModel>? = null
    private var dataRVAdapter: AdapterCartH? = null
    private var db: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_ditails)
        courseRV = findViewById(R.id.rc_order)
        var idd = intent.extras!!.getString("ran").toString()
        //Toast.makeText(this@OrderDitailsActivity,idd,Toast.LENGTH_SHORT).show()
        db = FirebaseDatabase.getInstance().reference
        dataModalArrayList = ArrayList()
        courseRV.setHasFixedSize(true)
        courseRV.setLayoutManager(LinearLayoutManager(this))
        dataRVAdapter = AdapterCartH(dataModalArrayList!!, this)
        courseRV.setAdapter(dataRVAdapter)
        loadrecyclerViewData(idd)
    }

    private fun loadrecyclerViewData(idd: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("UsersOrderHistory")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(idd).child("Order")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModalArrayList!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelCourses1 = dataSnapshot1.getValue(CardModel::class.java)

                    dataModalArrayList!!.add(modelCourses1!!)
                    dataRVAdapter = AdapterCartH(
                        dataModalArrayList!!, this@OrderDitailsActivity
                    )
                    courseRV!!.adapter = dataRVAdapter
                    dataRVAdapter!!.notifyDataSetChanged()


                }
                dataRVAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}