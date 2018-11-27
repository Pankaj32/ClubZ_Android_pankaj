package com.clubz.ui.profile.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.clubz.R
import com.clubz.data.model.AllFavContact
import com.clubz.ui.profile.ContactListActivity
import kotlinx.android.synthetic.main.adapter_contact_list.view.*

class AdapterContactList(var favContactList: ArrayList<AllFavContact>, val context: Context, val listner: Listner) :
        RecyclerView.Adapter<AdapterContactList.ViewHolder>(), Filterable {
var filterdFavContactList=favContactList
    internal var valueFilter: ValueFilter? = null

    // OR using options to customize


    interface Listner {
        fun onItemClick(contact: AllFavContact, pos: Int)

        fun onChatClick(contact: AllFavContact)
        fun onProfileClick(contact: AllFavContact)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return favContactList.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_contact_list, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact: AllFavContact = favContactList.get(position)
        // holder.ivProfileImage.text = feed.news_feed_description
        holder.tvNickname.text = contact.name
        holder.tvClubname.text = contact.club_name
        if (!contact.profile_image.isNullOrEmpty()) Glide.with(holder.ivProfileImage.context)
                .load(contact.profile_image)
                .into(holder.ivProfileImage)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        //  val ivBanner = view.ivBanner!!
        val ivProfileImage = view.ivProfileImage!!
        val tvNickname = view.tvNickname!!
        val tvClubname = view.tvClubname!!
        val chatImage = view.chatImage!!


        init {
            /*view.setOnClickListener({ v: View? ->
                val feed = items.get(adapterPosition)
                listner.onItemClick(feed, adapterPosition)
            })*/

            chatImage.setOnClickListener({ v: View? ->
                val contact = favContactList.get(adapterPosition)
                listner.onChatClick(contact)
            })
            ivProfileImage.setOnClickListener({ v: View? ->
                val contact = favContactList.get(adapterPosition)
                listner.onProfileClick(contact)
            })

        }
    }

    override fun getFilter(): Filter {
        if (valueFilter == null) {
            valueFilter = ValueFilter()
        }
        return valueFilter as ValueFilter
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()

            if (constraint != null && constraint.length > 0) {
                val filterList = java.util.ArrayList<AllFavContact>()
                for (fav in filterdFavContactList) {
                    if (fav.name!!.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filterList.add(fav)
                    }
                }
                results.count = filterList.size
                results.values = filterList
            } else {
                results.count = filterdFavContactList.size
                results.values = filterdFavContactList
            }
            return results

        }

        override fun publishResults(constraint: CharSequence,
                                    results: Filter.FilterResults) {
            favContactList = results.values as ArrayList<AllFavContact>
            notifyDataSetChanged()
        }

    }
}