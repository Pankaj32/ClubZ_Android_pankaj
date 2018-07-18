package com.clubz.ui.club.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.clubz.R
import com.clubz.data.model.Club_Category
import com.clubz.utils.Constants

/**
 * Created by mindiii on 3/14/18.
 */

class CreateClub_Spinner (context: Context,internal val list : List<Any> ,internal val type :Int): ArrayAdapter<Any>(context, R.layout.spn_view_crate_clb ,list) {
    var inflater : LayoutInflater = LayoutInflater.from(context);
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //val itemView = inflater.inflate(if(type==Constants.CreateClub_Spinner_Type_ClubCategory)R.layout.spn_view_crate_clb else R.layout.spn_view_crate_clb_end, parent, false)
        val itemView = inflater.inflate(R.layout.spn_view_crate_clb, parent, false)
        val name = itemView.findViewById<TextView>(R.id.name)

        when(type){
            Constants.CreateClub_Spinner_Type_ClubCategory->{
                val obj = list[position] as Club_Category
                name.text = obj.club_category_name.trim()
            }
            Constants.CreateClub_Spinner_Type_privacy_type->{
              //  img_lock.visibility =View.VISIBLE
                name.text = list[position].toString().trim()
                //val img_lock = itemView.findViewById<ImageView>(R.id.img_lock)
                //img_lock.setImageResource(if(position==0) R.drawable.ic_unlocked_padlock else R.drawable.ic_locked_padlock)
            }
        }
        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(R.layout.spn_view_crate_clb2, parent, false)
        val name = itemView.findViewById<TextView>(R.id.name)
        when(type){
            Constants.CreateClub_Spinner_Type_ClubCategory->{
                val obj = (list.get(position) as Club_Category)
                name.text = obj.club_category_name.trim()
            }
            Constants.CreateClub_Spinner_Type_privacy_type->{
                name.text = list.get(position).toString()
            }
        }
        return itemView
    }

}
