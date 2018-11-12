package com.clubz.ui.receipt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.clubz.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_receipt_member.view.*

class ReceiptMemberAdapter(val context: Context, val listner: Listner) :
        RecyclerView.Adapter<ReceiptMemberAdapter.ViewHolder>() {


    // OR using options to customize


    interface Listner {
        fun onMemberItemClick( pos: Int)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return 10
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_receipt_member, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      //  val feed: Feed = items.get(position)
        holder.itemUserName.text = "USER NAME"

        /* if(!feed.club_icon.isEmpty())
             Picasso.with(holder.ivUserProfile.context).load(feed.club_icon).fit().into(holder.ivUserProfile)*/
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val itemImage = view.item_image!!
        val itemUserName = view.itemUserName!!

        init {
            view.setOnClickListener({ v: View? ->
                //val feed = items.get(adapterPosition)
                listner.onMemberItemClick( adapterPosition)
            })
        }
    }

}