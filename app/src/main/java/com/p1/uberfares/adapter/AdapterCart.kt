package com.p1.uberfares.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.p1.uberfares.R
import com.p1.uberfares.modelos.CardModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class AdapterCart(private val dataModalArrayList: ArrayList<CardModel>, context: Context) :
    RecyclerView.Adapter<AdapterCart.ViewHolder>() {
    private val context: Context

    // constructor class for our Adapter
    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // passing our layout file for displaying our card item
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(com.p1.uberfares.R.layout.cart_item, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to our views in Recycler view items.
        val modal = dataModalArrayList[position]
            holder.itemNameTV.text = modal.name
            if (modal.count == "0") {
                FirebaseDatabase.getInstance().reference.child("UserCart")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(modal.item_uid!!).removeValue()

            }
            val rootView =
                (context as Activity).window.decorView.findViewById<TextView>(R.id.textView3)
            var total = 0;
            for (i in 0 until dataModalArrayList.size) {
                total += (dataModalArrayList[i].price!!.toInt() * dataModalArrayList[i].count!!.toInt())
            }
            rootView.text = ("Total Price : " + total.toString() + " ₪ ")
            //rootView.text = (total.toString())


//        holder.des_item.text = modal.dis
            holder.price.text = modal.price + " ₪ "
            holder.des_item.setText(modal.count)
            if (modal.count!!.toInt() >= 1) {
                holder.price.setText(modal.price.toString())
            }
            // we are using Picasso to load images
            // from URL inside our image view.
            Picasso.get().load(modal.imgUrl).into(holder.imageView4)
            holder.btnPlus.setOnClickListener {
                rootView.text = ("Total Price : " + total.toString() + " ₪ ")
                FirebaseDatabase.getInstance().reference.child("UserCart")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(modal.item_uid.toString()).child("count")
                    .setValue((modal.count!!.toInt() + 1).toString())
                holder.des_item.setText(modal.count)
                if (modal.count!!.toInt() >= 1) {
                    holder.price.setText(modal.price)
                }

            }
            holder.btnMinus.setOnClickListener {

                if (modal.count == "0") {
                    clear()
                    FirebaseDatabase.getInstance().reference.child("UserCart")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(modal.item_uid!!).removeValue()
                }

                rootView.text = ("Total Price : " + total.toString() + " ₪ ")
                FirebaseDatabase.getInstance().reference.child("UserCart")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(modal.item_uid.toString()).child("count")
                    .setValue((modal.count!!.toInt() - 1).toString())


                if (modal.count!!.toInt() >= 1) {
                    holder.price.setText(modal.price)
                }

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
        val btnMinus: Button
        val btnPlus: Button
        val imageView4: CircleImageView

        init {
            // initializing the views of recycler views.
            itemNameTV = itemView.findViewById(R.id.cart_itemNameTV)
            imageView4 = itemView.findViewById(R.id.cart_image_item)
            des_item = itemView.findViewById(R.id.cart_textView3)
            price = itemView.findViewById(R.id.cart_price)
            btnPlus = itemView.findViewById(R.id.cart_btnPlus)
            btnMinus = itemView.findViewById(R.id.cart_btnMinus)
        }
    }

    fun clear() {
        val size: Int = dataModalArrayList.size
        dataModalArrayList.clear()
        notifyItemRangeRemoved(0, size)
    }
}
