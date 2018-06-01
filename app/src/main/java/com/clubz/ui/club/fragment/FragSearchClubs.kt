package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Clubs
import com.clubz.ui.club.`interface`.SearchListner
import com.clubz.ui.club.adapter.MyClub
import com.clubz.ui.club.adapter.MyClub_List_Adapter
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import org.json.JSONObject
import java.util.ArrayList


class FragSearchClubs : Fragment() , View.OnClickListener, SearchListner,
        SwipeRefreshLayout.OnRefreshListener, MyClub {

    var adapter : MyClub_List_Adapter? = null
    var clubList : ArrayList<Clubs> = arrayListOf()
    var lastQuery : String? = ""
    var pageListner : RecyclerViewScrollListener? = null


    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_my_clubs, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener(this)
        adapter = MyClub_List_Adapter(clubList, context, this)
        list_recycler.adapter = adapter

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        list_recycler.setItemAnimator(null)
        list_recycler.setLayoutManager(lm)
        list_recycler.setHasFixedSize(true)

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {

            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

            }
        }
        list_recycler.addOnScrollListener(pageListner)

        clubList.clear()
    }


    override fun onJoinedClub(club: Clubs) {
    }

    override fun onLeavedClub(club: Clubs) {
    }

    override fun onClick(v: View?) {

    }


    override fun onRefresh() {
        clubList.clear()
        searchClubs(lastQuery!!, true)
        swipeRefreshLayout.isRefreshing = false
    }

    fun updateAdapter(club : Clubs){
        clubList?.add(club)
        adapter?.notifyDataSetChanged()
    }

    override fun onTextChange(text: String) {
        lastQuery = text;
        clubList.clear()
        searchClubs(lastQuery!!, true)
    }


    fun searchClubs(text : String = "",showProgres : Boolean = false, offset :String = "0"  ){

        val dialog = CusDialogProg(activity )
        if(text.isBlank() || showProgres)dialog.show()
        object  : VolleyGetPost(activity , activity , WebService.club_search_clubs,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                //{"status":"success","message":"found","data":[{"clubId":"20","user_id":"52","club_name":"Mindiii","club_description":"this is a mindiii company","club_image":"http:\/\/clubz.co\/dev\/uploads\/club_image\/32e494d9cb36f6a0d73d792bebee8e6e.jpg","club_foundation_date":"2018-03-15","club_email":"pankaj.mindiii@gmail.com","club_contact_no":"9630612281","club_country_code":"+91","club_website":"www.google.com","club_location":"Indore Jn.","club_address":"140 square","club_latitude":"22.7170909","club_longitude":"75.8684423","club_type":"1","club_category_id":"2","terms_conditions":"indore company","comment_count":"0","status":"1","crd":"2018-03-16 11:32:09","upd":"2018-03-16 11:32:09","club_category_name":"Sports","full_name":"Pankaj","club_user_status":"","distance":""}]}
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        //val searchlist : ArrayList<Clubs> = Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list)
                        clubList.addAll(Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list))
                    }else clubList.clear()
                    adapter?.notifyDataSetChanged()
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
                params.put("clubType",HomeActivity.isPrivate.toString())
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

}