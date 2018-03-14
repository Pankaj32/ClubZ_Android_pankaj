package com.clubz.Spinner_adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.clubz.model.Country_Code
import android.widget.TextView
import com.clubz.R
import com.clubz.util.Util


/**
 * Created by mindiii on 2/7/18.
 */
class Country_spinner_adapter(internal var context: Context , internal var list : ArrayList<Country_Code>, id :Int,internal val groupid :Int) : ArrayAdapter<Country_Code>(context, id ,list) {

    var inflater : LayoutInflater = LayoutInflater.from(context);
    val list_image = Util.imageResources

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView = if(convertView==null)inflater.inflate(R.layout.spinner_img, parent, false) else convertView
            itemView!!.findViewById<ImageView>(R.id.flag).setImageResource(list_image.get(position))
            return itemView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupid, parent, false)
        itemView.setBackgroundResource(R.color.white)
        itemView.findViewById<TextView>(R.id.country).visibility= View.VISIBLE
        itemView.findViewById<TextView>(R.id.country).setText(list.get(position).country_name)
        val code = itemView.findViewById<TextView>(R.id.code)
        code.visibility = View.VISIBLE
                code.setText("+"+list.get(position).phone_code+"  ")
        itemView.findViewById<ImageView>(R.id.flag).setImageResource(list_image.get(position))
        return itemView;
    }



    //TO commit


}