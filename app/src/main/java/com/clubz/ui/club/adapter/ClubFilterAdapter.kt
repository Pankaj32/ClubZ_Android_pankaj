package com.clubz.ui.club.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.model.ClubMember
import com.clubz.data.model.Clubs
import com.clubz.ui.club.ClubDetailIntent

import java.util.ArrayList

/**
 * Created by Dharmraj Acharya on 12/07/18.
 */
class ClubFilterAdapter(internal var list: ArrayList<Clubs>, internal var context: Context, var listner: MyClub)
    : RecyclerView.Adapter<ClubFilterAdapter.Holder>() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubFilterAdapter.Holder {
        val holder = Holder(LayoutInflater.from(context).inflate(R.layout.adapter_club_filter, null))
        setUpClick(holder)
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        val obj = list[position]
        return when {
            obj.user_id == ClubZ.currentUser?.id -> 0
            obj.club_type == "3" -> 0
            else -> 1
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val obj = list[position]

        holder.tvName.text = obj.club_name
        holder.status.setText(if (obj.club_type == "2") R.string.Private else R.string.Public)
        holder.imgStatus.setImageResource(if (obj.club_type == "2") R.drawable.ic_lock_outline else R.drawable.ic_public)
        holder.leadby.text = if (obj.full_name.isBlank()) ClubZ.currentUser?.full_name else obj.full_name

        if (obj.user_id == ClubZ.currentUser?.id) {

        } else {
            holder.switch1.isChecked = obj.is_allow_feeds == "1"
            holder.switch1.visibility = if (obj.club_user_status == "1") View.VISIBLE else View.GONE
        }

        try {
            /*Glide.with(holder.imageClub.context)
                    .load(obj.club_icon)

                    .into(holder.imageClub)*/

            Glide.with(holder.imageClub.context)
                    .load(obj.club_icon)
                    /*.fitCenter()
                    .placeholder(R.drawable.img_gallery)*/.into(holder.imageClub)

        } catch (ex: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(itemView: View) : ClubMemberHolder(itemView, context) {

        override fun getProfile(): ClubMember {
            val club = list[adapterPosition]
            val member = ClubMember()
            member.full_name = club.full_name
            member.userId = club.user_id
            member.profile_image = club.profile_image
            return member
        }

        override fun showProfileDetail(member: ClubMember?) {}
        override fun notyfyData(pos: Int) {}
        override fun canEditNickName(): Boolean {
            return false
        }

        var tvName = itemView.findViewById<TextView>(R.id.tvname)!!
        var leadby = itemView.findViewById<TextView>(R.id.leadby)!!
        var status = itemView.findViewById<TextView>(R.id.status)!!
        var imageClub = itemView.findViewById<ImageView>(R.id.image_club)!!
        var switch1 = itemView.findViewById<Switch>(R.id.switch1)!!
        var imgStatus = itemView.findViewById<ImageView>(R.id.img_status)!!
    }


    private fun setUpClick(holder: Holder) {
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]
            context.startActivity(context.ClubDetailIntent(club))
        }

        holder.switch1.setOnClickListener {
            val pos = holder.adapterPosition
            listner.onSilenceClub(list[pos], pos)
        }
    }
}