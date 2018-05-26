package com.clubz.ui.newsfeed.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R
import com.clubz.data.model.Feed
import com.squareup.picasso.Picasso
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
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val feed : Feed = items.get(position)
        holder!!.tvDescription?.text = feed.news_feed_description
        holder.tvTitle.text = feed.news_feed_title
        holder.tvCreateTime.text = "# "+feed.getDate()
        holder.tvClubname.text =feed.club_name
        holder.tvCreaterName.text = feed.club_name
        holder.tvChatCount.text = "${feed.comments +" "}${context.getString(R.string.comments)}"

        if(!feed.news_feed_attachment.isEmpty())
            Picasso.with(holder.ivBanner.context).load(feed.news_feed_attachment).fit().into(holder.ivBanner)

       /* if(!feed.club_icon.isEmpty())
            Picasso.with(holder.ivUserProfile.context).load(feed.club_icon).fit().into(holder.ivUserProfile)*/
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivBanner = view.ivBanner
    val tvTitle = view.tvTitle
    val tvCreateTime = view.tvCreateTime
    val tvClubname = view.tvClubname
    val tvDescription = view.tvDescription
    val tvCreaterName = view.tvCreaterName
    val tvChatCount = view.tvChatCount
   // val ivUserProfile = view.ivUserProfile
}