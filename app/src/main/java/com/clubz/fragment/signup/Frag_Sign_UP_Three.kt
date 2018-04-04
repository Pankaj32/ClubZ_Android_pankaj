package com.clubz.fragment.signup

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.Cus_Views.ChipView
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.Home_Activity
import com.clubz.R
import com.clubz.Sign_up_Activity
import com.clubz.helper.SessionManager
import com.clubz.helper.WebService
import com.clubz.model.Country_Code
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import kotlinx.android.synthetic.main.frag_sign_up_three.*
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by mindiii on 2/7/18.
 */


class Frag_Sign_UP_Three : Fragment(), View.OnClickListener {
    lateinit var _contact : String
    lateinit var _code : String
    lateinit var _authtoken : String

    var list : ArrayList<String> = ArrayList();
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_three , null);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(plus ,done ))view.setOnClickListener(this)
        affiliates.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.plus-> if(canadd())addView();
            R.id.done->/*{val activity = activity as Sign_up_Activity;
                startActivity(Intent(activity, Home_Activity::class.java))
            activity.finish();}*/
            updateUserdata()
        }
        }


    fun canadd(): Boolean{
        (activity as Sign_up_Activity ).hideKeyBoard()
        if(affiliates.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addaffil)
            return false;
        }
        if(list.size>=10){
            Util.showSnake(context,view!!,R.string.a_max10)
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
                for (s  in list){
                    if(s.trim().toLowerCase().equals(chipView!!.text.trim().toLowerCase())){
                        list.remove(s);
                        break;
                    }
                }
            }
        }

        chip.setText(affiliates.text.toString())
        list.add(affiliates.text.toString().trim())
        chip_grid.addView(chip);
        affiliates.setText("");
    }

    fun getValues() :String{
        Util.e("values ",list.toString());
        return if(list.size==0) "" else list.toString().replace("[","").replace("]","")
    }
    fun setData( contact : String , code : String , auth :String) :Frag_Sign_UP_Three{
        _contact = contact;
        _code = code;
        _authtoken = auth;
        return this;
    }

    fun updateUserdata(){
        val activity = activity as Sign_up_Activity;
        val dialog = CusDialogProg(context);
        dialog.show();
        object  : VolleyGetPost(activity,context, WebService.update_user,false) {
            override fun onVolleyResponse(response: String?) {
                //{"status":"fail","message":"The number +919770495603 is unverified. Trial accounts cannot send messages to unverified numbers; verify +919770495603 at twilio.com\/user\/account\/phone-numbers\/verified, or purchase a Twilio number to send messages to unverified numbers."}
                //{"status":"fail","message":"This mobile number is already registered."}
                //{"status":"success","message":"Registered successfully, Generate verify code successfully sent","otp":"3319","step":1}

                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        startActivity(Intent(activity, Home_Activity::class.java))
                        activity.finish()
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex :Exception){
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss();
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()

            }

            override fun onNetError() {
                dialog.dismiss()

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("interests" , "");
                params.put("skills" , "");
                params.put("affiliates" , getValues());
                Util.e("params" , params.toString())
                return params;

            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put( "language", SessionManager.getObj().getLanguage());
                params.put( "authToken", _authtoken); //Its Temp
                Util.e("headers" , params.toString())
                return params

            }
        }.execute()
    }

}