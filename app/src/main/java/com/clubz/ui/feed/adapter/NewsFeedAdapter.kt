package com.clubz.ui.feed.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R
import com.clubz.data.model.Feed
import kotlinx.android.synthetic.main.adapter_news_feed.view.*

class NewsFeedAdapter(val items : ArrayList<Feed>, val context: Context) : RecyclerView.Adapter<ViewHolder>(){


    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_news_feed, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvDescription?.text = items.get(position).description
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivBanner = view.ivBanner
    val tvDescription = view.tvDescription
}