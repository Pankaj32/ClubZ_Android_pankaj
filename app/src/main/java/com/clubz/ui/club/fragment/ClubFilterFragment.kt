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
import com.clubz.ui.club.adapter.ClubFilterAdapter
import com.clubz.ui.club.adapter.MyClub
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import kotlinx.android.synthetic.main.no_contant_layout.*
import org.json.JSONObject
import java.util.ArrayList


class ClubFilterFragment : Fragment() , SwipeRefreshLayout.OnRefreshListener, MyClub {

    private var adapter  : ClubFilterAdapter? = null
    private var clubList : ArrayList<Clubs> = arrayListOf()
    private var mListener: Listener? = null
    private var pageListner : RecyclerViewScrollListener? = null

    interface Listener {
        fun onRightNavigationItemChange()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) parent as Listener else context as Listener
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_my_clubs, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener(this)
        adapter = ClubFilterAdapter(clubList, context!!, this)
        list_recycler.adapter = adapter

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        list_recycler.itemAnimator = null
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

    override fun onDestroy() {
        super.onDestroy()
        clubList.clear()
        adapter = null
        pageListner = null
    }

    fun refreshList(){
        pageListner?.resetState()
        clubList.clear()
        getMyClubs()
    }

    override fun onRefresh() {
        clubList.clear()
        getMyClubs("", 0, true)
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

    override fun onJoinedClub(club: Clubs) {
       // listner?.onJoinClub(club)
    }

    override fun onLeavedClub(club: Clubs) {
       // listner?.onLeaveClub(club)
    }

    override fun onSilenceClub(club: Clubs, position : Int) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.club_silence, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        mListener?.onRightNavigationItemChange()
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


    private fun getMyClubs(text : String = "", offset :Int = 0, showProgress : Boolean = false){  /*${WebService.club_my_clubs} ?limit=$lati&offset=$longi" */
        val dialog = CusDialogProg(activity )
        if(showProgress)dialog.show()

        object : VolleyGetPost(activity , activity, WebService.club_my_clubs, false){
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
                params["clubType"] = ClubZ.isPrivate.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
//                params["authToken"] = SessionManager.getObj().user.auth_token
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return  params
            }
        }.execute()
    }

}