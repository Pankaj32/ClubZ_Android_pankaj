package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.R
import com.clubz.data.local.db.repo.AllClubRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.AllClub
import com.clubz.helper.Type_Token
import com.clubz.data.remote.WebService
import com.clubz.data.model.Clubs
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


class FragMyClubs : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MyClub {

    private var adapter: MyClubListAdapter? = null
    private var clubList: ArrayList<Clubs> = arrayListOf()
    private var listner: MyClubInteraction? = null
    private var pageListner: RecyclerViewScrollListener? = null

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
        swipeRefreshLayout.setOnRefreshListener(this)
        adapter = MyClubListAdapter(clubList, context!!, this)
        list_recycler.adapter = adapter

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list_recycler.itemAnimator = null
        list_recycler.layoutManager = lm
        list_recycler.setHasFixedSize(true)

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getMyClubs("", page * 10, false)
            }
        }

        list_recycler.addOnScrollListener(pageListner)
        clubList.clear()
        val tempClubList = AllClubRepo().getAllClubs()
        if (tempClubList.size > 0) {
            for (clubs in tempClubList) {
                val club = Clubs()
                club.clubId = clubs.clubId.toString()
                club.club_name = clubs.club_name!!
                club.club_description = clubs.club_description
                club.club_icon = clubs.club_icon
                club.club_image = clubs.club_image
                club.club_foundation_date = clubs.club_foundation_date
                club.club_email = clubs.club_email
                club.club_website = clubs.club_website
                club.club_contact_no = clubs.club_contact_no
                club.club_country_code = clubs.club_country_code
                club.club_city = clubs.club_city
                club.club_location = clubs.club_location
                club.club_address = clubs.club_address
                club.club_latitude = clubs.club_latitude
                club.club_longitude = clubs.club_longitude
                club.club_category_id = clubs.club_category_id
                club.terms_conditions = clubs.terms_conditions
                club.club_category_name = clubs.club_category_name
                club.user_id = clubs.user_id
                club.full_name = clubs.full_name
                club.user_image = clubs.user_image
                club.club_user_status = clubs.club_user_status
                club.user_role = clubs.user_role
                club.contact_no = clubs.contact_no
                club.contact_no_visibility = clubs.contact_no_visibility
                club.profile_image = clubs.profile_image
                club.clubUserId = clubs.clubUserId
                club.is_allow_feeds = clubs.is_allow_feeds
                club.club_type = clubs.club_type
                club.comment_count = clubs.comment_count
                club.status = clubs.status
                club.crd = clubs.crd
                club.upd = clubs.upd
                club.distance = clubs.distance
                club.members = clubs.members
                clubList.add(club)
            }
            adapter?.notifyDataSetChanged()
        }
        getMyClubs(ispull = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        clubList.clear()
        adapter = null
        listner = null
        pageListner = null
    }

    fun refreshList() {
        clubList.clear()
        pageListner?.resetState()
        getMyClubs()
    }

    override fun onJoinedClub(club: Clubs) {
        listner?.onJoinClub(club)
    }

    override fun onLeavedClub(club: Clubs) {
        listner?.onLeaveClub(club)
    }

    override fun onRefresh() {
        getMyClubs(ispull = true)
        pageListner?.resetState()
        swipeRefreshLayout.isRefreshing = false
    }

    fun updateAdapter(club: Clubs) {
        clubList.add(club)
        if (clubList.size > 0) {
            noFeedMsgUI.visibility = View.GONE
            swipeRefreshLayout.visibility = View.VISIBLE
        } else {
            noFeedMsgUI.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.GONE
        }
        adapter?.notifyDataSetChanged()
    }

    fun getMyClubs(text: String = "", offset: Int = 0, showProgress: Boolean = true,
                   ispull: Boolean = false) {  /*${WebService.club_my_clubs} ?limit=$lati&offset=$longi" */
        val dialog = CusDialogProg(activity)
        if (showProgress) dialog.show()

        object : VolleyGetPost(activity, activity, WebService.club_my_clubs, false,
                false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        if (ispull) clubList.clear()
                        clubList.addAll(Gson().fromJson<ArrayList<Clubs>>(obj.getString("data"), Type_Token.club_list))
                        updateClubInDb()
                        adapter?.notifyDataSetChanged()
                    }
                    if (clubList.size > 0) {
                        noFeedMsgUI.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.VISIBLE
                    } else {
                        noFeedMsgUI.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.GONE
                    }
                } catch (ex: Exception) {
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
                params["searchText"] = text
                params["offset"] = offset.toString()
                params["limit"] = "10"
                params["clubType"] = ClubZ.isPrivate.toString()
                Log.e("MyClubListParam",params.toString())
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }


    override fun onSilenceClub(club: Clubs, position: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.club_silence, false,
                false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {

                    }
                } catch (ex: Exception) {
                }
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

    private fun updateClubInDb() {
        AllClubRepo().deleteTable()
        for (club in clubList) {
            // if (!club.clubId.equals("1")) {
            val allClub = AllClub()
            allClub.clubId = club.clubId.toInt()
            allClub.club_name = club.club_name
            if (club.user_id.equals(ClubZ.currentUser?.id)) {
                allClub.notSilent = "1"
            } else {
                allClub.notSilent = club.is_allow_feeds
            }
            allClub.club_description = club.club_description
            allClub.club_icon = club.club_icon
            allClub.club_image = club.club_image
            allClub.club_foundation_date = club.club_foundation_date
            allClub.club_email = club.club_email
            allClub.club_website = club.club_website
            allClub.club_contact_no = club.club_contact_no
            allClub.club_country_code = club.club_country_code
            allClub.club_city = club.club_city
            allClub.club_location = club.club_location
            allClub.club_address = club.club_address
            allClub.club_latitude = club.club_latitude
            allClub.club_longitude = club.club_location
            allClub.club_category_id = club.club_category_id
            allClub.terms_conditions = club.terms_conditions
            allClub.club_category_name = club.club_category_name
            allClub.user_id = club.user_id
            allClub.full_name = club.full_name
            allClub.user_image = club.user_image
            allClub.club_user_status = club.club_user_status
            allClub.user_role = club.user_role
            allClub.contact_no = club.contact_no
            allClub.contact_no_visibility = club.contact_no_visibility
            allClub.profile_image = club.profile_image
            allClub.clubUserId = club.clubUserId
            allClub.is_allow_feeds = club.is_allow_feeds
            allClub.club_type = club.club_type
            allClub.comment_count = club.comment_count
            allClub.status = club.status
            allClub.crd = club.crd
            allClub.upd = club.upd
            allClub.distance = club.distance
            allClub.members = club.members

            AllClubRepo().insert(allClub)
            // }
        }
    }

}