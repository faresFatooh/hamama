package com.p1.uberfares.activities.client

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.p1.uberfares.R
import com.p1.uberfares.adapter.AdapterCardItem
import com.p1.uberfares.modelos.restaurant
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


var resturantItem = "ايطاليانو"
private lateinit var courseRV2: RecyclerView
private var dataModalArrayList2: ArrayList<CardModelItem>? = null
private var dataRVAdapter2: AdapterCardItem? = null

class RestaurantClientActivity : AppCompatActivity() {
    private lateinit var courseRV: RecyclerView
    private lateinit var count: TextView
    private lateinit var imageButtonCart: ImageButton
    private var dataModalArrayList: ArrayList<restaurant>? = null
    private var dataRVAdapter: AdapterCard? = null
    private var db: DatabaseReference? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_client)

        courseRV = findViewById(R.id.idRVItems)
        courseRV2 = findViewById(R.id.idRVItems2)
        count = findViewById(R.id.countCart)
        imageButtonCart = findViewById(R.id.imageButtonCart)
        imageButtonCart.setOnClickListener {

            startActivity(Intent(this@RestaurantClientActivity, CartActivity::class.java))
        }

        // initializing our variable for firebase
        // firestore and getting its instance.

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseDatabase.getInstance().reference

        // creating our new array list

        // creating our new array list
        dataModalArrayList = ArrayList()
        dataModalArrayList2 = ArrayList()
        courseRV.setHasFixedSize(true)
        courseRV2.setHasFixedSize(true)

        // adding horizontal layout manager for our recycler view.

        // adding horizontal layout manager for our recycler view.
        courseRV.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        courseRV2.setLayoutManager(LinearLayoutManager(this))

        val mDatabaseRef = FirebaseDatabase.getInstance().reference
        mDatabaseRef.child("UserCart").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val countItem = dataSnapshot.childrenCount

                    if (countItem.toString() == "0") {
                        count.visibility = View.GONE


                    } else {
                        count.setText(countItem.toString())
                        count.visibility = View.VISIBLE

                    }

                }

                override fun onCancelled(error: DatabaseError) {


                }
            })

        // adding our array list to our recycler view adapter class.

        // adding our array list to our recycler view adapter class.
        dataRVAdapter = AdapterCard(dataModalArrayList!!, this)
        dataRVAdapter2 = AdapterCardItem(dataModalArrayList2!!, this)

        // setting adapter to our recycler view.

        // setting adapter to our recycler view.
        courseRV.setAdapter(dataRVAdapter)
        courseRV2.setAdapter(dataRVAdapter2)

        loadrecyclerViewData()
//        loadrecyclerViewData2()
    }

    private fun loadrecyclerViewData() {
        val reference =
            FirebaseDatabase.getInstance().reference.child("Restaurants").child("topics")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataModalArrayList!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelCourses1 = dataSnapshot1.getValue(restaurant::class.java)
                    dataModalArrayList!!.add(modelCourses1!!)
                    dataRVAdapter = AdapterCard(dataModalArrayList!!, this@RestaurantClientActivity)
                    courseRV!!.adapter = dataRVAdapter
                    dataRVAdapter2!!.notifyDataSetChanged()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    class AdapterCard(private val dataModalArrayList: ArrayList<restaurant>, context: Context) :
        RecyclerView.Adapter<AdapterCard.ViewHolder>() {
        private val context: Context
        var defaultColor: Int = Color.BLACK
        var defaultColor2: Int = Color.RED
        var selectedPosition = -1

        // constructor class for our Adapter
        init {
            this.context = context
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // passing our layout file for displaying our card item
            return ViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.re_item, parent, false)
            )


        }


        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // setting data to our views in Recycler view items.
            val modal = dataModalArrayList[position]
            if (position == selectedPosition) {
                // Set the background color of the root view to the selected color
                holder.courseIV.borderColor = defaultColor!!
            } else {
                // Set the background color of the root view to the default color
                holder.courseIV.borderColor = defaultColor2!!
            }
            holder.courseNameTV.text = modal.name

            // we are using Picasso to load images
            // from URL inside our image view.
            Picasso.get().load(modal.imgUrl).into(holder.courseIV)
            val reference =
                FirebaseDatabase.getInstance().reference.child("RestaurantsMenu")
                    .child(resturantItem)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataModalArrayList2!!.clear()
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val modelCourses1 = dataSnapshot1.getValue(CardModelItem::class.java)
                        if (modelCourses1?.res.equals(resturantItem)) {
                            dataModalArrayList2!!.add(modelCourses1!!)
                        }
                        dataRVAdapter2 =
                            AdapterCardItem(
                                dataModalArrayList2!!,
                                context
                            )
                        courseRV2!!.adapter = dataRVAdapter2
                        dataRVAdapter2!!.notifyDataSetChanged()
                    }
                }


                override fun onCancelled(databaseError: DatabaseError) {}
            })
            holder.itemView.setOnTouchListener(OnTouchListener { v, event ->

                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.parseColor("#f0f0f0"))
                }
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(Color.TRANSPARENT)
                }
                false
            })

            holder.itemView.setOnClickListener {


                resturantItem = modal.name.toString()


                // Toast.makeText(context, resturantItem, Toast.LENGTH_SHORT).show()
                val reference =
                    FirebaseDatabase.getInstance().reference.child("RestaurantsMenu")
                        .child(resturantItem)
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataModalArrayList2!!.clear()
                        for (dataSnapshot1 in dataSnapshot.children) {
                            val modelCourses1 = dataSnapshot1.getValue(CardModelItem::class.java)
                            if (modelCourses1?.res.equals(resturantItem)) {
                                dataModalArrayList2!!.add(modelCourses1!!)
                            }
                            dataRVAdapter2 =
                                AdapterCardItem(
                                    dataModalArrayList2!!,
                                    context
                                )
                            courseRV2!!.adapter = dataRVAdapter2
                            dataRVAdapter2!!.notifyDataSetChanged()
                        }
                    }


                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

        }

        override fun getItemCount(): Int {
            // returning the size of array list.
            return dataModalArrayList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // creating variables for our
            // views of recycler items.
            val courseNameTV: TextView
            val courseIV: CircleImageView

            init {
                // initializing the views of recycler views.
                courseNameTV = itemView.findViewById(R.id.idTVtext)
                courseIV = itemView.findViewById(R.id.idIVimage)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@RestaurantClientActivity, MapClientActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}