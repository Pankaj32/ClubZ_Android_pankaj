package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.content.Context
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
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Clubs
import com.clubz.ui.club.ClubsActivity
import com.clubz.ui.club.`interface`.MyClubInteraction
import com.clubz.ui.club.adapter.MyClub
import com.clubz.ui.club.adapter.MyClubListAdapter
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import kotlinx.android.synthetic.main.no_contant_layout.*
import org.json.JSONObject
import java.util.ArrayList

class FragNearClubs : Fragment() , View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MyClub {
    override fun onSilenceClub(club: Clubs, position: Int) {
    }

    var adapter  : MyClubListAdapter? = null
    var clubList : ArrayList<Clubs> = arrayListOf()
    var listner  : MyClubInteraction? = null
    var pageListner : RecyclerViewScrollListener? = null

    override fun onClick(v: View?) {
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MyClubInteraction) listner = context
    }

    override fun onDetach() {
        super.onDetach()
        listner = null
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_my_clubs, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MyClubListAdapter(clubList, context!!, this)
        list_recycler.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener(this)
      //  list_recycler.getRecycledViewPool().clear();
        clubList.clear()

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        list_recycler.itemAnimator = null
        list_recycler.layoutManager = lm
        //list_recycler.setHasFixedSize(true)
        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) { }
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getNearByClubs("",false, page*10)
            }
        }
        list_recycler.addOnScrollListener(pageListner)
        getNearByClubs()
    }


    override fun onJoinedClub(club: Clubs) {
        listner?.onJoinClub(club)
    }

    override fun onLeavedClub(club: Clubs) {
        listner?.onJoinClub(club)
    }


    override fun onRefresh() {
       // list_recycler.getRecycledViewPool().clear();
        clubList.clear()
        pageListner?.resetState()
        getNearByClubs()
        swipeRefreshLayout.isRefreshing = false
    }

    fun refreshList(){
        //list_recycler.getRecycledViewPool().clear();
        clubList.clear()
        pageListner?.resetState()
        getNearByClubs()
    }

    fun updateAdapter(club : Clubs){
        clubList.add(club)
        if(clubList.size>0){
            noFeedMsgUI.visibility = View.GONE
            swipeRefreshLayout.visibility = View.VISIBLE
        }else {
            noFeedMsgUI.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.GONE
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     *@clubtype 1 means public , 2 means private , 0 means all
     */
    private fun getNearByClubs(text : String = "", showProgres : Boolean = false, offset : Int = 0){
        val dialog = CusDialogProg(activity )
        if(text.isBlank() || showProgres)dialog.show()
        object  : VolleyGetPost(activity , activity , WebService.club_search,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                //{"status":"success","message":"found","data":[{"clubId":"20","user_id":"52","club_name":"Mindiii","club_description":"this is a mindiii company","club_image":"http:\/\/clubz.co\/dev\/uploads\/club_image\/32e494d9cb36f6a0d73d792bebee8e6e.jpg","club_foundation_date":"2018-03-15","club_email":"pankaj.mindiii@gmail.com","club_contact_no":"9630612281","club_country_code":"+91","club_website":"www.google.com","club_location":"Indore Jn.","club_address":"140 square","club_latitude":"22.7170909","club_longitude":"75.8684423","club_type":"1","club_category_id":"2","terms_conditions":"indore company","comment_count":"0","status":"1","crd":"2018-03-16 11:32:09","upd":"2018-03-16 11:32:09","club_category_name":"Sports","full_name":"Pankaj","club_user_status":"","distance":""}]}
                try{
                     val obj = JSONObject(response)
                    if(obj.getString("status")=="success"){
                        //val list  = Gson().fromJson<ArrayList<Clubs>>(obj.getJSONArray("data").toString() , Type_Token.club_list)
                        clubList.addAll(Gson().fromJson<ArrayList<Clubs>>(obj.getJSONArray("data").toString() , Type_Token.club_list))
                    }

                    if(clubList.size>0){
                        noFeedMsgUI.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.VISIBLE
                    }else {
                        noFeedMsgUI.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.GONE
                    }
                    adapter?.notifyDataSetChanged()

                }catch (ex: Exception){
                    Util.showToast(R.string.swr,context!!)
                }
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss() }

            override fun onNetError() { dialog.dismiss() }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["city"] = ClubZ.city
                params["latitude"] = (if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString() )+""
                params["longitude"] = (if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString() )+""
                params["clubCategoryId"] = ""
                params["searchText"] = text
                params["offset"] = offset.toString()
                params["limit"] = "10"
                params["clubType"] = ClubZ.isPrivate.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["language"] = SessionManager.getObj().language
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute()
    }
}