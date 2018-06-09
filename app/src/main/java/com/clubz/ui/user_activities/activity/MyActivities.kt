package com.clubz.ui.user_activities.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.expandable_recycler_view.ExpandableRecyclerAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.MyActivitiesCategoryAdapter
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_activities.*
import org.json.JSONObject
import android.view.MenuItem
import android.widget.ImageView
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper




class MyActivities : AppCompatActivity(), ParentViewClickListioner, ChildViewClickListioner, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private var todayAdapter: MyActivitiesCategoryAdapter? = null
    private var isTodayOpen: Boolean = false
    private var isTomorrowOpen: Boolean = false
    private var isSoonOpen: Boolean = false
    private val INITIAL_POSITION = 0.0f
    private val ROTATED_POSITION = 180f
    private var todayList: List<GetMyactivitiesResponce.DataBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_activities)
        recyclerViewMyActivities.layoutManager = LinearLayoutManager(this@MyActivities)
        back_f.setOnClickListener(this@MyActivities)
        getActivitiesList()
    }

    fun getActivitiesList(listType: String = "", limit: String = "", offset: String = "") {
        val dialog = CusDialogProg(this@MyActivities)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@MyActivities, this@MyActivities,
                "${WebService.get_my_activity_list}?listType=${listType}&offset=${offset}&limit=${limit}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        var myActivitiesResponce: GetMyactivitiesResponce = Gson().fromJson(response, GetMyactivitiesResponce::class.java)
                        updateUi(myActivitiesResponce)
                    }
                    // searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /* params.put("searchText", searchTxt)
                 params.put("offset", offset.toString())
                 params.put("limit","10")
                 params.put("clubType", "")
                 params.put("latitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.latitude.toString() )+"")
                 params.put("longitude",(if(ClubZ.latitude==0.0 && ClubZ.longitude==0.0)"" else ClubZ.longitude.toString() )+"")*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(MyActivities::class.java.name)
    }


    private fun updateUi(myActivitiesResponce: GetMyactivitiesResponce) {
        for (i in 0..myActivitiesResponce.getData()!!.size - 1) {
            var todayData = myActivitiesResponce.getData()!!.get(i)
            for (j in 0..todayData.events!!.size - 1) {
                var eventData = todayData.events!!.get(j)
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm.equals("1")) todayData.is_Confirm = true
            }
        }
        todayList = myActivitiesResponce.getData()
        todayAdapter = MyActivitiesCategoryAdapter(this@MyActivities, todayList, this@MyActivities, this@MyActivities)
        todayAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = todayList!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = todayList!![position]
            }

        })
        recyclerViewMyActivities.setAdapter(todayAdapter)
    }

    override fun onItemMenuClick(position: Int, itemMenu: ImageView) {
        showPopup(itemMenu)
    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(this@MyActivities, ActivitiesDetails::class.java))
    }

    override fun onItemLike(position: Int) {

    }

    override fun onItemChat(position: Int) {

    }

    override fun onItemJoin(position: Int, type: String?) {
    }



    override fun onJoin(parentPosition: Int, childPosition: Int) {
        Toast.makeText(this@MyActivities, "parent " + parentPosition + " child " + childPosition, Toast.LENGTH_SHORT).show()
        Log.e("parent " + parentPosition + " child " + childPosition, "hh")
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.back_f -> {
                finish()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun showPopup(v: View) {
        val wrapper = ContextThemeWrapper(this, R.style.MyPopupMenu)
        val popup = PopupMenu(wrapper, v)
        val inflater = popup.getMenuInflater()
        inflater.inflate(R.menu.my_activities_menu, popup.getMenu())
        val menuHelper = MenuPopupHelper(this@MyActivities, popup.menu as MenuBuilder, v)
        menuHelper.setForceShowIcon(true)
        popup.setOnMenuItemClickListener(this@MyActivities);
        menuHelper.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.addDate -> {
                return true
            }
            R.id.removeAct -> {
                return true
            }
            R.id.hideAct -> {
                return true
            }
            R.id.disableNotification -> {
                return true
            }
            else -> return false
        }
    }
}
