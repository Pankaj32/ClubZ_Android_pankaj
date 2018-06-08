package com.clubz.ui.club

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.View
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_clubs.*
import org.json.JSONObject
import java.util.ArrayList

class ClubsActivity : AppCompatActivity(), View.OnClickListener, MyClubInteraction,
        ViewPager.OnPageChangeListener {

    lateinit var adapter : ViewPagerAdapter
    var searchListner: SearchListner? = null
    var searchAdapter : SearchClubName_Adapter? = null
    var searchList : ArrayList<Club_Potential_search> = arrayListOf()
    var pageListner : RecyclerViewScrollListener? = null
    var isMyClub = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)
        headerTxt.text = resources.getString(R.string.t_manage_your_clubs)
        ivBack.setOnClickListener(this)
        addsymbol.setOnClickListener(this)
        setViewPager(viewPager)
        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)

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

        val closeListner = object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                search_layout.visibility = View.GONE
                return false
            }

        }
        searchView.setOnCloseListener(closeListner)

        searchAdapter = object : SearchClubName_Adapter(this@ClubsActivity, searchList){
            override fun onItemClick(serch_obj: Club_Potential_search) {
                searchListner?.onTextChange(serch_obj.club_name)
                search_layout.visibility = View.GONE
                recycleView.visibility = View.GONE
                onClubSelected(serch_obj.club_name)
            }
        }

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recycleView.setItemAnimator(null)
        recycleView.setLayoutManager(lm)
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

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment( FragMyClubs(),resources.getString(R.string.t_my_clubs) , " This is First")
        adapter.addFragment( FragNearClubs(),resources.getString(R.string.t_near_clubs) , " This is second")
        adapter.addFragment( FragSearchClubs(),resources.getString(R.string.search) , " This is third")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
    }


    fun onClubSelected(clubName : String){
        val frag = adapter.getItem(2) as FragSearchClubs
        frag.onTextChange(clubName)
    }

    override fun onJoinClub(club: Clubs) {
        val frag = adapter.getItem(0) as FragMyClubs
        frag.updateAdapter(club)
    }

    override fun onLeaveClub(club: Clubs) {
        val frag = adapter.getItem(1) as FragNearClubs
        frag.updateAdapter(club)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBack ->{ onBackPressed() }
            R.id.addsymbol ->{startActivity(Intent(this@ClubsActivity, ClubCreationActivity::class.java))}
        }
    }


    override fun onBackPressed() {
        if(searchView.visibility==View.VISIBLE){
            tablayout.getTabAt(0)?.select()
            searchList.clear()
            searchAdapter?.notifyDataSetChanged()
            search_layout.visibility = View.GONE

        }else super.onBackPressed()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        if(position==0){
            searchView.clearFocus()
            searchView.visibility = View.GONE
            search_layout.visibility = View.GONE
            addsymbol.visibility = View.VISIBLE
            headerTxt.text = resources.getString(R.string.t_manage_your_clubs)
        }else if (position ==1){
            searchView.clearFocus()
            searchView.visibility = View.GONE
            search_layout.visibility = View.GONE
            addsymbol.visibility = View.VISIBLE
            headerTxt.text = resources.getString(R.string.t_join_the_force)
        }else if(position ==2){
            addsymbol.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            search_layout.visibility = View.VISIBLE
            recycleView.visibility = View.VISIBLE
            searchList.clear()
            searchAdapter?.notifyDataSetChanged()
            searchView.queryHint = ""
            searchView.setIconifiedByDefault(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
            headerTxt.text = resources.getString(R.string.search)
        }
    }



    fun searchClubsName(searchTxt: String = "", offset:Int=0){
        /*val lati= if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString()
        val longi=if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString()*/

       // "${WebService.club_search}?latitude=$lati&longitude=$longi&isMyClub=$isMyClub" + "&city=${ClubZ.city}"
        search_layout.visibility = View.VISIBLE
        recycleView.visibility = View.VISIBLE
        ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object  : VolleyGetPost(this@ClubsActivity , this@ClubsActivity,
                    WebService.nearclub_names,
                    false){
            override fun onVolleyResponse(response: String?) {
                try {

                    val obj = JSONObject(response)
                    if(obj.getString("status").equals("success")){
                        searchList.addAll(Gson().fromJson<ArrayList<Club_Potential_search>>(obj.getString("data"),
                                Type_Token.potential_list))
                    }
                    searchAdapter?.notifyDataSetChanged()
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
            }

            override fun onNetError() {
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("searchText", searchTxt)
                params.put("offset", offset.toString())
                params.put("limit","10")
                params.put("clubType", "")
                params.put("latitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString() )+"")
                params.put("longitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString() )+"")
                return  params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return  params
            }
        }.execute(ClubsActivity::class.java.name)
    }

}
