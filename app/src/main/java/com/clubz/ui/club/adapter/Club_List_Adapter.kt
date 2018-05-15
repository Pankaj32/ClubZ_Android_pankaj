package com.clubz.ui.club.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Clubs
import com.clubz.data.remote.WebService
import com.clubz.ui.club.ClubDetailIntent
import com.clubz.ui.dialogs.LeaveClubDialog
import com.clubz.utils.CircleTransform
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by mindiii on २०/३/१८.
 */
class Club_List_Adapter( internal var list : ArrayList<Clubs> , internal var context :Context , internal val activity : HomeActivity) : RecyclerView.Adapter<Club_List_Adapter.Holder>() {


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        var holder = Holder(LayoutInflater.from(context).inflate(R.layout.baseclub_list, null))
        setUpClick(holder)
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var obj = list.get(position)
        holder.tvname.setText(obj.club_name)
        holder.leadby.setText(obj.full_name)
        holder.body_des.setText(obj.club_description)
        holder.members.text = obj.members+" "+context.getString(R.string.members)
        if(activity.latitude==0.0 && activity.longitude==0.0){
            holder.distance.setText("-- Km")
        }
        else {
            try {
                holder.distance.setText(" ${(Util.getDistanceMile(arrayOf(activity.latitude, activity.longitude,obj.club_latitude.toDouble() , obj.club_longitude.toDouble()))*1.60934).toInt()} Km");
            }catch (ex :Exception){}
        }


        if(obj.user_id.equals(ClubZ.currentUser?.id)){
            holder.switch1.visibility = View.GONE
            holder.btn_join.visibility = View.GONE

        }else{
            holder.switch1.visibility = View.VISIBLE
            holder.btn_join.visibility = View.VISIBLE
            // emtpty means no action performed yet, 0 = pending for approvial by Club admin , 1 = Joined club
            // obj.club_type 1 = public club and 2 = private club
            if(obj.club_type.equals("1") && obj.club_user_status.isBlank()){
                // holder.btn_join.setText(if(obj.club_type.equals("1"))R.string.join else R.string.req_join)
                holder.btn_join.text = context.getString(R.string.join)

            }else if(obj.club_type.equals("1") && obj.club_user_status.equals("1")){
                holder.btn_join.text = context.getString(R.string.leave)

            }else if(obj.club_type.equals("2") && obj.club_user_status.isBlank()){
                holder.btn_join.text = context.getString(R.string.req_join)

            }else if(obj.club_type.equals("2") && obj.club_user_status.equals("0")){
                holder.btn_join.text = context.getString(R.string.dismiss)

            }else if(obj.club_type.equals("2") && obj.club_user_status.equals("1")){
                holder.btn_join.text = context.getString(R.string.leave)

            }

            holder.switch1.isChecked = !obj.is_allow_feeds.equals("0")
            holder.switch1.isEnabled = obj.club_user_status.equals("1")
        }

       // holder.btn_join.setText(if(obj.club_type.equals("1"))R.string.join else R.string.req_join)

        holder.img_status.setImageResource(if(obj.club_type.equals("1")) R.drawable.ic_unlocked_padlock_black else R.drawable.ic_locked_padlock_black)
        holder.status.setText(if(obj.club_type.equals("1")) R.string.Public else R.string.Private)
        try {
            if(!obj.club_icon.endsWith("defaultProduct.png")) Picasso.with(context).load(obj.club_icon).transform(CircleTransform()).placeholder(R.drawable.img_gallery).into(holder.image_club, object : Callback {
                override fun onSuccess() {
                    holder.image_club.setPadding(0,0,0,0)
                }

                override fun onError() {

                }
            })
        }catch (ex :Exception){}

       /* holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                activity.draweHandler(activity.lastDrawerGravity)
                //activity.addFragment(Frag_ClubDetails().setData(obj),0);
                val intent = ClubDetailActivity.newIntent(context, obj)
                context.startActivity(intent)
            }
        })

        holder.btn_join.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list.get(position)

            if(club.club_type.equals("1") && club.club_user_status.equals("1")){
                showLeaveConfirationDialog(club, pos)
            }else if(club.club_type.equals("2") && club.club_user_status.equals("1")){
                showLeaveConfirationDialog(club, pos)
            }else{
                joinClub(club, pos)
            }
        }*/
    }

    override fun getItemCount(): Int {
        return list.size;

    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvname      = itemView.findViewById<TextView>(R.id.tvname)
        var leadby      = itemView.findViewById<TextView>(R.id.leadby)
        var status      = itemView.findViewById<TextView>(R.id.status)
        var members     = itemView.findViewById<TextView>(R.id.members)
        var body_des    = itemView.findViewById<TextView>(R.id.body_des)
        var distance    = itemView.findViewById<TextView>(R.id.distance)
        var img_status  = itemView.findViewById<ImageView>(R.id.img_status)
        var image_club  = itemView.findViewById<ImageView>(R.id.image_club)
        var btn_join    = itemView.findViewById<Button>(R.id.btn_join)
        var switch1     = itemView.findViewById<Switch>(R.id.switch1)
    }


    fun setUpClick(holder: Holder){
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list.get(pos)
            context.startActivity(context.ClubDetailIntent(club))
            //activity.draweHandler(activity.lastDrawerGravity)
            //activity.addFragment(Frag_ClubDetails().setData(obj),0);
            //val intent = ClubDetailActivity.newIntent(context, club)
        }

        holder.btn_join.setOnClickListener {
            val pos = holder.adapterPosition
            val club = list.get(pos)

            if(club.club_type.equals("1") && club.club_user_status.equals("1")){
                showLeaveConfirationDialog(club, pos)
            }else if(club.club_type.equals("2") && club.club_user_status.equals("1")){
                showLeaveConfirationDialog(club, pos)
            }else{
                joinClub(club, pos)
            }
        }
    }

    fun showLeaveConfirationDialog(club : Clubs, pos : Int){
        object : LeaveClubDialog(context) {
            override fun onLeaveClubClicked() {
                this.dismiss();
                joinClub(club, pos)
            }

            override fun onCloseClicked() {
                this.dismiss();
            }
        }.show()
    }


    fun joinClub(club : Clubs, pos : Int){

        val dialog = CusDialogProg(activity )
        dialog.show()
        var clubUserStatus : String = ""

        if(club.club_type.equals("1") && club.club_user_status.isBlank()){
            clubUserStatus = "1"

        }else if(club.club_type.equals("1") && club.club_user_status.equals("1")){
            clubUserStatus = ""

        }else if(club.club_type.equals("2") && club.club_user_status.isBlank()){
            clubUserStatus = "0"

        }else if(club.club_type.equals("2") && club.club_user_status.equals("0")){
            clubUserStatus = ""

        }else if(club.club_type.equals("2") && club.club_user_status.equals("1")){
            clubUserStatus = ""
        }

        val api: String
        if(clubUserStatus.isEmpty())
            api = WebService.club_leave
        else api = WebService.club_join

        object  : VolleyGetPost(activity , activity ,api,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){

                        if(club.club_type.equals("1") && clubUserStatus.equals("1")){
                            club.is_allow_feeds = ""

                        }else if( (club.club_type.equals("1") && clubUserStatus.isBlank()) || (club.club_type.equals("2") && clubUserStatus.isBlank())) {
                            club.is_allow_feeds = "1"

                        }else if(club.club_type.equals("2") && clubUserStatus.equals("0")){
                            club.is_allow_feeds = ""

                        }else if(club.club_type.equals("2") && clubUserStatus.equals("0")){
                            club.is_allow_feeds = "0"

                        }/*else if(club.club_type.equals("2") && clubUserStatus.isBlank()){
                            club.is_allow_feeds = "1"
                        }*/

                        club.club_user_status = clubUserStatus
                        //list.get(pos).club_user_status = clubUserStatus
                        notifyItemChanged(pos)
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
                params.put("clubId", club.clubId)
                params.put("clubUserStatus",clubUserStatus)
                params.put("isAllowFeeds", if(club.is_allow_feeds.isBlank())"1" else club.is_allow_feeds)
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", ClubZ.currentUser?.auth_token!!)
                return params
            }
        }.execute()

    }
}