package com.clubz.ui.newsfeed.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.utils.VolleyGetPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_news_feed.view.*
import org.json.JSONObject

class NewsFeedAdapter(val items : ArrayList<Feed>, val context: Context, val listner : Listner) :
        RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>(){

    interface Listner{
        fun onItemClick(feed: Feed, pos : Int)
        fun onFeedEditClick(view: View, feed: Feed, pos : Int)
        fun onChatClick(feed: Feed)
        fun onProfileClick(feed: Feed)
    }
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
        holder!!.tvDescription.text = feed.news_feed_description
        holder.tvTitle.text = feed.news_feed_title
        holder.tvCreateTime.text = feed.getTimeAgo(context)
        holder.tvClubname.text =feed.club_name
        holder.tvCreaterName.text = feed.user_name
        holder.bubbleMenu.visibility = if(feed.user_id == ClubZ.currentUser?.id) View.VISIBLE else View.GONE
        holder.likeIcon.isChecked = feed.isLiked==1
        holder.ivChat.setImageResource(if(feed.comments>0) R.drawable.chat_icon else R.drawable.ic_chat_outline)

        if(feed.news_feed_attachment.isEmpty()){
            holder.rlContent.visibility = View.GONE
            holder.llTxt.visibility = View.VISIBLE
            holder.tvDescTxt.text = feed.news_feed_description
        } else{
            holder.llTxt.visibility = View.GONE
            holder.ivBanner.visibility = View.VISIBLE
            holder.rlContent.visibility = View.VISIBLE
            Picasso.with(holder.ivBanner.context).load(feed.news_feed_attachment).fit().into(holder.ivBanner)
        }

       /* if(!feed.club_icon.isEmpty())
            Picasso.with(holder.ivUserProfile.context).load(feed.club_icon).fit().into(holder.ivUserProfile)*/
    }

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val ivBanner = view.ivBanner!!
        val tvTitle = view.tvTitle!!
        val tvCreateTime = view.tvCreateTime!!
        val tvClubname = view.tvClubname!!
        val tvDescription = view.tvDescription!!
        val tvDescTxt = view.tvDescTxt!!
        val tvCreaterName = view.tvCreaterName!!
        //val tvChatCount = view.tvChatCount
        val llTxt = view.ll_txt!!
        val rlContent = view.rl_content!!
        val bubbleMenu = view.bubble_menu!!
        val likeIcon = view.likeIcon!!
        val ivChat = view.ivChat!!
        val ll1 = view.ll1!!
        // val ivUserProfile = view.ivUserProfile

        init {
            view.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                listner.onItemClick(feed, adapterPosition)
            })

            bubbleMenu.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                listner.onFeedEditClick(bubbleMenu, feed, adapterPosition)
            })

            likeIcon.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                val isCheck = likeIcon.isChecked
                feed.isLiked = if(isCheck) 1 else 0
                if (isCheck) feed.likes++ else feed.likes--
                notifyItemChanged(adapterPosition)
                likeNewsFeed(feed)
            })

            ivChat.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                listner.onChatClick(feed)
            })

            ll1.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                listner.onProfileClick(feed)
            })

        }
    }

    fun likeNewsFeed(feed: Feed){
        //getNewsFeedLsit
        // val dialog = CusDialogProg(context);
        // dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(context, WebService.feed_like, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    // dialog.dismiss();
                    Log.d("newsFeedsLike", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status")=="success") {

                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                //dialog.dismiss();
            }

            override fun onNetError() {
                // dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["newsFeedId"] = feed.newsFeedId.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().getLanguage()
                return params
            }
        }.execute()
    }
}