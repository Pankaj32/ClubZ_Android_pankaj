package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
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
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import kotlinx.android.synthetic.main.no_contant_layout.*
import org.json.JSONObject
import java.util.ArrayList


class FragMyClubs : Fragment() , View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, MyClub {

    private var adapter  : MyClubListAdapter? = null
    private var clubList : ArrayList<Clubs> = arrayListOf()
    private var listner  : MyClubInteraction? = null
    private var pageListner : RecyclerViewScrollListener? = null

    override fun onClick(v: View?) {

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MyClubInteraction){
            listner = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listner = null
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_my_clubs, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener(this)
        adapter = MyClubListAdapter(clubList, context, this)
        list_recycler.adapter = adapter

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        list_recycler.itemAnimator = null
       // val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
       // list_recycler.addItemDecoration(decor)

        list_recycler.layoutManager = lm
        list_recycler.setHasFixedSize(true)
        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) { }
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getMyClubs("",page*10,false)
            }
        }
        list_recycler.addOnScrollListener(pageListner)
        clubList.clear()
        getMyClubs()
    }


    fun refreshList(){
        pageListner?.resetState()
        clubList.clear()
        getMyClubs()
    }

    override fun onJoinedClub(club: Clubs) {
        listner?.onJoinClub(club)
    }

    override fun onLeavedClub(club: Clubs) {
        listner?.onLeaveClub(club)
    }

    override fun onRefresh() {
        clubList.clear()
        getMyClubs()
        swipeRefreshLayout.isRefreshing = false
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

    fun getMyClubs(text : String = "", offset :Int = 0, showProgress : Boolean = true){  /*${WebService.club_my_clubs} ?limit=$lati&offset=$longi" */
        val dialog = CusDialogProg(activity )
        if(showProgress)dialog.show()

        object  : VolleyGetPost(activity , activity, WebService.club_my_clubs, false){
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
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
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss() }

            override fun onNetError() { dialog.dismiss() }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["searchText"] = text
                params["offset"] = offset.toString()
                params["limit"]= "10"
                params["clubType"] = ClubsActivity.isPrivate.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return  params
            }
        }.execute()
    }


    override fun onSilenceClub(club: Clubs, position : Int) {
        val dialog = CusDialogProg(context)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.club_silence, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {

                    }
                } catch (ex: Exception) { }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubUserId"] = club.clubUserId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

}