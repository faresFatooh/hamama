package com.p1.uberfares.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p1.uberfares.R
import com.p1.uberfares.modelos.CardModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterCartH(private val dataModalArrayList: ArrayList<CardModel>, context: Context) :
    RecyclerView.Adapter<AdapterCartH.ViewHolder>() {
    private val context: Context

    // constructor class for our Adapter
    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // passing our layout file for displaying our card item
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to our views in Recycler view items.
        val modal = dataModalArrayList[position]
        holder.itemNameTV.text = modal.name
        Picasso.get().load(modal.imgUrl).into(holder.imageView4)
        holder.price.setText("Quantity : ${modal.count}")
        holder.des_item.setText("Price : ${modal.price}")
        holder.btnPlus.visibility = View.INVISIBLE
        holder.btnMinus.visibility = View.INVISIBLE


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


}