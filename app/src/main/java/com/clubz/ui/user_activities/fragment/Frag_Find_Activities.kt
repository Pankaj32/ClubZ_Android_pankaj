package com.clubz.ui.user_activities.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.*
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.adapter.ConfirmAffiliatesAdapter
import com.clubz.ui.user_activities.adapter.JoinAffiliatesAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.*
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner
import com.clubz.ui.user_activities.model.GetConfirmAffiliates
import com.clubz.ui.user_activities.model.GetJoinAffliates
import com.clubz.ui.user_activities.model.GetOthersActivitiesResponce
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Picasso
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
    private var height: Int = 0
    private var width: Int = 0
    private var hasAffliates: Int = 0
    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
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
        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        todayLay.setOnClickListener(this@Frag_Find_Activities)
        tomorrowLay.setOnClickListener(this@Frag_Find_Activities)
        soonLay.setOnClickListener(this@Frag_Find_Activities)
        othersLay.setOnClickListener(this@Frag_Find_Activities)
        val display = getActivity().getWindowManager().getDefaultDisplay()
        width = display.getWidth()
        height = display.getHeight()
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
                    todayExCol.setText(R.string.collapsed)
                    //  arowToday.setImageResource(R.drawable.ic_down_arrow)
                    if (todayList != null && todayList!!.size > 0) {
                        recyclerViewToday.visibility = View.GONE
                    } else {
                        todayNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowToday, isTodayOpen)
                    isTodayOpen = true
                    todayExCol.setText(R.string.expanded)
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
                    tomorrowExCol.setText(R.string.collapsed)
                    // arowTomorrow.setImageResource(R.drawable.ic_down_arrow)
                    if (tomorrowList != null && tomorrowList!!.size > 0) {
                        recyclerViewTomorrow.visibility = View.GONE
                    } else {
                        tomorrowNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowTomorrow, isTomorrowOpen)
                    isTomorrowOpen = true
                    tomorrowExCol.setText(R.string.expanded)
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
                    soonExCol.setText(R.string.collapsed)
                    arowSoon.setImageResource(R.drawable.ic_down_arrow)
                    if (soonList != null && soonList!!.size > 0) {
                        recyclerViewSoon.visibility = View.GONE
                    } else {
                        soonNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowSoon, isSoonOpen)
                    isSoonOpen = true
                    soonExCol.setText(R.string.expanded)
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
                    othersExCol.setText(R.string.collapsed)
                    //  arowOthers.setImageResource(R.drawable.ic_down_arrow)
                    if (othersList != null && othersList!!.size > 0) {
                        recyclerViewOthers.visibility = View.GONE
                    } else {
                        othersNoDataTxt.visibility = View.GONE
                    }
                } else {
                    setRotation(arowOthers, isOthersOpen)
                    isOthersOpen = true
                    othersExCol.setText(R.string.expanded)
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
                        hasAffliates = othersActivitiesResponce.getData()!!.hasAffiliates!!
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
                Log.e("authToken", SessionManager.getObj().user.auth_token)
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

        /* for (i in 0..othersActivitiesResponce.getData()!!.others!!.size - 1) {
             var otherData = othersActivitiesResponce.getData()!!.others!!.get(i)
             for (j in 0..otherData.events!!.size - 1) {
                 var eventData = otherData.events!!.get(j)
                 eventData.childIndex = j
                 eventData.parentIndex = i
                 if (eventData.is_confirm.equals("1")) otherData.is_Confirm = true
             }
         }*/

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
        if (todayList != null && todayList!!.size > 0) {
            recyclerViewToday.visibility = View.VISIBLE
        } else {
            todayNoDataTxt.visibility = View.VISIBLE
        }
        setRotation(arowToday, isTodayOpen)
        isTodayOpen = false
        todayExCol.setText(R.string.expanded)
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
        recyclerViewOthers.setAdapter(othersAdapter)
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

    override fun onItemLike(position: Int, type: String) {
        when (type) {
            "today" -> {
                if (hasAffliates == 1) {
                    var todayActivity = todayList!![position]
                    getUserJoinAfiliatesList(todayActivity.activityId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_no_affiliates)
                }
                // popUpJoin("heart")
            }
            "tomorrow" -> {
                if (hasAffliates == 1) {
                    var tomorrowActivity = tomorrowList!![position]
                    getUserJoinAfiliatesList(tomorrowActivity.activityId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_no_affiliates)
                } //  popUpJoin("heart")
            }
            "soon" -> {
                if (hasAffliates == 1) {
                    var soonActivity = soonList!![position]
                    getUserJoinAfiliatesList(soonActivity.activityId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_no_affiliates)
                } //popUpJoin("heart")
            }
            "others" -> {
                if (hasAffliates == 1) {
                    var othersActivity = othersList!![position]
                    getUserJoinAfiliatesList(othersActivity.activityId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_no_affiliates)
                }    // popUpJoin("heart")
            }

        }
    }

    override fun onItemChat(position: Int) {
    }

    override fun onItemJoin(position: Int, type: String) {
        // startActivity(Intent(mContext, ActivitiesDetails::class.java))
        when (type) {
        /*   "today" -> {
               popUpJoin("hand")
           }
           "tomorrow" -> {
               popUpJoin("hand")
           }
           "soon" -> {
               popUpJoin("hand")
           }
           "others" -> {
               popUpJoin("hand")
           }*/

        }
    }

    override fun onJoin(parentPosition: Int, childPosition: Int, type: String) {
        when (type) {
            "today" -> {
                var todayActivity = todayList!![parentPosition]
                var todayEvents = todayActivity.events!![childPosition]
                if (todayEvents.hasAffiliatesJoined.equals("1")) {
                    getUserConfirmAfiliatesList(todayActivity.activityId!!, todayEvents.activityEventId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_cant_join)
                }
            }
            "tomorrow" -> {
                var tomorrowActivity = tomorrowList!![parentPosition]
                var tomorrowEvents = tomorrowActivity.events!![childPosition]
                if (tomorrowEvents.hasAffiliatesJoined.equals("1")) {
                    getUserConfirmAfiliatesList(tomorrowActivity.activityId!!, tomorrowEvents.activityEventId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_cant_join)
                }
            }
            "soon" -> {
                var soonActivity = soonList!![parentPosition]
                var soonEvents = soonActivity.events!![childPosition]
                if (soonEvents.hasAffiliatesJoined.equals("1")) {
                    getUserConfirmAfiliatesList(soonActivity.activityId!!, soonEvents.activityEventId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_cant_join)
                }
            }
            "others" -> {

            }

        }
    }

    internal fun popUpJoin(activityId: String, getJoinAffliates: GetJoinAffliates) {
        //    var isLike: Boolean = false;
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_join_events)
        dialog.window!!.setLayout(width * 10 / 11, WindowManager.LayoutParams.WRAP_CONTENT)
        //    dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        // dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val likeLay = dialog.findViewById<View>(R.id.likeLay) as RelativeLayout
        val profileImage = dialog.findViewById<View>(R.id.profileImage) as ImageView
        val topIcon = dialog.findViewById<View>(R.id.topIcon) as ImageView
        val like = dialog.findViewById<View>(R.id.like) as ImageView
        val dialogRecycler = dialog.findViewById<View>(R.id.dialogRecycler) as RecyclerView
        val adapter = JoinAffiliatesAdapter(mContext, getJoinAffliates.getData()!!.affiliates)
        dialogRecycler.adapter = adapter
        val activityUserName = dialog.findViewById<View>(R.id.activityUserName) as TextView
        val mTitle = dialog.findViewById<View>(R.id.mTitle) as TextView
        val mCancel = dialog.findViewById<View>(R.id.mCancel) as TextView
        val mJoin = dialog.findViewById<View>(R.id.mJoin) as TextView


        if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
            like.setImageResource(R.drawable.active_heart_ico)
        } else {
            like.setImageResource(R.drawable.inactive_heart_ico)
        }
        topIcon.setImageResource(R.drawable.active_heart_ico)
        mTitle.setText(R.string.joinTitle)
        activityUserName.setText(userName)
        if (!userImage.equals("")) {
            Picasso.with(profileImage.context).load(userImage).fit().into(profileImage)
        }
        //}
        dialog.setCancelable(true)
        dialog.show()
        likeLay.setOnClickListener(View.OnClickListener {
            if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
                getJoinAffliates.getData()!!.isJoined = "0"
                like.setImageResource(R.drawable.inactive_heart_ico)
            } else {
                getJoinAffliates.getData()!!.isJoined = "1"
                like.setImageResource(R.drawable.active_heart_ico)
            }
        })
        mCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        mJoin.setOnClickListener(View.OnClickListener {
            var mUserId: String = ""
            var affiliateId: String = ""
            if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
                mUserId = userId
            }
            for (affiliates in getJoinAffliates.getData()!!.affiliates!!) {
                if (affiliates.isJoined.equals("1")) {
                    if (affiliateId.equals("")) {
                        affiliateId = affiliates.userAffiliateId!!
                    } else {
                        affiliateId = affiliateId + "," + affiliates.userAffiliateId
                    }
                }
            }
            joinActivity(activityId, affiliateId, mUserId, dialog)
        })
    }

    internal fun popUpConfirm(activityId: String, activityEventId: String, confirmAffiliates: GetConfirmAffiliates) {
        var isLike: Boolean = false;
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_join_events)
        dialog.window!!.setLayout(width * 10 / 11, WindowManager.LayoutParams.WRAP_CONTENT)
        //    dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        // dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val likeLay = dialog.findViewById<View>(R.id.likeLay) as RelativeLayout
        val profileImage = dialog.findViewById<View>(R.id.profileImage) as ImageView
        val topIcon = dialog.findViewById<View>(R.id.topIcon) as ImageView
        val like = dialog.findViewById<View>(R.id.like) as ImageView
        val dialogRecycler = dialog.findViewById<View>(R.id.dialogRecycler) as RecyclerView
        val adapter = ConfirmAffiliatesAdapter(mContext, confirmAffiliates.getData()!!.affiliates)
        dialogRecycler.adapter = adapter
        val activityUserName = dialog.findViewById<View>(R.id.activityUserName) as TextView
        val mTitle = dialog.findViewById<View>(R.id.mTitle) as TextView
        val mCancel = dialog.findViewById<View>(R.id.mCancel) as TextView
        val mJoin = dialog.findViewById<View>(R.id.mJoin) as TextView
        if (confirmAffiliates.getData()!!.isConfirmed != null) {
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                like.setImageResource(R.drawable.hand_ico)
            } else {
                like.setImageResource(R.drawable.ic_inactive_hand_ico)
            }
        } else {
            likeLay.visibility = View.GONE
        }
        topIcon.setImageResource(R.drawable.hand_ico)
        mTitle.setText(R.string.confirmTitle)


        activityUserName.setText(userName)
        if (!userImage.equals("")) {
            Picasso.with(profileImage.context).load(userImage).fit().into(profileImage)
        }
        //}
        dialog.setCancelable(true)
        dialog.show()
        likeLay.setOnClickListener(View.OnClickListener {
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                confirmAffiliates.getData()!!.isConfirmed = "0"
                like.setImageResource(R.drawable.ic_inactive_hand_ico)
            } else {
                confirmAffiliates.getData()!!.isConfirmed = "1"
                like.setImageResource(R.drawable.hand_ico)
            }
        })
        mCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        mJoin.setOnClickListener(View.OnClickListener {
            var mUserId: String = ""
            var affiliateId: String = ""
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                mUserId = userId
            }
            for (affiliates in confirmAffiliates.getData()!!.affiliates!!) {
                if (affiliates.isConfirmed.equals("1")) {
                    if (affiliateId.equals("")) {
                        affiliateId = affiliates.userAffiliateId!!
                    } else {
                        affiliateId = affiliateId + "," + affiliates.userAffiliateId
                    }
                }
            }
            confirmActivity(activityId, affiliateId, activityEventId, mUserId, dialog)
        })
    }

    fun getUserJoinAfiliatesList(activityId: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserJoinAffiliates}?userId=${userId}&activityId=${activityId}",/*userId=74&activityId=7*/
                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        //  popUpJoin(type)
                        var getJoinAffliates: GetJoinAffliates = Gson().fromJson(response, GetJoinAffliates::class.java)
                        popUpJoin(activityId, getJoinAffliates)
                    }


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
                Log.e("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Find_Activities::class.java.name)
    }

    fun getUserConfirmAfiliatesList(activityId: String, activityEventId: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserConfirmAffiliates}?userId=${userId}&activityEventId=${activityEventId}&activityId=${activityId}",


                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        var getConfirmAffiliates: GetConfirmAffiliates = Gson().fromJson(response, GetConfirmAffiliates::class.java)
                        popUpConfirm(activityId, activityEventId, getConfirmAffiliates)
                    }


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
                Log.e("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Find_Activities::class.java.name)
    }

    fun joinActivity(activityId: String,
                     affiliateId: String,
                     userId: String,
                     dialog1: Dialog) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.joinActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog1.dismiss()
                        getActivitiesList()
                    } else {
                        // nodataLay.visibility = View.VISIBLE
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
                params["activityId"] = activityId
                params["affiliateId"] = affiliateId
                params["userId"] = userId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Find_Activities::class.java.name)
    }

    fun confirmActivity(activityId: String,
                        affiliateId: String,
                        activityEventId: String,
                        userId: String,
                        dialog1: Dialog) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.confirmActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog1.dismiss()
                        getActivitiesList()
                    } else {
                        // nodataLay.visibility = View.VISIBLE
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
                params["activityId"] = activityId
                params["activityEventId"] = activityEventId
                params["userId"] = userId
                params["affiliateId"] = affiliateId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Find_Activities::class.java.name)
    }
}
