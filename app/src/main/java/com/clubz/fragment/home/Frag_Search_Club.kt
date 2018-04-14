package com.clubz.fragment.home

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.Adapter.Club_List_Adapter
import com.clubz.Adapter.Potential_Search_Adapter
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.Cus_Views.Cus_dialog_material_design
import com.clubz.HomeActivity
import com.clubz.R
import com.clubz.fragment.FilterListner
import com.clubz.fragment.Textwatcher_Statusbar
import com.clubz.helper.Permission
import com.clubz.helper.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.helper.WebService
import com.clubz.model.Club_Potential_search
import com.clubz.model.Clubs
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_list.*
import org.json.JSONObject
import java.util.ArrayList
import com.clubz.Cus_Views.SimpleDividerItemDecoration


class Frag_Search_Club :Fragment() , FilterListner , Textwatcher_Statusbar, View.OnClickListener {
    override fun onClick(v: View?) {

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_list, null)
    }


    override fun onFilterChnge() {
        checkLocation()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ClubSearch_Potential()
       // checkLocation()
        for(views in arrayOf(search_layout,view!!))views.setOnClickListener(this)

    }

    override fun onDestroy() {
        (activity as HomeActivity).filterListner = null
        super.onDestroy()

    }

    //TODO pagination
    /**
     *@clubtype 1 means public , 2 means private , 0 means all
     */
    fun searchClubs(text : String = "",showProgres : Boolean = false, offset :String = "0"  ){
        val activity = activity as HomeActivity
        val dialog = CusDialogProg(activity )
        if(text.isBlank() || showProgres)dialog.show()
        object  : VolleyGetPost(activity , activity , WebService.club_search,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                //{"status":"success","message":"found","data":[{"clubId":"20","user_id":"52","club_name":"Mindiii","club_description":"this is a mindiii company","club_image":"http:\/\/clubz.co\/dev\/uploads\/club_image\/32e494d9cb36f6a0d73d792bebee8e6e.jpg","club_foundation_date":"2018-03-15","club_email":"pankaj.mindiii@gmail.com","club_contact_no":"9630612281","club_country_code":"+91","club_website":"www.google.com","club_location":"Indore Jn.","club_address":"140 square","club_latitude":"22.7170909","club_longitude":"75.8684423","club_type":"1","club_category_id":"2","terms_conditions":"indore company","comment_count":"0","status":"1","crd":"2018-03-16 11:32:09","upd":"2018-03-16 11:32:09","club_category_name":"Sports","full_name":"Pankaj","club_user_status":"","distance":""}]}
                try{
                     val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        val list  = Gson().fromJson<ArrayList<Clubs>>(obj.getJSONArray("data").toString() , Type_Token.club_list)
                        if(list_recycler.adapter == null){
                            list_recycler.adapter = Club_List_Adapter(list,context , activity)
                        }else{
                            (list_recycler.adapter as Club_List_Adapter).list = list
                            list_recycler.adapter.notifyDataSetChanged()
                        }
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
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("latitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.latitude.toString() )+"")
                params.put("longitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.longitude.toString() )+"")
                params.put("clubCategoryId","")
                params.put("searchText",text)
                params.put("offset",offset)
                params.put("limit","10")
                params.put("clubType",activity.isPrivate.toString() )
                Util.e("parms search", params.toString())
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
        if(p0!=null && recycler_potential_search.adapter !=null){
            search_layout.visibility = View.VISIBLE
            (recycler_potential_search.adapter as Potential_Search_Adapter).filter.filter(p0.toString())
            if(p0.toString().isBlank()){
                search_layout.visibility = View.GONE }
        }
    }

    fun checkLocation(p0: String = ""){
        val permission = Permission(activity,context)
        if(!permission.checkLocationPermission()) return
        val activity = (activity as HomeActivity)
                    if (activity.latitude==0.0 && activity.longitude==0.0  && permission.askForGps()){ val al_dialog : Cus_dialog_material_design  = object : Cus_dialog_material_design(context){
                        override fun onDisagree() {
                            this.dismiss()
                        }

                        override fun onAgree() {
                            this.dismiss()
                            val dialog = CusDialogProg(activity )
                            dialog.show()
                            //activity.requestLocationUpdates()
                            Handler().postDelayed(Runnable {
                                dialog.dismiss()
                               try{ checkLocation()}catch (e :Exception ){}
                            },5000)
                        }

                    }
                        al_dialog.setTextAlert_msg(R.string.t_er_loc_msg)
                        al_dialog.setTextAlert_title(R.string.t_error_location)
                        al_dialog.setTextAgree(R.string.ok)
                        al_dialog.setTextDisagree(R.string.cancel)
                        al_dialog.show()
                    }else{
                        searchClubs(p0)
                    }
        }



    fun ClubSearch_Potential(){
        val activity  = activity as HomeActivity
        val lati= if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.latitude.toString()
        val longi=if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.longitude.toString()
        object  : VolleyGetPost(activity , activity, "${WebService.nearclub_names}?latitude=$lati&longitude=$longi",true){
            override fun onVolleyResponse(response: String?) {
                try {

                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        val searchlist : ArrayList<Club_Potential_search> = Gson().fromJson<ArrayList<Club_Potential_search>>(obj.getString("data"), Type_Token.potential_list)
                        recycler_potential_search.layoutManager = LinearLayoutManager(context)

                        recycler_potential_search.addItemDecoration(SimpleDividerItemDecoration(context))
                         var adapter = object : Potential_Search_Adapter(context,searchlist , activity){
                             override fun onItemClick(serch_obj: Club_Potential_search) {
                                 search_layout.visibility =View.GONE
                                 searchClubs(serch_obj.club_name ,true)
                             }
                         }
                        recycler_potential_search.adapter = adapter

                    }else{

                    }

                }catch (ex: Exception){
                    ex.printStackTrace()
                }

            }

            override fun onVolleyError(error: VolleyError?) {

            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /*params.put("latitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.latitude.toString() )+"")
                params.put("longitude",(if(activity.latitude==0.0 && activity.longitude==0.0)"" else activity.longitude.toString() )+"")*/
                return  params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return  params
            }
        }.execute()
    }





}