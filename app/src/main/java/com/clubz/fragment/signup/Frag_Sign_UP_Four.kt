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
import kotlinx.android.synthetic.main.frag_sign_up_four.*
import java.util.ArrayList

/**
 * Created by mindiii on 2/7/18.
 */


class Frag_Sign_UP_Four : Fragment(), View.OnClickListener {

    var list1 : ArrayList<String> = ArrayList();
    var list2 : ArrayList<String> = ArrayList();
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_four ,null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(plus1   , plus2))view.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.plus1->if(canaddIntrest())addIntrest();
            R.id.plus2->if(canaddSkill())addSkill();
            R.id.skip->{}//
        }
    }


    fun canaddIntrest(): Boolean{
        (activity as Sign_up_Activity).hideKeyBoard()
        if(intrest.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addintres)
            return false;
        }
        for (s  in list1){
            if(s.trim().toLowerCase().equals(intrest.text.toString().trim().toLowerCase())){
                Util.showSnake(context,view!!,R.string.a_already_intrest)
                return false
            }
        }
        return true;
    }

    fun canaddSkill(): Boolean{
        (activity as Sign_up_Activity).hideKeyBoard()
        if(skill_set.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addskill)
            return false;
        }
        for (s  in list2){
            if(s.trim().toLowerCase().equals(skill_set.text.toString().trim().toLowerCase())){
                Util.showSnake(context,view!!,R.string.a_already_skill)
                return false
            }
        }
        return true;
    }

    fun addIntrest(){
        val chip = object : ChipView(context,chip_grid.childCount.toString()){
            override fun setDeleteListner(chipView: ChipView?) {
            }
        }
        chip.setText(intrest.text.toString()+"")
        list1.add(intrest.text.toString().trim())
        chip_grid.addView(chip);
        intrest.setText("");

    }
    fun addSkill(){
        val chip = object : ChipView(context,chip_grid2.childCount.toString()){
            override fun setDeleteListner(chipView: ChipView?) {
            }
        }
        chip.setText(skill_set.text.toString()+"")
        list2.add(skill_set.text.toString().trim())
        chip_grid2.addView(chip);
        skill_set.setText("");
    }
}