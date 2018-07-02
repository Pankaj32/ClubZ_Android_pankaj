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
import com.clubz.ui.club.adapter.MyClubListAdapter
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import kotlinx.android.synthetic.main.no_contant_layout.*
import org.json.JSONObject
import java.util.ArrayList


class FragSearchClubs : Fragment() , SearchListner, SwipeRefreshLayout.OnRefreshListener, MyClub {
    override fun onSilenceClub(club: Clubs, position: Int) {
    }

    var adapter : MyClubListAdapter? = null
    var clubList : ArrayList<Clubs> = arrayListOf()
    var lastQuery : String? = ""
    var pageListner : RecyclerViewScrollListener? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_my_clubs, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noFeedMsgUI.visibility = View.GONE
        swipeRefreshLayout.setOnRefreshListener(this)
        adapter = MyClubListAdapter(clubList, context, this)
        list_recycler.adapter = adapter

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        list_recycler.itemAnimator = null
        list_recycler.layoutManager = lm
        list_recycler.setHasFixedSize(true)

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) { }
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                searchClubs(lastQuery!!,false, page*10)
            }
        }
        list_recycler.addOnScrollListener(pageListner)
        clubList.clear()
    }


    override fun onJoinedClub(club: Clubs) {
    }

    override fun onLeavedClub(club: Clubs) {
    }

    override fun onRefresh() {
        clubList.clear()
        pageListner?.resetState()
        searchClubs(lastQuery!!, true)
        swipeRefreshLayout.isRefreshing = false
    }


    override fun onTextChange(text: String) {
        lastQuery = text
        clubList.clear()
        pageListner?.resetState()
        searchClubs(lastQuery!!, true)
    }


    fun searchClubs(text : String = "",showProgres : Boolean = false, offset :Int = 0){

        val dialog = CusDialogProg(activity )
        if(text.isBlank() || showProgres)dialog.show()
        object  : VolleyGetPost(activity , activity , WebService.club_search_clubs,false){
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try{
                    val obj = JSONObject(response)
                    if(obj.getString("status") == "success"){
                        clubList.addAll(Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list))
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
                    Util.showToast(R.string.swr,context)
                }
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss() }

            override fun onNetError() { dialog.dismiss() }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["city"] = ClubZ.city
                params["latitude"] = if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString()
                params["longitude"] = if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString()
                params["clubCategoryId"] =""
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