package com.clubz.ui.club.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.ClubMember
import com.clubz.data.model.Clubs
import com.clubz.data.remote.WebService
import com.clubz.ui.club.ClubDetailIntent
import com.clubz.ui.dialogs.LeaveClubDialog
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by Dharmraj Acharya on 25/05/18.
 */
class MyClubListAdapter(internal var list: ArrayList<Clubs>, internal var context: Context, var listner: MyClub)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val holder = MyClubHolder(LayoutInflater.from(context).inflate(R.layout.adapter_my_clubs, null))
            setUpClick(holder)
            holder
        } else {
            val holder = Holder(LayoutInflater.from(context).inflate(R.layout.baseclub_list, null))
            setUpClick(holder)
            holder
        }
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
    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        val obj = list[position]

        if (h is MyClubHolder) {
            val holder: MyClubHolder = h
            holder.tvName.text = obj.club_name
            holder.leadby.text = if (obj.full_name.isBlank()) ClubZ.currentUser?.full_name else obj.full_name
            holder.status.setText(if (obj.club_type == "2") R.string.Private else R.string.Public)
            holder.bodyDes.text = obj.club_description
            holder.members.text = String.format(context.getString(R.string.members_count), obj.members + 1)
            holder.llBodyContent.visibility = if (obj.isVisiableBody) View.VISIBLE else View.GONE
            holder.imgStatus.setImageResource(if (obj.club_type == "2") R.drawable.ic_lock_outline else R.drawable.ic_public)
            Glide.with(holder.imageClub.context)
                    .load(obj.club_icon)
                    /*.fitCenter()*/
                    .into(holder.imageClub)

            if(!obj.user_image.endsWith("defaultUser.png")) {
                Glide.with(holder.ivClubManager.context)
                        .load(obj.profile_image)
                        .into(holder.ivClubManager)

            }
            /*Picasso.with(holder.imageClub.context)
                    .load(obj.club_icon)
                    .fit()
                    .placeholder(R.drawable.img_gallery).into(holder.imageClub)*/

        } else if (h is Holder) {

            val holder: Holder = h
            holder.tvName.text = obj.club_name
            holder.leadBy.text = if (obj.full_name.isBlank()) ClubZ.currentUser?.full_name else obj.full_name
            holder.bodyDes.text = obj.club_description
            holder.members.text = String.format(context.getString(R.string.members_count), obj.members + 1)
            holder.llBodyContent.visibility = if (obj.isVisiableBody) View.VISIBLE else View.GONE
            if (ClubZ.latitude == 0.0 && ClubZ.longitude == 0.0) {
                holder.distance.text = "-- Km"
            } else {
                try {
                    holder.distance.text = " ${(Util.getDistanceMile(arrayOf(ClubZ.latitude,
                            ClubZ.longitude, obj.club_latitude.toDouble(),
                            obj.club_longitude.toDouble())) * 1.60934).toInt()} Km"
                } catch (ex: Exception) {
                }
            }
            Glide.with(holder.imageClub.context)
                    .load(obj.club_icon)
                    /*.fitCenter()*/
                    .into(holder.imageClub)
if(!obj.user_image.endsWith("defaultUser.png")) {
    Glide.with(holder.ivClubManager.context)
            .load(obj.profile_image)
            .into(holder.ivClubManager)

}
            if (obj.user_id == ClubZ.currentUser?.id) {

                holder.btnJoinClub.visibility = View.GONE

            } else {
                holder.btnJoinClub.visibility = View.VISIBLE

                if (obj.club_type == "1" && obj.club_user_status.isBlank()) {
                    // holder.btnJoinClub.setText(if(obj.club_type.equals("1"))R.string.join else R.string.req_join)
                    holder.btnJoinClub.text = context.getString(R.string.join)

                } else if (obj.club_type == "1" && obj.club_user_status == "1") {
                    holder.btnJoinClub.text = context.getString(R.string.leave)

                } else if (obj.club_type == "2" && obj.club_user_status.isBlank()) {
                    holder.btnJoinClub.text = context.getString(R.string.req_join)

                } else if (obj.club_type == "2" && obj.club_user_status == "0") {
                    holder.btnJoinClub.text = context.getString(R.string.dismiss)

                } else if (obj.club_type == "2" && obj.club_user_status == "1") {
                    holder.btnJoinClub.text = context.getString(R.string.leave)

                } else if (obj.club_type == "3") {
                    holder.btnJoinClub.visibility = View.GONE
                }
            }

            holder.imgStatus.setImageResource(if (obj.club_type == "1") R.drawable.ic_public else R.drawable.ic_lock_outline)
            holder.status.setText(if (obj.club_type == "2") R.string.Private else R.string.Public)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyClubHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.findViewById<TextView>(R.id.tvname)!!
        var leadby = itemView.findViewById<TextView>(R.id.leadby)!!
        var status = itemView.findViewById<TextView>(R.id.status)!!
        var members = itemView.findViewById<TextView>(R.id.members)!!
        var imageClub = itemView.findViewById<ImageView>(R.id.image_club)!!
        var llBodyContent = itemView.findViewById<LinearLayout>(R.id.ll_bodyContent)!!
        var bodyDes = itemView.findViewById<TextView>(R.id.body_des)!!
        var ivExpandBtn = itemView.findViewById<ImageView>(R.id.ivExpandBtn)!!
        var ivClubManager = itemView.findViewById<ImageView>(R.id.ivClubManager)!!
        var imgStatus = itemView.findViewById<ImageView>(R.id.img_status)!!

    }

    inner class Holder(itemView: View) : ClubMemberHolder(itemView, context) {

        override fun getProfile(): ClubMember {
            val club = list[adapterPosition]
            val member = ClubMember()
            member.full_name = club.full_name
            member.userId = club.user_id
            member.profile_image = club.profile_image
            member.contact_no = club.club_contact_no
            member.contact_no_visibility = club.contact_no_visibility
            return member
        }

        override fun showProfileDetail(member: ClubMember?) {

        }

        override fun notyfyData(pos: Int) {
        }

        override fun canEditNickName(): Boolean {
            return false
        }

        var tvName = itemView.findViewById<TextView>(R.id.tvname)!!
        var leadBy = itemView.findViewById<TextView>(R.id.leadby)!!
        var status = itemView.findViewById<TextView>(R.id.status)!!
        var members = itemView.findViewById<TextView>(R.id.members)!!
        var bodyDes = itemView.findViewById<TextView>(R.id.body_des)!!
        var distance = itemView.findViewById<TextView>(R.id.distance)!!
        var imgStatus = itemView.findViewById<ImageView>(R.id.img_status)!!
        var imageClub = itemView.findViewById<ImageView>(R.id.image_club)!!
        var ivClubManager = itemView.findViewById<ImageView>(R.id.ivClubManager)!!
        var btnJoinClub = itemView.findViewById<Button>(R.id.btn_join)!!
        // var switch1     = itemView.findViewById<Switch>(R.id.switch1)!!
        var llProfile = itemView.findViewById<LinearLayout>(R.id.ll_profile)!!
        var llBodyContent = itemView.findViewById<LinearLayout>(R.id.ll_bodyContent)!!
        var ivExpandBtn = itemView.findViewById<ImageView>(R.id.ivExpandBtn)!!
    }


    private fun setUpClick(holder: Holder) {
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]
            context.startActivity(context.ClubDetailIntent(club))
        }

        holder.btnJoinClub.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]

            if (club.club_type == "1" && club.club_user_status == "1") {
                showLeaveConfirationDialog(club, pos)
            } else if (club.club_type == "2" && club.club_user_status == "1") {
                showLeaveConfirationDialog(club, pos)
            } else {
                joinClub(club, pos)
            }
        }

        /* holder.switch1.setOnClickListener {
             val pos = holder.adapterPosition
             listner.onSilenceClub(list[pos], pos)
         }*/

        holder.llProfile.setOnClickListener {
            holder.showProfile()
        }

        holder.ivExpandBtn.setOnClickListener {
            val pos = holder.adapterPosition
            holder.llBodyContent.visibility = if (list[pos].isVisiableBody) View.GONE else View.VISIBLE
            Util.setRotation(holder.ivExpandBtn, list[pos].isVisiableBody)
            holder.ivExpandBtn.setImageResource(if (list[pos].isVisiableBody) R.drawable.ic_keyboard_arrow_down else R.drawable.ic_keyboard_arrow_up)
            list[pos].isVisiableBody = !list[pos].isVisiableBody
        }

    }

    private fun setUpClick(holder: MyClubHolder) {
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]
            context.startActivity(context.ClubDetailIntent(club))
        }
        holder.ivExpandBtn.setOnClickListener {
            val pos = holder.adapterPosition
            holder.llBodyContent.visibility = if (list[pos].isVisiableBody) View.GONE else View.VISIBLE
            Util.setRotation(holder.ivExpandBtn, list[pos].isVisiableBody)
            holder.ivExpandBtn.setImageResource(if (list[pos].isVisiableBody) R.drawable.ic_keyboard_arrow_down else R.drawable.ic_keyboard_arrow_up)
            list[pos].isVisiableBody = !list[pos].isVisiableBody
        }
    }

    private fun showLeaveConfirationDialog(club: Clubs, pos: Int) {
        object : LeaveClubDialog(context) {
            override fun onLeaveClubClicked() {
                this.dismiss()
                joinClub(club, pos)
            }

            override fun onCloseClicked() {
                this.dismiss()
            }
        }.show()
    }

    private fun joinClub(club: Clubs, pos: Int) {
        ClubZ.isNeedToUpdateNewsFeed = true
        val dialog = CusDialogProg(context)
        dialog.show()
        var clubUserStatus = ""

        if (club.club_type == "1" && club.club_user_status.isBlank()) {
            clubUserStatus = "1"

        } else if (club.club_type == "1" && club.club_user_status == "1") {
            clubUserStatus = ""

        } else if (club.club_type == "2" && club.club_user_status.isBlank()) {
            clubUserStatus = "0"

        } else {
            if (club.club_type == "2" && club.club_user_status == "0") {
                clubUserStatus = ""

            } else if (club.club_type == "2" && club.club_user_status == "1") {
                clubUserStatus = ""
            }
        }

        val api = if (clubUserStatus.isEmpty()) WebService.club_leave else WebService.club_join
        object : VolleyGetPost(context as Activity, context, api, false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {

                        if (club.club_type == "1" && clubUserStatus == "1") {
                            club.is_allow_feeds = ""

                        } else if ((club.club_type == "1" && clubUserStatus.isBlank()) ||
                                (club.club_type == "2" && clubUserStatus.isBlank())) {
                            club.is_allow_feeds = "1"

                        } else if (club.club_type == "2" && clubUserStatus == "0") {
                            club.is_allow_feeds = ""

                        } else if (club.club_type == "2" && clubUserStatus == "0") {
                            club.is_allow_feeds = "0"

                        }/*else if(club.club_type.equals("2") && clubUserStatus.isBlank()){
                            club.is_allow_feeds = "1"
                        }*/


                        club.club_user_status = clubUserStatus
                        //list.get(pos).club_user_status = clubUserStatus

                        if (clubUserStatus == "1") {
                            listner.onJoinedClub(club)
                            list.removeAt(pos)
                            notifyItemRemoved(pos)
                        } else if (club.club_type == "2" && clubUserStatus == "") {
                            notifyItemChanged(pos)
                        } else if (clubUserStatus == "") {
                            listner.onLeavedClub(club)
                            list.removeAt(pos)
                            notifyItemRemoved(pos)
                        } else notifyItemChanged(pos)
                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("Error", error.toString())
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubId"] = club.clubId
                params["clubUserStatus"] = clubUserStatus
                params["isAllowFeeds"] = if (club.is_allow_feeds.isBlank()) "1" else club.is_allow_feeds
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["language"] = SessionManager.getObj().language
                params["authToken"] = ClubZ.currentUser?.auth_token!!
                return params
            }
        }.execute()
    }


}

interface MyClub {
    fun onJoinedClub(club: Clubs)
    fun onLeavedClub(club: Clubs)
    fun onSilenceClub(club: Clubs, position: Int)
}