package com.clubz.Spinner_adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.clubz.R

/**
 * Created by mindiii on 3/14/18.
 */

class CreateClub_Spinner (context: Context, resource: Int, list : List<Any>): ArrayAdapter<Any>(context, resource ,list) {
    var inflater : LayoutInflater = LayoutInflater.from(context);
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = if(convertView==null)inflater.inflate(R.layout.spn_view_crate_clb, parent, false) else convertView
        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(R.layout.spn_view_crate_clb2, parent, false)
        return itemView;
    }

}
