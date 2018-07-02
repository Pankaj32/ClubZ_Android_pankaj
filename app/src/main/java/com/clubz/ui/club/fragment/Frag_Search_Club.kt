package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.club.adapter.Club_List_Adapter
import com.clubz.ui.club.adapter.Potential_Search_Adapter
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Cus_dialog_material_design
import com.clubz.ui.main.HomeActivity
import com.clubz.R
import com.clubz.ui.core.FilterListner
import com.clubz.ui.core.Textwatcher_Statusbar
import com.clubz.helper.Permission
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Club_Potential_search
import com.clubz.data.model.Clubs
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_list.*
import org.json.JSONObject
import java.util.ArrayList
import com.clubz.ui.cv.SimpleDividerItemDecoration


class Frag_Search_Club : Fragment() , FilterListner, Textwatcher_Statusbar,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    var isMyClubs : Boolean = false;
    var adapter : Potential_Search_Adapter? = null
    var searchList : ArrayList<Club_Potential_search> = arrayListOf()
    var clubList : ArrayList<Clubs> = arrayListOf()

    override fun onClick(v: View?) {

    }

    fun setFragmentType(bool : Boolean) : Frag_Search_Club{
        isMyClubs = bool
        text_top?.text = getString(R.string.my_clubs)
        getMyClubs()
        ClubSearch_Potential(1)
        return this
    }


    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_list, null)
    }


    override fun onFilterChnge() {
        checkLocation()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(views in arrayOf(search_layout,view!!))views.setOnClickListener(this)

        recycler_potential_search.layoutManager = LinearLayoutManager(context)
        recycler_potential_search.addItemDecoration(SimpleDividerItemDecoration(context))

        adapter = object : Potential_Search_Adapter(context, searchList, activity as HomeActivity){
            override fun onItemClick(serch_obj: Club_Potential_search) {
                search_layout.visibility = View.GONE
                searchClubs(serch_obj.club_name ,true)
            }
        }

        if (isMyClubs){
            text_top.text = getString(R.string.my_clubs)
            getMyClubs()
        }else{
            ClubSearch_Potential(0)
        }

        swipeRefreshLayout.setOnRefreshListener(this)
    }


    override fun onRefresh() {

        if(isMyClubs){
            getMyClubs()
            ClubSearch_Potential(1)
        }else{
            checkLocation()
        }
        swipeRefreshLayout.isRefreshing = false
    }


 /*   override fun onDestroyView() {
        super.onDestroyView()
        val activity = activity as HomeActivity
        activity.hideKeyBoard()
    }*/

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
                            list_recycler.adapter = Club_List_Adapter(list, context, activity)
                        }else{
                            (list_recycler.adapter as Club_List_Adapter).list = list
                            list_recycler.adapter.notifyDataSetChanged()
                        }
                    }
                    else{
                        list_recycler.adapter = Club_List_Adapter(ArrayList<Clubs>(), context, activity)
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
                params.put("city", ClubZ.city)
                params.put("latitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString() )+"")
                params.put("longitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString() )+"")
                params.put("clubCategoryId","")
                params.put("searchText",text)
                params.put("offset",offset)
                params.put("limit","20")
                params.put("clubType",ClubZ.isPrivate.toString())
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
        isMyClubs = false;
        text_top?.text = getString(R.string.near_club)
        val permission = Permission(activity,context)
        if(!permission.checkLocationPermission()) return
        val activity = (activity as HomeActivity)
                    if (ClubZ.latitude ==0.0 && ClubZ.longitude==0.0  && permission.askForGps()){ val al_dialog : Cus_dialog_material_design  = object : Cus_dialog_material_design(context){
                        override fun onDisagree() {
                            this.dismiss()
                        }

                        override fun onAgree() {
                            this.dismiss()
                            val dialog = CusDialogProg(activity )
                            dialog.show()
                            //activity.requestLocationUpdates()
                            Handler().postDelayed({
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
                        ClubSearch_Potential(0)
                    }
        }


    fun ClubSearch_Potential(isMyClub : Int){
        val activity  = activity as HomeActivity
        val lati= if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString()
        val longi=if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString()
        //"${WebService.nearclub_names}?latitude=$lati&longitude=$longi&isMyClub=$isMyClub" + "&city=${ClubZ.city}"
        object  : VolleyGetPost(activity , activity,
                WebService.nearclub_names,false){

            override fun onVolleyResponse(response: String?) {
                try {

                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        val searchlist : ArrayList<Club_Potential_search> = Gson().fromJson<ArrayList<Club_Potential_search>>(obj.getString("data"), Type_Token.potential_list)

                        recycler_potential_search.layoutManager = LinearLayoutManager(context)

                        recycler_potential_search.addItemDecoration(SimpleDividerItemDecoration(context))
                         val adapter = object : Potential_Search_Adapter(context,searchlist , activity){
                             override fun onItemClick(serch_obj: Club_Potential_search) {
                                 search_layout.visibility = View.GONE
                                 if(isMyClubs) getMyClubs(serch_obj.club_name)
                                 else searchClubs(serch_obj.club_name ,true)
                             }
                         }
                        recycler_potential_search.adapter = adapter
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
                params.put("searchText", "")
                params.put("offset", "0")
                params.put("limit","100")
                params.put("clubType", "")
                params.put("latitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString() )+"")
                params.put("longitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString() )+"")
                return  params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return  params
            }
        }.execute()
    }


    fun setRecycleView(searchList : ArrayList<Clubs>){
        //adapter?.setList(searchList)

        if(list_recycler.adapter == null){
            list_recycler.adapter = Club_List_Adapter(searchList, context, activity as HomeActivity)
        }else{
            (list_recycler.adapter as Club_List_Adapter).list = searchList
            list_recycler.adapter.notifyDataSetChanged()
        }
    }


    fun getMyClubs(text : String = "", offset :String = "0"){  /*${WebService.club_my_clubs} ?limit=$lati&offset=$longi" */
        val activity  = activity as HomeActivity
        val dialog = CusDialogProg(activity )
        dialog.show()
        object  : VolleyGetPost(activity , activity, WebService.club_my_clubs, false){

            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        val searchlist : ArrayList<Clubs> = Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list)
                        setRecycleView(searchlist)
                    }else{
                        clubList.clear()
                        setRecycleView(clubList)
                    }

                }catch (ex: Exception){
                    ex.printStackTrace()
                }

            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("searchText",text)
                params.put("offset",offset)
                params.put("limit","200")
                params.put("clubType",ClubZ.isPrivate.toString())
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return  params
            }
        }.execute()
    }

}