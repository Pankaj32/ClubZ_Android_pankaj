package com.clubz.ui.profile.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.clubz.R
import com.clubz.data.model.ClubMember

class AdapterContactList(internal var list : ArrayList<ClubMember> , internal var context :Context ) : RecyclerView.Adapter<AdapterContactList.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_contact_list, null))
        holder.setUpClick()
        return holder
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setUpClick() {

        }
    }
}