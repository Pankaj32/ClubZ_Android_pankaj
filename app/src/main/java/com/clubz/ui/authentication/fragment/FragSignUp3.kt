package com.clubz.ui.authentication.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.frag_sign_up_three.*
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by mindiii on 2/7/18.
 */


class FragSignUp3 : SignupBaseFragment(), View.OnClickListener {

    private lateinit var contact : String
    private lateinit var _code : String
    lateinit var authtoken : String
    var list : ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_three , null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(v in arrayOf(plus ,done ))v.setOnClickListener(this)
        affiliates.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        mainLayout.setListener(this)
    }

    override fun getScrollView(): ScrollView {
        return scrollView
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.plus-> if(canAdd())addView()
            R.id.done->/*{val activity = activity as SignupActivity;
                startActivity(Intent(activity, HomeActivity::class.java))
            activity.finish();}*/
            updateUserdata()
        }
        }


    private fun canAdd(): Boolean{
        listner.hideKeyBoard()
        if(affiliates.text.isBlank()){
            Util.showSnake(context,view!!,R.string.a_addaffil)
            return false
        }
        if(list.size>=10){
            Util.showSnake(context,view!!,R.string.a_max10)
            return false
        }
        for (s in list){
            if(s.trim().toLowerCase() == affiliates.text.toString().trim().toLowerCase()){
                Util.showSnake(context,view!!,R.string.a_already_affil)
                return false
            }
        }
        return true
    }

    fun addView(){
        val chip = object : ChipView(context,chip_grid.childCount.toString()){
            override fun getLayout(): Int { return 0 }
            override fun setDeleteListner(chipView: ChipView?) {
                for (s  in list){
                    if(s.trim().toLowerCase() == chipView!!.text.trim().toLowerCase()){
                        list.remove(s)
                        break
                    }
                }
            }
        }

        chip.text = affiliates.text.toString()
        list.add(affiliates.text.toString().trim())
        chip_grid.addView(chip)
        affiliates.setText("")
    }

    fun getValues() :String{
        Util.e("values ",list.toString())
        return if(list.size==0) "" else list.toString().replace("[","").replace("]","")
    }
    fun setData( contact : String , code : String , auth :String) : FragSignUp3 {
        this.contact = contact
        _code = code
        authtoken = auth
        return this
    }

    private fun updateUserdata(){
        val dialog = CusDialogProg(context)
        dialog.show()
        object  : VolleyGetPost(signupActivity,context, WebService.update_user,false
        ,true) {
            override fun onVolleyResponse(response: String?) {
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status") == "success"){
                        if(list.size>0){
                            SessionManager.getObj().setAffiliates("1")
                        }
                        startActivity(Intent(signupActivity, HomeActivity::class.java))
                        signupActivity.finish()
                    }else{
                        Toast.makeText(context,obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }catch (ex :Exception){
                    Toast.makeText(context,R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss() }
            override fun onNetError() { dialog.dismiss() }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["interests"] = ""
                params["skills"] = ""
                params["affiliates"] = getValues()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["language"] = SessionManager.getObj().language
                params["authToken"] = authtoken //Its Temp
                return params
            }
        }.execute()
    }

}