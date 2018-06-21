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
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by Dharmraj Acharya on 25/05/18.
 */
class MyClubListAdapter(internal var list : ArrayList<Clubs>, internal var context :Context, var listner : MyClub)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 0){
            val holder = MyClubHolder(LayoutInflater.from(context).inflate(R.layout.adapter_my_clubs, null))
            setUpClick(holder)
            holder
        }else {
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

        /*return if(this.isMyClub){
            val obj = list.get(position)
            when {
                obj.user_id == ClubZ.currentUser?.id -> 0
                obj.club_type == "3" -> 0
                else -> 1
            }
        }else 1*/
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        val obj = list[position]

        if(h is MyClubHolder){
            val holder : MyClubHolder = h
            holder.tvName.text = obj.club_name
            holder.leadby.text = if(obj.full_name.isBlank()) ClubZ.currentUser?.full_name else obj.full_name
            holder.status.setText(if(obj.club_type=="2") R.string.Private else R.string.Public)
            Picasso.with(holder.imageClub.context)
                    .load(obj.club_icon)
                    .fit()
                    .placeholder(R.drawable.img_gallery).into(holder.imageClub)

        }else if(h is Holder){

            val holder : Holder = h
            holder.tvName.text = obj.club_name
            holder.leadBy.text = if(obj.full_name.isBlank()) ClubZ.currentUser?.full_name else obj.full_name
            holder.bodyDes.text = obj.club_description
            holder.members.text = String.format("%d ",obj.members+1, context.getString(R.string.members))

            if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0){
                holder.distance.text = "-- Km"
            }
            else {
                try {
                    holder.distance.text = " ${(Util.getDistanceMile(arrayOf(ClubZ.latitude,
                            ClubZ.longitude,obj.club_latitude.toDouble() ,
                            obj.club_longitude.toDouble()))*1.60934).toInt()} Km"
                }catch (ex :Exception){}
            }


            if(obj.user_id == ClubZ.currentUser?.id){
                holder.switch1.visibility = View.GONE
                holder.btn_join.visibility = View.GONE
                if(!ClubZ.currentUser!!.profile_image.isEmpty()){
                    Picasso.with(holder.ivClubManager.context)
                            .load(ClubZ.currentUser!!.profile_image)
                            .into(holder.ivClubManager)
                }else
                    Picasso.with(holder.ivClubManager.context)
                            .load(R.drawable.ic_user_white)
                            .into(holder.ivClubManager)
            } else{
                holder.btn_join.visibility = View.VISIBLE
                //holder.switch1.isActivated = obj.is_allow_feeds == "1"
                // holder.switch1.isEnabled = obj.club_user_status.equals("1")
                holder.switch1.isChecked = obj.is_allow_feeds == "1"
                holder.switch1.visibility = if(obj.club_user_status == "1") View.VISIBLE else View.GONE
                // emtpty means no action performed yet, 0 = pending for approvial by Club admin , 1 = Joined club
                // obj.club_type 1 = public club and 2 = private club
                if(obj.club_type == "1" && obj.club_user_status.isBlank()){
                    // holder.btn_join.setText(if(obj.club_type.equals("1"))R.string.join else R.string.req_join)
                    holder.btn_join.text = context.getString(R.string.join)

                }else if(obj.club_type == "1" && obj.club_user_status == "1"){
                    holder.btn_join.text = context.getString(R.string.leave)

                }else if(obj.club_type == "2" && obj.club_user_status.isBlank()){
                    holder.btn_join.text = context.getString(R.string.req_join)

                }else if(obj.club_type == "2" && obj.club_user_status == "0"){
                    holder.btn_join.text = context.getString(R.string.dismiss)

                }else if(obj.club_type == "2" && obj.club_user_status == "1"){
                    holder.btn_join.text = context.getString(R.string.leave)

                }else if(obj.club_type == "3"){
                    holder.btn_join.visibility = View.GONE
                }
            }

            holder.imgStatus.setImageResource(if(obj.club_type == "1") R.drawable.ic_unlocked_padlock_black else R.drawable.ic_locked_padlock_black)
            //holder.status.setText(if(obj.club_type.equals("1")) R.string.Public else R.string.Private)
            holder.status.setText(if(obj.club_type=="2") R.string.Private else R.string.Public)
            try {

                if(obj.profile_image.isEmpty()){
                    Picasso.with(holder.ivClubManager.context)
                            .load(R.drawable.ic_user_white)
                            .fit()
                            .into(holder.ivClubManager)
                }else
                    Picasso.with(holder.ivClubManager.context)
                            .load(obj.profile_image)
                            .fit()
                            .into(holder.ivClubManager)

                Picasso.with(holder.imageClub.context)
                        .load(obj.club_icon)
                        .fit()
                        .placeholder(R.drawable.img_gallery).into(holder.imageClub)

            }catch (ex :Exception){}
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class MyClubHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName      = itemView.findViewById<TextView>(R.id.tvname)!!
        var leadby      = itemView.findViewById<TextView>(R.id.leadby)!!
        var status      = itemView.findViewById<TextView>(R.id.status)!!
        var imageClub  = itemView.findViewById<ImageView>(R.id.image_club)!!
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

        override fun notyfyData(pos: Int) {
        }

        override fun canEditNickName(): Boolean {
            return false
        }

        var tvName      = itemView.findViewById<TextView>(R.id.tvname)!!
        var leadBy      = itemView.findViewById<TextView>(R.id.leadby)!!
        var status      = itemView.findViewById<TextView>(R.id.status)!!
        var members     = itemView.findViewById<TextView>(R.id.members)!!
        var bodyDes     = itemView.findViewById<TextView>(R.id.body_des)!!
        var distance    = itemView.findViewById<TextView>(R.id.distance)!!
        var imgStatus   = itemView.findViewById<ImageView>(R.id.img_status)!!
        var imageClub   = itemView.findViewById<ImageView>(R.id.image_club)!!
        var ivClubManager = itemView.findViewById<ImageView>(R.id.ivClubManager)!!
        var btn_join    = itemView.findViewById<Button>(R.id.btn_join)!!
        var switch1     = itemView.findViewById<Switch>(R.id.switch1)!!
        var llProfile   = itemView.findViewById<LinearLayout>(R.id.ll_profile)!!
    }


    private fun setUpClick(holder: Holder){
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]
            context.startActivity(context.ClubDetailIntent(club))
        }

        holder.btn_join.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]

            if(club.club_type == "1" && club.club_user_status == "1"){
                showLeaveConfirationDialog(club, pos)
            }else if(club.club_type == "2" && club.club_user_status == "1"){
                showLeaveConfirationDialog(club, pos)
            }else{
                joinClub(club, pos)
            }
        }

        holder.switch1.setOnClickListener({
            val pos = holder.adapterPosition
            listner.onSilenceClub(list[pos], pos)
        })

        holder.llProfile.setOnClickListener({
            holder.showProfile()
        })

    }

    private fun setUpClick(holder: MyClubHolder){
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list[pos]
            context.startActivity(context.ClubDetailIntent(club))
        }
    }

    private fun showLeaveConfirationDialog(club : Clubs, pos : Int){
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

    fun joinClub(club : Clubs, pos : Int){
        ClubZ.isNeedToUpdateNewsFeed = true
        val dialog = CusDialogProg(context )
        dialog.show()
        var clubUserStatus = ""

        if(club.club_type == "1" && club.club_user_status.isBlank()){
            clubUserStatus = "1"

        }else if(club.club_type == "1" && club.club_user_status == "1"){
            clubUserStatus = ""

        }else if(club.club_type == "2" && club.club_user_status.isBlank()){
            clubUserStatus = "0"

        }else {
            if(club.club_type == "2" && club.club_user_status == "0"){
                clubUserStatus = ""

            }else if(club.club_type == "2" && club.club_user_status == "1"){
                clubUserStatus = ""
            }
        }

        val api = if(clubUserStatus.isEmpty()) WebService.club_leave else WebService.club_join
        object  : VolleyGetPost(context as Activity, context ,api,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status") == "success"){

                        if(club.club_type=="1" && clubUserStatus=="1"){
                            club.is_allow_feeds = ""

                        }else if( (club.club_type=="1" && clubUserStatus.isBlank()) ||
                                (club.club_type=="2" && clubUserStatus.isBlank())) {
                            club.is_allow_feeds = "1"

                        }else if(club.club_type=="2" && clubUserStatus=="0"){
                            club.is_allow_feeds = ""

                        }else if(club.club_type=="2" && clubUserStatus=="0"){
                            club.is_allow_feeds = "0"

                        }/*else if(club.club_type.equals("2") && clubUserStatus.isBlank()){
                            club.is_allow_feeds = "1"
                        }*/


                        club.club_user_status = clubUserStatus
                        //list.get(pos).club_user_status = clubUserStatus

                        if(clubUserStatus=="1"){
                            listner.onJoinedClub(club)
                            list.removeAt(pos)
                            notifyItemRemoved(pos)
                        }else if(club.club_type=="2" && clubUserStatus == "") {
                            notifyItemChanged(pos)
                        }else if(clubUserStatus==""){
                            listner.onLeavedClub(club)
                            list.removeAt(pos)
                            notifyItemRemoved(pos)
                        }else  notifyItemChanged(pos)
                    } else{
                        Util.showToast(obj.getString("message"),context)
                    }
                }catch (ex: Exception){
                    Util.showToast(R.string.swr,context)
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
                params["isAllowFeeds"] = if(club.is_allow_feeds.isBlank())"1" else club.is_allow_feeds
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["language"] = SessionManager.getObj().language
                params["authToken"] = ClubZ.currentUser?.auth_token!!
                return params
            }
        }.execute()
    }


}  interface MyClub{
    fun onJoinedClub(club : Clubs)
    fun onLeavedClub(club : Clubs)
    fun onSilenceClub(club : Clubs, position: Int)
}