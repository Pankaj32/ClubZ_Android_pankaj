package com.clubz.fragment.home

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.android.volley.VolleyError
import com.clubz.Adapter.Club_List_Adapter
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.Cus_Views.Cus_dialog_material_design
import com.clubz.Home_Activity
import com.clubz.R
import com.clubz.fragment.FilterListner
import com.clubz.fragment.Textwatcher_Statusbar
import com.clubz.helper.Permission
import com.clubz.helper.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.helper.WebService
import com.clubz.model.Clubs
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_list.*
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by mindiii on 19/3/18.
 */
class Frag_Search_Club :Fragment() , FilterListner , Textwatcher_Statusbar{



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_list, null)
    }


    override fun onFilterChnge() {
        checkLocation()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocation()
        view!!.setOnClickListener{}

    }

    override fun onDestroy() {
        (activity as Home_Activity).filterListner = null
        super.onDestroy()

    }

    //TODO pagination
    /**
     *@clubtype 1 means public , 2 means private , 0means all
     */
    fun searchClubs(text : String = "", offset :String = "0" ){
        val activity = activity as Home_Activity
        val dialog = CusDialogProg(activity );
        if(text.isBlank())dialog.show();
        object  : VolleyGetPost(activity , activity , WebService.club_search,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss();
                //{"status":"success","message":"found","data":[{"clubId":"20","user_id":"52","club_name":"Mindiii","club_description":"this is a mindiii company","club_image":"http:\/\/clubz.co\/dev\/uploads\/club_image\/32e494d9cb36f6a0d73d792bebee8e6e.jpg","club_foundation_date":"2018-03-15","club_email":"pankaj.mindiii@gmail.com","club_contact_no":"9630612281","club_country_code":"+91","club_website":"www.google.com","club_location":"Indore Jn.","club_address":"140 square","club_latitude":"22.7170909","club_longitude":"75.8684423","club_type":"1","club_category_id":"2","terms_conditions":"indore company","comment_count":"0","status":"1","crd":"2018-03-16 11:32:09","upd":"2018-03-16 11:32:09","club_category_name":"Sports","full_name":"Pankaj","club_user_status":"","distance":""}]}
                try{
                     val obj = JSONObject(response);
                    if(obj.getString("status").equals("success")){
                        val list  = Gson().fromJson<ArrayList<Clubs>>(obj.getJSONArray("data").toString() , Type_Token.club_list);
                        list_recycler.adapter = Club_List_Adapter(list,context , activity);
                    }
                    else{
                        list_recycler.adapter = Club_List_Adapter(ArrayList<Clubs>(),context ,activity)
                        Util.showToast(obj.getString("message"),context)
                    }
                }catch (ex: Exception){
                    Util.showToast(R.string.swr,context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                Util.e("Error", error.toString())
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("latitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.latitude.toString() )+"");
                params.put("longitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.longitude.toString() )+"");
                params.put("clubCategoryId","");
                params.put("searchText",text);
                params.put("offset",offset);
                params.put("limit","10");
                params.put("clubType",activity.isPrivate.toString() );
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute()

    }


    override fun afterchangeText(p0: Editable?) {
        checkLocation(p0.toString())
    }

    fun checkLocation(p0: String = ""){
        val permission = Permission(activity,context)
        if(!permission.checkLocationPermission()) return
        if(!permission.askForGps()) return
        val activity = (activity as Home_Activity)
        if (activity.latitude==0.0 && activity.longitude==0.0){ val al_dialog : Cus_dialog_material_design  = object : Cus_dialog_material_design(context){
            override fun onDisagree() {
                this.dismiss()
            }

            override fun onAgree() {
                this.dismiss()
                val dialog = CusDialogProg(activity );
                dialog.show();
                activity.requestLocationUpdates()
                Handler().postDelayed(Runnable {
                    dialog.dismiss()
                    checkLocation()
                },5000)
            }

        }
            al_dialog.setTextAlert_msg(R.string.t_er_loc_msg);
            al_dialog.setTextAlert_title(R.string.t_error_location);
            al_dialog.setTextAgree(R.string.ok);
            al_dialog.setTextDisagree(R.string.cancel);
            al_dialog.show()
    }else{
          searchClubs(p0)
      }
    }


}