package com.clubz.ui.club

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.View
import android.view.Window
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Club_Potential_search
import com.clubz.data.model.Clubs
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.club.`interface`.MyClubInteraction
import com.clubz.ui.club.`interface`.SearchListner
import com.clubz.ui.club.adapter.SearchClubName_Adapter
import com.clubz.ui.club.fragment.FragMyClubs
import com.clubz.ui.club.fragment.FragNearClubs
import com.clubz.ui.club.fragment.FragSearchClubs
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.cv.SimpleDividerItemDecoration
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.VolleyGetPost
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_clubs.*
import kotlinx.android.synthetic.main.club_more_menu.*
import kotlinx.android.synthetic.main.menu_club_selection.*
import org.json.JSONObject
import java.util.ArrayList

class ClubsActivity : AppCompatActivity(), View.OnClickListener, MyClubInteraction,
        ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var searchListner: SearchListner? = null
    private var searchAdapter: SearchClubName_Adapter? = null
    private var searchList: ArrayList<Club_Potential_search> = arrayListOf()
    private var pageListner: RecyclerViewScrollListener? = null
    private var dialog: Dialog? = null
    private var menuDialog: Dialog? = null

    /*companion object {
        var isPrivate: Int = 0
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)
        headerTxt.text = resources.getString(R.string.t_manage_your_clubs)
        ivBack.setOnClickListener(this)
        // addsymbol.setOnClickListener(this)
        bubble_menu.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)

        /*floating_search_view.setOnQueryChangeListener { oldQuery, newQuery ->
            searchList.clear()
            searchClubsName(newQuery)
            searchAdapter?.setCurrentText(newQuery)
        }*/


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchListner!!.onTextChange(query!!)
                //searchAdapter?.filter?.filter(query)
                searchList.clear()
                searchClubsName(query)
                searchAdapter?.setCurrentText(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchListner?.onTextChange(newText!!)
                //searchAdapter?.filter?.filter(newText)
                searchList.clear()
                searchClubsName(newText!!)
                searchAdapter?.setCurrentText(newText)
                return false
            }
        })

        val closeListner = SearchView.OnCloseListener {
            search_layout.visibility = View.GONE
            false
        }
        searchView.setOnCloseListener(closeListner)

        searchAdapter = object : SearchClubName_Adapter(this@ClubsActivity, searchList) {
            override fun onItemClick(serch_obj: Club_Potential_search) {
                searchListner?.onTextChange(serch_obj.club_name)
                search_layout.visibility = View.GONE
                recycleView.visibility = View.GONE
                onClubSelected(serch_obj.club_name)
            }
        }
        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleView.itemAnimator = null
        recycleView.layoutManager = lm
        recycleView.setHasFixedSize(true)
        recycleView.addItemDecoration(SimpleDividerItemDecoration(this))
        recycleView.adapter = searchAdapter

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {

            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragMyClubs(), resources.getString(R.string.t_my_clubs), " This is First")
        adapter.addFragment(FragNearClubs(), resources.getString(R.string.t_near_clubs), " This is second")
        adapter.addFragment(FragSearchClubs(), resources.getString(R.string.search), " This is third")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
    }


    fun onClubSelected(clubName: String) {
        val frag = adapter.getItem(2) as FragSearchClubs
        frag.onTextChange(clubName)
    }

    override fun onJoinClub(club: Clubs) {
        val frag = adapter.getItem(0) as FragMyClubs
        frag.updateAdapter(club)
        onUpdateFirebase(club,1)
    }


    override fun onLeaveClub(club: Clubs) {
        val frag = adapter.getItem(1) as FragNearClubs
        frag.updateAdapter(club)
        onUpdateFirebase(club, 0)
    }

    override fun onUpdateFirebase(club: Clubs, status:Int) {
        val memberBean = MemberBean()
        memberBean.clubId = club.clubId
        memberBean.userId = ClubZ.currentUser?.id
        memberBean.joind = status
        memberBean.silent = "1"



        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(club.clubId)
                .child(memberBean.userId)
                .setValue(memberBean).addOnCompleteListener {
                }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.bubble_menu -> {
                showMenu()
            }
            R.id.ll_menu1 -> {
                menuDialog?.dismiss()
                startActivityForResult(Intent(this@ClubsActivity, ClubCreationActivity::class.java), 1001)
            }

            R.id.ll_menu2 -> {
                menuDialog?.dismiss()
                showFilterDialog(0)
            }

            R.id.tv_private -> {
                when (ClubZ.isPrivate) {
                    1 -> {
                        ClubZ.isPrivate = 0; dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = true
                        refreshNow()
                    }
                    0, 2 -> {
                        ClubZ.isPrivate = 1; dialog!!.chk_priavte.isChecked = false
                        dialog!!.chk_public.isChecked = true
                        refreshNow()
                    }
                }
            }

            R.id.tv_public -> {
                when (ClubZ.isPrivate) {
                    2 -> {
                        ClubZ.isPrivate = 0; dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = true
                        refreshNow()
                    }
                    0, 1 -> {
                        ClubZ.isPrivate = 2; dialog!!.chk_priavte.isChecked = true
                        dialog!!.chk_public.isChecked = false
                        refreshNow()
                    }
                }
            }
        }
    }


    private fun refreshNow() {
        val frag = adapter.getItem(viewPager.currentItem)
        if (frag is FragMyClubs) {
            frag.refreshList()
        } else (frag as? FragNearClubs)?.refreshList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val frag = adapter.getItem(0) as FragMyClubs
            frag.refreshList()
        }
    }


    override fun onBackPressed() {
        if (searchView.visibility == View.VISIBLE) {
            tablayout.getTabAt(0)?.select()
            searchList.clear()
            searchAdapter?.notifyDataSetChanged()
            search_layout.visibility = View.GONE

        } else super.onBackPressed()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        when (position) {
            0 -> {
                searchView.clearFocus()
                searchView.visibility = View.GONE
                search_layout.visibility = View.GONE
                //floating_search_view.visibility = View.GONE
                //addsymbol.visibility = View.VISIBLE
                bubble_menu.visibility = View.VISIBLE
                headerTxt.visibility = View.VISIBLE
                headerTxt.text = resources.getString(R.string.t_manage_your_clubs)
            }
            1 -> {
                searchView.clearFocus()
                searchView.visibility = View.GONE
                search_layout.visibility = View.GONE
                //floating_search_view.visibility = View.GONE
                //addsymbol.visibility = View.VISIBLE
                bubble_menu.visibility = View.VISIBLE
                headerTxt.visibility = View.VISIBLE
                headerTxt.text = resources.getString(R.string.t_join_the_force)
            }
            2 -> {
                //addsymbol.visibility = View.GONE
                bubble_menu.visibility = View.GONE
                headerTxt.visibility = View.GONE
                //
                search_layout.visibility = View.VISIBLE
                recycleView.visibility = View.VISIBLE
                //floating_search_view.visibility = View.VISIBLE
                searchList.clear()
                searchAdapter?.notifyDataSetChanged()
                searchView.visibility = View.VISIBLE
                searchView.queryHint = ""
                searchView.setIconifiedByDefault(true)
                searchView.isFocusable = true
                searchView.isIconified = false
                searchView.requestFocusFromTouch()
                headerTxt.text = resources.getString(R.string.search)
            }
        }
    }


    @SuppressLint("RtlHardcoded")
    private fun showFilterDialog(position: Int) {

        if (dialog == null) {
            dialog = Dialog(this)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = dialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog?.setContentView(R.layout.menu_club_selection)
            for (views in arrayOf(dialog?.tv_private, dialog?.tv_public)) views?.setOnClickListener(this)

            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.CENTER)
            lp?.y = -100
            dialogWindow?.attributes = lp
            dialog?.setCancelable(true)
        }

        if (position == 0) {
            if (ClubZ.isPrivate == 0) {
                dialog?.chk_priavte?.isChecked = true; dialog?.chk_public?.isChecked = true
            } else {
                dialog?.chk_priavte?.isChecked = (ClubZ.isPrivate == 2)
                dialog?.chk_public?.isChecked = (ClubZ.isPrivate == 1)
            }
        }

        dialog?.show()
    }

    @SuppressLint("RtlHardcoded")
    private fun showMenu() {
        if (menuDialog == null) {
            menuDialog = Dialog(this)
            menuDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = menuDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            menuDialog?.setContentView(R.layout.club_more_menu)
            for (views in arrayOf(menuDialog?.ll_menu1, menuDialog?.ll_menu2)) views?.setOnClickListener(this)

            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            menuDialog?.setCancelable(true)
        }
        menuDialog?.show()
    }


    fun searchClubsName(searchTxt: String = "", offset: Int = 0) {
        search_layout.visibility = View.VISIBLE
        recycleView.visibility = View.VISIBLE
        ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@ClubsActivity, this@ClubsActivity,
                WebService.nearclub_names,
                false) {
            override fun onVolleyResponse(response: String?) {
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        searchList.addAll(Gson().fromJson<ArrayList<Club_Potential_search>>(obj.getString("data"),
                                Type_Token.potential_list))
                    }
                    //floating_search_view.swapSuggestions(searchList)
                    searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
            }

            override fun onNetError() {
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["searchText"] = searchTxt
                params["offset"] = offset.toString()
                params["limit"] = "10"
                params["clubType"] = ""
                params["latitude"] = if (ClubZ.latitude == 0.0 && ClubZ.longitude == 0.0) "" else ClubZ.latitude.toString()
                params["longitude"] = if (ClubZ.latitude == 0.0 && ClubZ.longitude == 0.0) "" else ClubZ.longitude.toString()
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(ClubsActivity::class.java.name)
    }

}
