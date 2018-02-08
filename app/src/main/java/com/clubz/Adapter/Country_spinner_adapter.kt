package com.clubz.Adapter

import android.content.Context
import android.opengl.Visibility
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.clubz.model.Country_Code
import android.widget.TextView
import com.clubz.R


/**
 * Created by mindiii on 2/7/18.
 */
class Country_spinner_adapter(internal var context: Context , internal var list : ArrayList<Country_Code>, id :Int,internal val groupid :Int) : ArrayAdapter<Country_Code>(context, id ,list) {

    var inflater : LayoutInflater = LayoutInflater.from(context);
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupid, parent, false)
        itemView.findViewById<TextView>(R.id.arow).visibility = View.VISIBLE
        itemView.findViewById<TextView>(R.id.code).setText("+"+list.get(position).phone_code)
        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupid, parent, false)
        itemView.setBackgroundResource(R.color.bg_violet)
        itemView.findViewById<TextView>(R.id.country).visibility= View.VISIBLE
        itemView.findViewById<TextView>(R.id.country).setText(list.get(position).country_name)
        itemView.findViewById<TextView>(R.id.code).setText("+"+list.get(position).phone_code+"  ")
        return itemView;
    }


    //TO commit


}