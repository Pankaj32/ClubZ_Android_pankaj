package com.clubz.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.clubz.Home_Activity
import com.clubz.R
import com.clubz.fragment.home.Frag_ClubDetails
import com.clubz.model.Clubs
import com.clubz.util.CircleTransform
import com.clubz.util.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONArray
import java.util.ArrayList

/**
 * Created by mindiii on २०/३/१८.
 */
class Club_List_Adapter( internal var list : ArrayList<Clubs> , internal var context :Context , internal val activity :Home_Activity) : RecyclerView.Adapter<Club_List_Adapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.baseclub_list, null))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var obj = list.get(position)
        holder.tvname.setText(obj.club_name)
        holder.leadby.setText(obj.full_name)
        holder.body_des.setText(obj.club_description)
        holder.members.setText(obj.members+" "+context.resources.getString(R.string.members))
        if(activity.latitude==0.0 && activity.longitude==0.0){
            holder.distance.setText("-- Km")
        }
        else {
            try {
                holder.distance.setText(" ${(Util.getDistanceMile(arrayOf(activity.latitude, activity.longitude,obj.club_latitude.toDouble() , obj.club_longitude.toDouble()))*1.60934).toInt()} Km");
            }catch (ex :Exception){}
        }
        holder.btn_join.setText(if(obj.club_type.equals("1"))R.string.join else R.string.req_join)
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
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                activity.addFragment(Frag_ClubDetails().setData(obj),0);
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size;
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvname = itemView.findViewById<TextView>(R.id.tvname);
        var leadby = itemView.findViewById<TextView>(R.id.leadby);
        var status = itemView.findViewById<TextView>(R.id.status);
        var members = itemView.findViewById<TextView>(R.id.members);
        var body_des = itemView.findViewById<TextView>(R.id.body_des);
        var distance = itemView.findViewById<TextView>(R.id.distance);
        var img_status = itemView.findViewById<ImageView>(R.id.img_status);
        var image_club = itemView.findViewById<ImageView>(R.id.image_club);
        var btn_join = itemView.findViewById<Button>(R.id.btn_join);
    }

}