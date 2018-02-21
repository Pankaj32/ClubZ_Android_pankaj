package com.clubz.fragment.signup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.Cus_Views.ChipView
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.util.Util
import kotlinx.android.synthetic.main.frag_sign_up_three.*
import java.util.ArrayList

/**
 * Created by mindiii on 2/7/18.
 */


class Frag_Sign_UP_Three : Fragment(), View.OnClickListener {

    var list : ArrayList<String> = ArrayList();
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_three , null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(plus ,next ))view.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.plus-> if(canadd())addView();
            R.id.next-> (activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Four())
            R.id.skip-> (activity as Sign_up_Activity).replaceFragment(Frag_Sign_UP_Four())
        }
    }

    fun canadd(): Boolean{
        (activity as Sign_up_Activity ).hideKeyBoard()
        if(affiliates.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addaffil)
            return false;
        }
        for (s  in list){
            if(s.trim().toLowerCase().equals(affiliates.text.toString().trim().toLowerCase())){
                Util.showSnake(context,view!!,R.string.a_already_affil)
                return false
            }
        }
        return true;
    }

    fun addView(){
        val chip = object : ChipView(context,chip_grid.childCount.toString()){
            override fun setDeleteListner(chipView: ChipView?) {

            }
        }
        chip.setText(affiliates.text.toString())
        list.add(affiliates.text.toString().trim())
        chip_grid.addView(chip);
        affiliates.setText("");
    }


}