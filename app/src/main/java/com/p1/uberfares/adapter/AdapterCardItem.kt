package com.p1.uberfares.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.p1.uberfares.R
import com.p1.uberfares.activities.client.CardModelItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class AdapterCardItem(private val dataModalArrayList: ArrayList<CardModelItem>, context: Context) :
    RecyclerView.Adapter<AdapterCardItem.ViewHolder>() {
    private val context: Context

    // constructor class for our Adapter
    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // passing our layout file for displaying our card item
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.resturant_item, parent, false)
        )
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to our views in Recycler view items.
        val modal = dataModalArrayList[position]
        holder.itemNameTV.text = modal.name
        holder.des_item.text = modal.dis
        holder.price.text = modal.price + " ₪ "


        val mDatabase = FirebaseDatabase.getInstance().getReference("UserCart")

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rface = dataSnapshot.child(FirebaseAuth.getInstance().currentUser!!.uid)

                    .child(modal.item_uid.toString()).child("item_uid")
                    .getValue(String::class.java)
                if (rface != modal.item_uid) {
                    holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    holder.btnAdd.setText("Add")
                } else {
                    holder.btnAdd.setText("Remove")
                    holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        cart(context)
        holder.btnAdd.setOnClickListener {
            cart(context)


            if (holder.btnAdd.text == "Add") {


                val sharedPreference =
                    context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

                if (sharedPreference.getString("res", null) == null) {
                    var editor = sharedPreference.edit()
                    editor.putString("res", modal.res)
                    editor.apply()
                }
//                Toast.makeText(context,sharedPreference.getString("res", null).toString(),Toast.LENGTH_SHORT).show()

                if (modal.res == sharedPreference.getString("res", null).toString()) {
                    holder.btnAdd.setText("Remove")
                    holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(modal.item_uid.toString()).child("name")
                        .setValue(modal.name)

                    FirebaseDatabase.getInstance().reference.child("res")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("name")
                        .setValue(modal.res)

                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(modal.item_uid.toString()).child("count")
                        .setValue("1")

                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child(modal.item_uid.toString()).child("res")
                        .setValue(modal.res)
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child(modal.item_uid.toString()).child("price")
                        .setValue(modal.price)
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child(modal.item_uid.toString()).child("imgUrl")
                        .setValue(modal.imgUrl)
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child(modal.item_uid.toString()).child("uid")
                        .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child(modal.item_uid.toString()).child("item_uid")
                        .setValue(modal.item_uid.toString())
                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("عذراً")
                    builder.setMessage("الطلبات متاحة من مطعم واحد")

                    builder.setPositiveButton("اكمال الطلب") { dialog, which ->

                    }

                    builder.setNegativeButton("حذف السلة") { dialog, which ->
                        FirebaseDatabase.getInstance().reference.child("UserCart")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .removeValue()
                        (context as Activity).recreate()


//                        val sharedPreference =
//                            context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
//                        sharedPreference.edit().putString("res", null).apply()
//                        sharedPreference.edit().clear().apply()

                    }
                    builder.show()

                }


            } else if (holder.btnAdd.text == "Remove") {
                holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.RED)
                holder.btnAdd.setText("Add")
                FirebaseDatabase.getInstance().reference.child("UserCart")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(modal.item_uid!!).removeValue()


            }

            cart(context)
        }

        // we are using Picasso to load images
        // from URL inside our image view.
        Picasso.get().load(modal.imgUrl).into(holder.imageView4)
        holder.itemView.setOnClickListener {

            // setting on click listener
            // for our items of recycler items.
//            Toast.makeText(context, "Clicked item is " + modal.name, Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        // returning the size of array list.
        return dataModalArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our
        // views of recycler items.
        val itemNameTV: TextView
        val des_item: TextView
        val price: TextView
        val btnAdd: Button
        val imageView4: CircleImageView

        init {
            // initializing the views of recycler views.
            itemNameTV = itemView.findViewById(R.id.itemNameTV)
            imageView4 = itemView.findViewById(R.id.image_item)
            des_item = itemView.findViewById(R.id.des_item)
            price = itemView.findViewById(R.id.price)
            btnAdd = itemView.findViewById(R.id.btnAdd)
        }
    }


}

fun cart(context: Context) {
    val mDatabaseRef = FirebaseDatabase.getInstance().reference
    mDatabaseRef.child("UserCart").child(FirebaseAuth.getInstance().currentUser!!.uid)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val countItem = dataSnapshot.childrenCount
                val rootView =
                    (context as Activity).window.decorView.findViewById<TextView>(R.id.countCart)



                if (countItem.toString() == "0") {
                    rootView.visibility = View.GONE
                    val sharedPreference =
                        context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                    sharedPreference.edit().putString("res", null).apply()
                    sharedPreference.edit().clear().apply()
                } else {
                    rootView.setText(countItem.toString())
                    rootView.visibility = View.VISIBLE


                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })


}


