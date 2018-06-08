package com.clubz.ui.user_activities.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.*

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.activity.ActivitiesDetails
import com.clubz.ui.user_activities.expandable_recycler_view.*
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_find_activities.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Frag_Find_Activities.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Frag_Find_Activities.newInstance] factory method to
 * create an instance of this fragment.
 */
class Frag_Find_Activities : Fragment(), View.OnClickListener, ParentViewClickListioner, ChildViewClickListioner {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mContext: Context? = null
    private var todayAdapter: TodaysActivitiesCategoryAdapter? = null
    private var tomorrowAdapter: TomorrowActivitiesCategoryAdapter? = null
    private var soonAdapter: SoonActivitiesCategoryAdapter? = null
    private var othersAdapter: OthersActivitiesCategoryAdapter? = null
    private var isTodayOpen: Boolean = false
    private var isTomorrowOpen: Boolean = false
    private var isSoonOpen: Boolean = false
    private var isOthersOpen: Boolean = false
    private val INITIAL_POSITION = 0.0f
    private val ROTATED_POSITION = 180f
    private var todayList: List<GetOthersActivitiesResponce.DataBean.TodayBean>? = null
    private var tomorrowList: List<GetOthersActivitiesResponce.DataBean.TomorrowBean>? = null
    private var soonList: List<GetOthersActivitiesResponce.DataBean.SoonBean>? = null
    private var othersList: List<GetOthersActivitiesResponce.DataBean.OthersBean>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.frag_find_activities, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewToday.layoutManager = LinearLayoutManager(mContext)
        recyclerViewTomorrow.layoutManager = LinearLayoutManager(mContext)
        recyclerViewSoon.layoutManager = LinearLayoutManager(mContext)
        recyclerViewOthers.layoutManager = LinearLayoutManager(mContext)

        todayLay.setOnClickListener(this@Frag_Find_Activities)
        tomorrowLay.setOnClickListener(this@Frag_Find_Activities)
        soonLay.setOnClickListener(this@Frag_Find_Activities)
        othersLay.setOnClickListener(this@Frag_Find_Activities)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context

    }

    override fun onDetach() {
        super.onDetach()

    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Frag_Find_Activities.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): Frag_Find_Activities {
            val fragment = Frag_Find_Activities()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.todayLay -> {
                if (isTodayOpen) {
                    setRotation(arowToday, isTodayOpen)
                    isTodayOpen = false
                    //  arowToday.setImageResource(R.drawable.ic_down_arrow)
                    if (todayList != null && todayList!!.size > 0) {
                        recyclerViewToday.visibility = View.GONE
                    } else {
                        todayNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowToday, isTodayOpen)
                    isTodayOpen = true
                    //   arowToday.setImageResource(R.drawable.ic_drop_up_arrow)
                    if (todayList != null && todayList!!.size > 0) {
                        recyclerViewToday.visibility = View.VISIBLE
                    } else {
                        todayNoDataTxt.visibility = View.VISIBLE
                    }
                }
            }
            R.id.tomorrowLay -> {
                if (isTomorrowOpen) {
                    setRotation(arowTomorrow, isTomorrowOpen)
                    isTomorrowOpen = false
                    // arowTomorrow.setImageResource(R.drawable.ic_down_arrow)
                    if (tomorrowList != null && tomorrowList!!.size > 0) {
                        recyclerViewTomorrow.visibility = View.GONE
                    } else {
                        tomorrowNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowTomorrow, isTomorrowOpen)
                    isTomorrowOpen = true
                    // arowTomorrow.setImageResource(R.drawable.ic_drop_up_arrow)
                    if (tomorrowList != null && tomorrowList!!.size > 0) {
                        recyclerViewTomorrow.visibility = View.VISIBLE
                    } else {
                        tomorrowNoDataTxt.visibility = View.VISIBLE
                    }
                }
            }
            R.id.soonLay -> {
                if (isSoonOpen) {
                    setRotation(arowSoon, isSoonOpen)
                    isSoonOpen = false
                    arowSoon.setImageResource(R.drawable.ic_down_arrow)
                    if (soonList != null && soonList!!.size > 0) {
                        recyclerViewSoon.visibility = View.GONE
                    } else {
                        soonNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowSoon, isSoonOpen)
                    isSoonOpen = true
                    recyclerViewSoon.visibility = View.VISIBLE
                    arowSoon.setImageResource(R.drawable.ic_drop_up_arrow)
                    if (soonList != null && soonList!!.size > 0) {
                        recyclerViewSoon.visibility = View.VISIBLE
                    } else {
                        soonNoDataTxt.visibility = View.VISIBLE
                    }
                }
            }
            R.id.othersLay -> {
                if (isOthersOpen) {
                    setRotation(arowOthers, isOthersOpen)
                    isOthersOpen = false
                    //  arowOthers.setImageResource(R.drawable.ic_down_arrow)
                    if (othersList != null && othersList!!.size > 0) {
                        recyclerViewOthers.visibility = View.GONE
                    } else {
                        othersNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowOthers, isOthersOpen)
                    isOthersOpen = true
                    recyclerViewOthers.visibility = View.VISIBLE
                    //   arowSoon.setImageResource(R.drawable.ic_drop_up_arrow)
                    if (othersList != null && othersList!!.size > 0) {
                        recyclerViewOthers.visibility = View.VISIBLE
                    } else {
                        othersNoDataTxt.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun getActivitiesList(listType: String = "", limit: String = "", offset: String = "") {

        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.get_activity_list}?listType=${listType}&offset=${offset}&limit=${limit}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        var othersActivitiesResponce: GetOthersActivitiesResponce = Gson().fromJson(response, GetOthersActivitiesResponce::class.java)
                        updateUi(othersActivitiesResponce)
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
        }.execute(Frag_Find_Activities::class.java.name)
    }


    private fun updateUi(othersActivitiesResponce: GetOthersActivitiesResponce) {
        for (i in 0..othersActivitiesResponce.getData()!!.today!!.size - 1) {
            var todayData = othersActivitiesResponce.getData()!!.today!!.get(i)
            for (j in 0..todayData.events!!.size - 1) {
                var eventData = todayData.events!!.get(j)
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm.equals("1")) todayData.is_Confirm = true
            }
        }
        todayList = othersActivitiesResponce.getData()!!.today

        for (i in 0..othersActivitiesResponce.getData()!!.tomorrow!!.size - 1) {
            var tomorrowData = othersActivitiesResponce.getData()!!.tomorrow!!.get(i)
            for (j in 0..tomorrowData.events!!.size - 1) {
                var eventData = tomorrowData.events!!.get(j)
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm.equals("1")) tomorrowData.is_Confirm = true
            }
        }
        tomorrowList = othersActivitiesResponce.getData()!!.tomorrow

        for (i in 0..othersActivitiesResponce.getData()!!.soon!!.size - 1) {
            var soonData = othersActivitiesResponce.getData()!!.soon!!.get(i)
            for (j in 0..soonData.events!!.size - 1) {
                var eventData = soonData.events!!.get(j)
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm.equals("1")) soonData.is_Confirm = true
            }
        }
        soonList = othersActivitiesResponce.getData()!!.soon

        for (i in 0..othersActivitiesResponce.getData()!!.others!!.size - 1) {
            var otherData = othersActivitiesResponce.getData()!!.others!!.get(i)
            for (j in 0..otherData.events!!.size - 1) {
                var eventData = otherData.events!!.get(j)
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm.equals("1")) otherData.is_Confirm = true
            }
        }
        othersList = othersActivitiesResponce.getData()!!.others

        todayAdapter = TodaysActivitiesCategoryAdapter(mContext, todayList, this@Frag_Find_Activities, this@Frag_Find_Activities)
        todayAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = todayList!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = todayList!![position]
            }

        })
        recyclerViewToday.setAdapter(todayAdapter)
        tomorrowAdapter = TomorrowActivitiesCategoryAdapter(mContext, tomorrowList, this@Frag_Find_Activities, this@Frag_Find_Activities)
        tomorrowAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = tomorrowList!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = tomorrowList!![position]
            }

        })
        recyclerViewTomorrow.setAdapter(tomorrowAdapter)

        soonAdapter = SoonActivitiesCategoryAdapter(mContext, soonList, this@Frag_Find_Activities, this@Frag_Find_Activities)
        soonAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = soonList!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = soonList!![position]
            }

        })
        recyclerViewSoon.setAdapter(soonAdapter)

        othersAdapter = OthersActivitiesCategoryAdapter(mContext, othersList, this@Frag_Find_Activities, this@Frag_Find_Activities)
        othersAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = othersList!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = othersList!![position]
            }

        })
        recyclerViewSoon.setAdapter(othersAdapter)
    }

    override fun onResume() {
        super.onResume()
        getActivitiesList()
    }

    private fun setRotation(imgView: ImageView, expanded: Boolean) {
        val rotateAnimation: RotateAnimation
        if (expanded) { // rotate clockwise
            rotateAnimation = RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        } else { // rotate counterclockwise
            rotateAnimation = RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        }

        rotateAnimation.duration = 200
        rotateAnimation.fillAfter = true
        imgView.startAnimation(rotateAnimation)
    }

    override fun onItemMenuClick(position: Int, itemMenu: ImageView) {
        Log.e("parent " + position, " " + position)
        //Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(position: Int) {
    }

    override fun onItemLike(position: Int) {
    }

    override fun onItemChat(position: Int) {
    }

    override fun onItemJoin(position: Int, type: String) {
        // startActivity(Intent(mContext, ActivitiesDetails::class.java))
        when (type) {
            "today" -> {

            }
            "tomorrow" -> {

            }
            "soon" -> {

            }
            "others" -> {

            }

        }
    }

    override fun onJoin(parentPosition: Int, childPosition: Int) {
        Toast.makeText(context, "parent " + parentPosition + " child " + childPosition, Toast.LENGTH_SHORT).show()
        Log.e("parent " + parentPosition + " child " + childPosition, "hh")
    }
}
