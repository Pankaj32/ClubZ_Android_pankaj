package com.clubz.ui.authentication.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.ui.cv.ChipView
import com.clubz.R
import com.clubz.ui.core.BaseFragment
import com.clubz.utils.Util
import kotlinx.android.synthetic.main.frag_sign_up_four.*
import java.util.ArrayList

/**
 * Created by mindiii on 2/7/18.
 */

class FragSignUp4 : BaseFragment(), View.OnClickListener {

    var list1 : ArrayList<String> = ArrayList()
    var list2 : ArrayList<String> = ArrayList()
    var canCallApi : Boolean = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_four ,null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(v in arrayOf(plus1, plus2 ,next2 ,skip)) v.setOnClickListener(this)

        intrest.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //getSearchs("1",p0.toString())
            }
        })

        skill_set.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) { }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //getSearchs("2",p0.toString())
            }
        })
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.plus1->if(canaddIntrest())addIntrest()
            R.id.plus2->if(canaddSkill())addSkill()
            R.id.skip->Util.showSnake(context,view!!,R.string.temp_sucess)// TODO temp
            R.id.next2-> Util.showSnake(context,view!!,R.string.temp_sucess)// TODO temp
        }
    }


    private fun canaddIntrest(): Boolean{
        listner.hideKeyBoard()
        if(intrest.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addintres)
            return false
        }
        for (s  in list1){
            if(s.trim().toLowerCase().equals(intrest.text.toString().trim().toLowerCase())){
                Util.showSnake(context,view!!,R.string.a_already_intrest)
                return false
            }
        }
        return true
    }

    private fun canaddSkill(): Boolean{
        listner.hideKeyBoard()
        if(skill_set.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addskill)
            return false
        }
        for (s  in list2){
            if(s.trim().toLowerCase().equals(skill_set.text.toString().trim().toLowerCase())){
                Util.showSnake(context,view!!,R.string.a_already_skill)
                return false
            }
        }
        return true
    }

    fun addIntrest(){
        val chip = object : ChipView(context,chip_grid.childCount.toString()){
            override fun getLayout(): Int {
                return 0
            }

            override fun setDeleteListner(chipView: ChipView?) {
            }
        }
        chip.text = intrest.text.toString()+""
        list1.add(intrest.text.toString().trim())
        chip_grid.addView(chip)
        intrest.setText("")

    }
    fun addSkill(){
        val chip = object : ChipView(context,chip_grid2.childCount.toString()){
            override fun getLayout(): Int {
                return 0
            }
            override fun setDeleteListner(chipView: ChipView?) {
            }
        }
        chip.text = skill_set.text.toString()+""
        list2.add(skill_set.text.toString().trim())
        chip_grid2.addView(chip)
        skill_set.setText("")
    }

   /* fun getSearchs(searchType :String , searchText : String){
        if(!canCallApi) return ;
        object : VolleyGetPost(activity,activity,WebService.auto_serch, false){
            override fun onVolleyResponse(response: String?) {
                Util.e("Response" , response!!);
            }

            override fun onVolleyError(error: VolleyError?) {

            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("searchType",searchType);
                params.put("searchText",searchText);
                return  params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {

                params.put("authToken",SessionManager.obj.getUser().auth_token);
                params.put("language",SessionManager.obj.getLanguage());
                return params
            }
        }
    }

    fun submitDetails(){
        object : VolleyGetPost(activity,activity,WebService.update_user, false){
            override fun onVolleyResponse(response: String?) {

            }

            override fun onVolleyError(error: VolleyError?) {

            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                *//*params.put("interests",);
                params.put("skills",);
                params.put("affiliates",);*//*
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken",SessionManager.obj.getUser().auth_token);
                params.put("language",SessionManager.obj.getLanguage());
                return params
            }
        }
    }*/

    fun getIntrests() : String{
        return list1.toString()
    }

    fun getSkills() :  String{
        return list1.toString()
    }
}