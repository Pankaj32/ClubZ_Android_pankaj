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
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Clubs
import com.clubz.ui.club.`interface`.MyClubInteraction
import com.clubz.ui.club.adapter.MyClub
import com.clubz.ui.club.adapter.MyClub_List_Adapter
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_clubs.*
import org.json.JSONObject
import java.util.ArrayList


class FragMyClubs : Fragment() , View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, MyClub {

    var adapter : MyClub_List_Adapter? = null
    var clubList : ArrayList<Clubs> = arrayListOf()
    var listner : MyClubInteraction? = null
    var pageListner : RecyclerViewScrollListener? = null

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
        adapter = MyClub_List_Adapter(clubList, context, this, true)
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
        clubList?.add(club)
        adapter?.notifyDataSetChanged()
    }

    fun getMyClubs(text : String = "", offset :String = "0"){  /*${WebService.club_my_clubs} ?limit=$lati&offset=$longi" */
        val dialog = CusDialogProg(activity )
        dialog.show()
        object  : VolleyGetPost(activity , activity, WebService.club_my_clubs, false){

            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        //val searchlist : ArrayList<Clubs> = Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list)
                        clubList.addAll(Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list))
                    }else clubList.clear()
                    adapter?.notifyDataSetChanged()
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
                params.put("clubType", HomeActivity.isPrivate.toString())
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return  params
            }
        }.execute()
    }
}