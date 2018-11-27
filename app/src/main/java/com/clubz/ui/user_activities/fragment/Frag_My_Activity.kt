package com.clubz.ui.user_activities.fragment

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.model.ChatBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.repo.AllActivitiesRepo
import com.clubz.data.local.db.repo.AllEventsRepo
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.AllActivities
import com.clubz.data.model.AllEvents
import com.clubz.data.model.DialogMenu
import com.clubz.data.remote.WebService
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.activities.fragment.ItemListDialogFragment
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.Internet_Connection_dialog
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.user_activities.activity.AddEventActivity
import com.clubz.ui.user_activities.activity.ActivitiesDetails
import com.clubz.ui.user_activities.activity.EventDetailsActivity
import com.clubz.ui.user_activities.activity.NewActivities
import com.clubz.ui.user_activities.adapter.*
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner
import com.clubz.ui.user_activities.model.*
import com.clubz.utils.DateTimeUtil
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_my_activity.*
import org.json.JSONObject
import kotlin.collections.ArrayList


class Frag_My_Activity : Fragment(),
        ActivityItemClickListioner,
        ItemListDialogFragment.Listener,
        SwipeRefreshLayout.OnRefreshListener {

    override fun onRefresh() {
        /*if (isMyState) {
            getActivitiesList()
        } else {
            getOthersActivitiesList()
        }*/
        pageListner?.resetState()
        getAllActivitiesList(isPull = true)
    }


    private var pageListner: RecyclerViewScrollListener? = null
    private var activitiesAdapter: ActivitiesAdapter? = null
    private var tempuraryActivityList = ArrayList<ActivitiesBean.DataBean>()
    private var activityList = ArrayList<ActivitiesBean.DataBean>()
    private var actionPosition: Int? = null
    private var hideUnhide: String? = null
    private var height: Int = 0
    private var width: Int = 0
    var day = -1
    var month = -1
    var year = -1
    var eventDate: String = ""
    var eventTime: String = ""
    var latitude = ""
    var longitute = ""
    val REQUEST_CODE_AUTOCOMPLETE: Int = 1000
    var mContext: Context? = null
    private var hasAffliates = ""

    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    private var confirmStatus: String = ""
    var isMyActivity: Boolean = false
    private var now: String = ""
    private var isResume: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_my_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewMyActivities.layoutManager = LinearLayoutManager(mContext)
        val diametric = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(diametric)
        width = diametric.widthPixels
        height = diametric.heightPixels
        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        hasAffliates = SessionManager.getObj().user.hasAffiliates
        swiperefresh.setOnRefreshListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewMyActivities.itemAnimator = null
        recyclerViewMyActivities.layoutManager = lm
        recyclerViewMyActivities.setHasFixedSize(true)

        /* nodataLay.visibility = if (activityList.isEmpty()) View.VISIBLE else View.GONE*/
        activitiesAdapter = ActivitiesAdapter(mContext, activityList, this)
        recyclerViewMyActivities.adapter = activitiesAdapter

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getAllActivitiesList(offset = page * 10)
            }
        }
        recyclerViewMyActivities.addOnScrollListener(pageListner)

        val tempActiviyList = AllActivitiesRepo().getAllActivities()
        val tempEventList = AllEventsRepo().getAllEvents()
        val activityListLocal = ArrayList<ActivitiesBean.DataBean>()
        if (tempActiviyList.size > 0) {
            for (activity in tempActiviyList) {
                val activityBean = ActivitiesBean.DataBean()
                val events = ArrayList<ActivitiesBean.DataBean.EventsBean>()

                activityBean.activityId = activity.activityId
                activityBean.activityEventId = activity.activityEventId
                activityBean.event_date = activity.event_date
                activityBean.is_my_activity = activity.is_my_activity
                activityBean.image = activity.image
                activityBean.activityName = activity.activityName
                activityBean.is_hide = activity.is_hide
                activityBean.name = activity.name
                activityBean.leader_id = activity.leader_id
                activityBean.club_id = activity.club_id
                activityBean.creator_id = activity.creator_id
                activityBean.location = activity.location
                activityBean.latitude = activity.latitude
                activityBean.longitude = activity.longitude
                activityBean.fee_type = activity.fee_type
                activityBean.fee = activity.fee
                activityBean.min_users = activity.min_users
                activityBean.max_users = activity.max_users
                activityBean.user_role = activity.user_role
                activityBean.description = activity.description
                activityBean.terms_conditions = activity.terms_conditions
                activityBean.is_cancel = activity.is_cancel
                activityBean.status = activity.status
                activityBean.crd = activity.crd
                activityBean.upd = activity.upd
                activityBean.club_name = activity.club_name
                activityBean.clubId = activity.clubId
                activityBean.is_like = activity.is_like
                activityBean.userId = activity.userId
                activityBean.full_name = activity.full_name
                activityBean.device_token = activity.device_token
                activityBean.profile_image = activity.profile_image
                activityBean.listType = activity.listType

                activityBean.creator_phone = activity.creator_phone
                activityBean.contact_no_visibility = activity.contact_no_visibility
                activityBean.leader_name = activity.leader_name
                activityBean.leader_prflimage = activity.leader_prflimage
                activityBean.leader_phno = activity.leader_phno
                activityBean.leader_contact_no_visibility = activity.leader_contact_no_visibility
                activityBean.totalUser = activity.totalUser

                for (event in tempEventList) {
                    if (activity.activityId.equals(event.activityId) && activity.listType.equals(event.list_type)) {
                        val eventBean = ActivitiesBean.DataBean.EventsBean()
                        eventBean.activityEventId = event.activityEventId
                        eventBean.event_title = event.event_title
                        eventBean.event_date = event.event_date
                        eventBean.event_time = event.event_time
                        eventBean.description = event.description
                        eventBean.location = event.location
                        eventBean.latitude = event.latitude
                        eventBean.longitude = event.longitude
                        eventBean.fee = event.fee
                        eventBean.fee_type = event.fee_type
                        eventBean.min_users = event.min_users
                        eventBean.max_users = event.max_users
                        eventBean.total_users = event.total_users
                        eventBean.joined_users = event.joined_users
                        eventBean.confirm_users = event.confirm_users
                        eventBean.confirm_userlist = event.confirm_userlist
                        eventBean.is_confirm = event.is_confirm
                        eventBean.hasJoined = event.hasJoined
                        eventBean.hasAffiliatesJoined = event.hasAffiliatesJoined
                        eventBean.is_cancel = event.is_cancel
                        eventBean.list_type = event.list_type
                        events.add(eventBean)
                    }
                }
                if (events.size > 0) activityBean.events = events

                activityListLocal.add(activityBean)
            }
            updateAllUiOthers(activityListLocal, true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (isResume) {
            getAllActivitiesList(isPull = true)
        } else {
            getAllActivitiesList(isPull = true)
            isResume = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context;
    }

    override fun onDetach() {
        super.onDetach()
    }


    fun getAllActivitiesList(listType: String = "", limit: String = "10", offset: Int = 0, isPull: Boolean? = false) {
        /*val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing || !isResume) dialog.show()*/
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext,
                "${WebService.get_all_activity_list}?listType=${listType}&offset=${offset}&limit=${limit}" +
                        "&date=${DateTimeUtil.getCurrentDate()}&time=${DateTimeUtil.getCurrentTime()}", true, false)
        //WebService.get_activity_list + listType + "&limit=&offset=",
        {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    // dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val activityBean: ActivitiesBean = Gson().fromJson(response, ActivitiesBean::class.java)
                        now = activityBean.dateTime!!
                        updateAllUiOthers(activityBean.data, isPull)
                        if (isPull!!) updateInDb(activityBean.data)
                    } else {
                        nodataLay.visibility = View.VISIBLE
                    }
                    // searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                //  dialog.dismiss()
            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /*params.put("listType", listType)
                params.put("offset", offset.toString())
                params.put("limit", limit)*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun updateInDb(data: List<ActivitiesBean.DataBean>?) {
        AllActivitiesRepo().deleteTable()
        AllEventsRepo().deleteTable()
        for (activities in data!!) {
            val activity = AllActivities()
            activity.activityId = activities.activityId
            activity.activityEventId = activities.activityEventId
            activity.event_date = activities.event_date
            activity.is_my_activity = activities.is_my_activity
            activity.image = activities.image
            activity.activityName = activities.activityName
            activity.is_hide = activities.is_hide
            activity.name = activities.name
            activity.leader_id = activities.leader_id
            activity.club_id = activities.club_id
            activity.creator_id = activities.creator_id
            activity.location = activities.location
            activity.latitude = activities.latitude
            activity.longitude = activities.longitude
            activity.fee_type = activities.fee_type
            activity.fee = activities.fee
            activity.min_users = activities.min_users
            activity.max_users = activities.max_users
            activity.user_role = activities.user_role
            activity.description = activities.description
            activity.terms_conditions = activities.terms_conditions
            activity.is_cancel = activities.is_cancel
            activity.status = activities.status
            activity.crd = activities.crd
            activity.upd = activities.upd
            activity.club_name = activities.club_name
            activity.clubId = activities.clubId
            activity.is_like = activities.is_like
            activity.userId = activities.userId
            activity.full_name = activities.full_name
            activity.device_token = activities.device_token
            activity.profile_image = activities.profile_image

            activity.listType = activities.listType

            activity.creator_phone = activities.creator_phone
            activity.contact_no_visibility = activities.contact_no_visibility
            activity.leader_name = activities.leader_name
            activity.leader_prflimage = activities.leader_prflimage
            activity.leader_phno = activities.leader_phno
            activity.leader_contact_no_visibility = activities.leader_contact_no_visibility
            activity.totalUser = activities.totalUser


            AllActivitiesRepo().insert(activity)
            if (activities.events != null) {
                for (events in activities.events!!) {
                    val event = AllEvents()
                    event.activityId = activities.activityId
                    event.activityEventId = events.activityEventId
                    event.event_title = events.event_title
                    event.event_date = events.event_date
                    event.event_time = events.event_time
                    event.description = events.description
                    event.location = events.location
                    event.latitude = events.latitude
                    event.longitude = events.longitude
                    event.fee = events.fee
                    event.fee_type = events.fee_type
                    event.min_users = events.min_users
                    event.max_users = events.max_users
                    event.total_users = events.total_users
                    event.joined_users = events.joined_users
                    event.confirm_users = events.confirm_users
                    event.confirm_userlist = events.confirm_userlist
                    event.is_confirm = events.is_confirm
                    event.hasJoined = events.hasJoined
                    event.hasAffiliatesJoined = events.hasAffiliatesJoined
                    event.is_cancel = events.is_cancel
                    event.list_type = events.list_type
                    AllEventsRepo().insert(event)
                }
            }
        }
    }

    private fun updateAllUiOthers(activityBean: List<ActivitiesBean.DataBean>?, pull: Boolean?) {
        activityList.clear()
        if (pull!!) {
            tempuraryActivityList.clear()
            activitiesAdapter?.notifyDataSetChanged()
        }
        for (dataBean in activityBean!!) {
            if (dataBean.is_my_activity.equals("0")) {
                if (dataBean.is_hide.equals("0")) tempuraryActivityList.add(dataBean)
            } else {
                tempuraryActivityList.add(dataBean)
            }
        }
        if (isMyActivity) {
            for (dataBean in tempuraryActivityList) {
                if (dataBean.is_my_activity.equals("1")) activityList.add(dataBean)
            }
        } else {
            activityList.addAll(tempuraryActivityList)
        }
        nodataLay.visibility = if (activityList.isEmpty()) View.VISIBLE else View.GONE
        activitiesAdapter?.notifyDataSetChanged()
    }

    override fun onLongPress(type: String, activityPosition: Int) {

        actionPosition = activityPosition
        val activity = activityList[actionPosition!!]

        val list: ArrayList<DialogMenu> = arrayListOf()
        if (activity.is_my_activity.equals("1")) {
            list.add(DialogMenu(getString(R.string.add_date), R.drawable.ic_add_24))
            list.add(DialogMenu(getString(R.string.remove_activity), R.drawable.ic_delete_icon))
            if (activity.is_hide.equals("0")) {
                list.add(DialogMenu(getString(R.string.hide_activity), R.drawable.ic_visibility_off))
                hideUnhide = "hide"
            } else {
                list.add(DialogMenu(getString(R.string.un_hide_activity), R.drawable.ic_visibility))
                hideUnhide = "unhide"
            }
            list.add(DialogMenu(getString(R.string.edit_activity), R.drawable.ic_edit))
        } else {
            if (activity.is_like.equals("0")) {
                list.add(DialogMenu(getString(R.string.join_activity), R.drawable.ic_cards_heart))
            } else {
                list.add(DialogMenu(getString(R.string.leave_activity), R.drawable.ic_favorite_border))
            }

        }

        val a = ItemListDialogFragment()
        a.setInstanceMyActivity(this, list)
        a.show(fragmentManager, "draj")
        //ItemListDialogFragment.newInstance(list).show(fragmentManager, "draj")

    }

    override fun onItemClick(type: String, activityPosition: Int) {

        var activityId = ""
        var activityName = ""
        var clubName = ""
        var clubId = ""
        var userId = ""
        var userName = ""
        var userProfileImg = ""
        val activitiesBean = activityList.get(activityPosition);

        activityId = activitiesBean.activityId!!
        activityName = activitiesBean.activityName!!
        clubName = activitiesBean.club_name!!
        clubId = activitiesBean.clubId!!
        //other

        if (activitiesBean.is_my_activity.equals("1")) {
            startActivity(Intent(mContext, ActivitiesDetails::class.java)
                    /*.putExtra("activityId", activityId)
                    .putExtra("activityName", activityName)
                    .putExtra("clubId", clubId)
                    .putExtra("clubName", clubName)*/
                    .putExtra(NotificatioKeyUtil.Key_From, "")
                    .putExtra("activityBean", activitiesBean)
                    .putExtra("From", "MyActivities")
                    .putExtra("type", "my")
            )
        } else {
            userId = activitiesBean.userId!!
            userName = activitiesBean.full_name!!
            userProfileImg = activitiesBean.profile_image!!
            startActivity(Intent(mContext, ActivitiesDetails::class.java)
                    /*.putExtra("activityId", activityId)
                    .putExtra("userId", userId)
                    .putExtra("userName", userName)
                    .putExtra("userProfileImg", userProfileImg)
                    .putExtra("activityName", activityName)
                    .putExtra("clubId", clubId)
                    .putExtra("clubName", clubName)*/
                    .putExtra(NotificatioKeyUtil.Key_From, "")
                    .putExtra("activityBean", activitiesBean)
                    .putExtra("From", "OthersActivity")
                    .putExtra("type", "others")
            )
        }
    }

    override fun onJoinClick(type: String, activityPosition: Int) {

    }

    override fun onConfirm(type: String, activityPosition: Int, eventPosition: Int) {
        val activitiesBean = activityList.get(activityPosition)
        val eventBean = activitiesBean.events?.get(eventPosition)
        if (activitiesBean.is_my_activity.equals("1")) {
            confirmStatus = if (eventBean?.is_confirm.equals("0")) "1" else "0"
            showConfirmationDialog(action = "confirm", activityId = activitiesBean.activityId!!, activityEventId = eventBean?.activityEventId!!)
        } else {
            if (!activitiesBean.is_my_activity!!.equals("1")) {
                if (eventBean?.hasJoined.equals("1")) {
                    getUserConfirmAfiliatesList(activitiesBean.activityId!!, eventBean?.activityEventId!!)
                } else {
                    Util.showSnake(mContext!!, snakLay!!, R.string.d_cant_join)
                }
            }
        }
    }

    override fun onEventDateClick(activityPosition: Int, eventPosition: Int) {
        val activitiesBean = activityList.get(activityPosition)
        val eventBean = activitiesBean.events?.get(eventPosition)
        if (activitiesBean.is_my_activity.equals("1")) {

            startActivity(Intent(mContext, EventDetailsActivity::class.java)
                    .putExtra("activity", activitiesBean)
                    .putExtra("event", eventBean)
                    .putExtra("now", now))
            // popUpDateDetails(activitiesBean, eventBean)
        }
    }

    //bottomsheet
    override fun onItemClicked(position: Int) {
        val activities = activityList[actionPosition!!]
        when (position) {
            0 -> {

                if (activities.is_my_activity == "1") {
                    if (Util.isConnectingToInternet(mContext!!)) {
                        startActivity(Intent(mContext, AddEventActivity::class.java).putExtra("activity", activities))
                    } else {
                        object : Internet_Connection_dialog(mContext!!) {
                            override fun tryaginlistner() {
                                this.dismiss()
                                startActivity(Intent(mContext, AddEventActivity::class.java).putExtra("activity", activities))
                            }
                        }.show()
                    }

                } else {
                    if (hasAffliates.equals("1")) {
                        getUserJoinAfiliatesList(activities.activityId!!,activities.clubId!!)
                    } else {
                        if (activities.is_like == "1") {
                            joinActivity(activities.activityId!!, "", "",activities?.clubId!!)
                        } else {
                            joinActivity(activities.activityId!!, "", userId,activities?.clubId!!)
                        }
                    }
                }
            }
            1 -> {
                showConfirmationDialog(action = "remove")
            }
            2 -> {
                showConfirmationDialog(action = hideUnhide!!)
            }
            3 -> {
                if (Util.isConnectingToInternet(mContext!!)) {
                    startActivity(Intent(mContext, NewActivities::class.java)
                            .putExtra("activityBean", activities))
                } else {
                    object : Internet_Connection_dialog(mContext!!) {
                        override fun tryaginlistner() {
                            this.dismiss()
                            startActivity(Intent(mContext, NewActivities::class.java)
                                    .putExtra("activityBean", activities))
                        }
                    }.show()
                }

            }
        }
    }

    private fun showConfirmationDialog(action: String = "", activityId: String = "", activityEventId: String = "") {
        try {
            val builder1 = AlertDialog.Builder(mContext)
            builder1.setTitle(mContext!!.getString(R.string.alert))
            if (action.equals("confirm")) {
                val status = if (confirmStatus.equals("1")) "confirm" else "unconfirm"
                builder1.setMessage("Are you sure you want to $status this Date?")
            } else {
                builder1.setMessage("Are you sure you want to $action this activity?")
            }
            builder1.setCancelable(false)
            builder1.setPositiveButton(mContext!!.getString(R.string.ok), { dialog, id ->
                when (action) {
                    "remove" -> {
                        deleteMyActivity(activityList[actionPosition!!].activityId!!)
                    }
                    "hide",
                    "unhide" -> {
                        hideMyActivity(activityList[actionPosition!!].activityId!!)
                    }
                    "confirm" -> confirmMyActivity(activityId, activityEventId, userId, confirmStatus, dialog, snakLay)
                }
            })

            builder1.setNegativeButton(mContext!!.getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val alert11 = builder1.create()
            alert11.show()
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext!!, R.color.nav_gray))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteMyActivity(activityId: String) {
        val dialog = CusDialogProg(mContext)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext,
                WebService.deleteMyActivity,
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
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
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["activityId"] = activityId

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun hideMyActivity(activityId: String) {
        val dialog = CusDialogProg(mContext)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext, WebService.hideMyActivity,
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
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
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["activityId"] = activityId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    /*private fun datePicker(i1: Int, i2: Int, i3: Int, addDateTxt: TextView) {
        val calendar = GregorianCalendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DATE)
        if (i1 != -1) {
            day = i1
            month = i2 - 1
            year = i3
        }
        val datepickerdialog = DatePickerDialog(mContext, R.style.DialogTheme2, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                val check = Date()
                check.year = p1 - 1900; check.month; check.date = p3
                val d = Date(System.currentTimeMillis() - 1000)
                d.hours = 0
                d.minutes = 0
                d.seconds = 0
                Util.e("Tag", "$d : ${p0!!.minDate} : $check")
                year = p1; month = p2 + 1;day = p3
                eventDate = "" + year + "/" + month + "/" + day
                addDateTxt.text = Util.convertDate("$year-$month-$day")
            }
        }, year, month, day)
        datepickerdialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datepickerdialog.window!!.setBackgroundDrawableResource(R.color.white)

        datepickerdialog.show()
    }

    private fun timePicker(addTimeTxt: TextView) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker = TimePickerDialog(mContext, R.style.DialogTheme2, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                val hr = if (p1 < 10) "0$p1" else "$p1"
                val min = if (p2 < 10) "0$p2" else "$p2"
                //val onTimeSet: Unit
                eventTime = "$hr:$min"
                addTimeTxt.text = Util.setTimeFormat(eventTime)
            }
        }, hour, minute, false)
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }
*/
    private fun openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(activity)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(activity, e.connectionStatusCode,
                    0 /* requestCode */).show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            val message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode)

            Log.e("TAG", message)
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                val place = PlaceAutocomplete.getPlace(mContext, data)
                latitude = place.latLng.latitude.toString()
                longitute = place.latLng.longitude.toString()

                /* dialog?.locationTxt?.setText(place.address)
                 dialog?.show()
                 Log.e("TAG", "Place Selected: " + place.name + " " + latitude + " " + longitute)*/
            }
        }
    }

    private fun isvaildDataToSend(eventNameTxt: EditText, addDateTxt: TextView, addTimeTxt: TextView, locationTxt: TextView, containerLay: RelativeLayout): Boolean {
        if (eventNameTxt.text.toString().isBlank()) {
            Util.showSnake(mContext, containerLay, R.string.a_EventName)
            eventNameTxt.requestFocus()
            return false
        } else if (addDateTxt.text.toString().isBlank()) {
            Util.showSnake(mContext, containerLay, R.string.a_date)
            return false
        } else if (addTimeTxt.text.toString().isBlank()) {
            Util.showSnake(mContext, containerLay, R.string.a_time)
            return false
        } else if (locationTxt.text.toString().isBlank()) {
            Util.showSnake(mContext, containerLay, R.string.a_location_dl)
            return false
        }
        return true
    }

    /*private fun addEvent(eventTitle: String,
                         eventDate: String,
                         eventTime: String,
                         location: String,
                         activityId: String,
                         description: String) {
        val dialogProgress = CusDialogProg(mContext)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext,
                WebService.addEvents,
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        dialog?.dismiss()
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
                    } else {
                        nodataLay.visibility = View.VISIBLE
                    }
                    // searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialogProgress.dismiss()
            }

            override fun onNetError() {
                dialogProgress.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["eventTitle"] = eventTitle
                params["eventDate"] = eventDate
                params["eventTime"] = eventTime
                params["location"] = location
                params["latitude"] = latitude
                params["longitude"] = longitute
                params["activityId"] = activityId
                params["description"] = description
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }*/


    fun getUserConfirmAfiliatesList(activityId: String, activityEventId: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserConfirmAffiliates}?userId=${userId}&activityEventId=${activityEventId}&activityId=${activityId}",


                true, true) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                dialog.dismiss()
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
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun getUserJoinAfiliatesList(activityId: String,clubId: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserJoinAffiliates}?userId=${userId}&activityId=${activityId}",/*userId=74&activityId=7*/
                true, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        //  popUpJoin(type)
                        var getJoinAffliates: GetJoinAffliates = Gson().fromJson(response, GetJoinAffliates::class.java)
                        popUpJoin(activityId, getJoinAffliates,clubId)
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
        }.execute(Frag_My_Activity::class.java.name)
    }

    /*var dialog: Dialog? = null
    private fun popUpAddEvents(activities: ActivitiesBean.DataBean) {
        if (dialog == null) {
            dialog = Dialog(mContext)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setContentView(R.layout.dialog_add_events)
            dialog!!.window!!.setLayout(width * 10 / 11, WindowManager.LayoutParams.WRAP_CONTENT)
            //    dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            // dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            val mCancel = dialog!!.findViewById<View>(R.id.mCancel) as TextView
            val mAdd = dialog!!.findViewById<View>(R.id.mAdd) as TextView
            val mTitle = dialog!!.findViewById<View>(R.id.mTitle) as TextView
            val addDateTxt = dialog!!.findViewById<View>(R.id.addDateTxt) as TextView
            val addTimeTxt = dialog!!.findViewById<View>(R.id.addTimeTxt) as TextView
            val locationTxt = dialog!!.findViewById<View>(R.id.locationTxt) as EditText
            val descTxt = dialog!!.findViewById<View>(R.id.descTxt) as EditText
            val eventNameTxt = dialog!!.findViewById<View>(R.id.eventNameTxt) as EditText
            val datelay = dialog!!.findViewById<View>(R.id.datelay) as RelativeLayout
            val timeLay = dialog!!.findViewById<View>(R.id.timeLay) as RelativeLayout
            val loctionLay = dialog!!.findViewById<View>(R.id.loctionLay) as RelativeLayout
            val containerLay = dialog!!.findViewById<View>(R.id.containerLay) as RelativeLayout

            // var latitute: String = ""
            // var longitute: String = ""
            mTitle.text = activities.activityName
            mTitle.visibility = View.GONE
            dialog!!.setCancelable(false)
            mCancel.setOnClickListener({ dialog!!.dismiss() })
            mAdd.setOnClickListener({
                val eventTitle = eventNameTxt.text.toString().trim()
                val location = locationTxt.text.toString().trim()
                val dese = descTxt.text.toString().trim()
                if (isvaildDataToSend(eventNameTxt, addDateTxt, addTimeTxt, locationTxt, containerLay)) addEvent(eventTitle, eventDate, eventTime, location, activities.activityId!!, dese)
            })
            datelay.setOnClickListener {
                hideDialogKeyBoard()
                datePicker(day, month, year, addDateTxt)
            }
            timeLay.setOnClickListener {
                hideDialogKeyBoard()
                timePicker(addTimeTxt)
            }
            *//* loctionLay.setOnClickListener {
                 hideDialogKeyBoard()
                 openAutocompleteActivity()
             }*//*

            dialog!!.setOnDismissListener { dialogInterface ->
                dialog = null
            }
        }
        dialog?.show()
    }*/

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    internal fun popUpConfirm(activityId: String, activityEventId: String, confirmAffiliates: GetConfirmAffiliates) {
        var isLike: Boolean = false
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_join_events)
        dialog.window!!.setLayout(width * 10 / 11, WindowManager.LayoutParams.WRAP_CONTENT)
        //    dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        // dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val likeLay = dialog.findViewById<View>(R.id.likeLay) as RelativeLayout
        val showSnack = dialog.findViewById<View>(R.id.showSnack) as RelativeLayout
        val profileImage = dialog.findViewById<View>(R.id.profileImage) as ImageView
        val topIcon = dialog.findViewById<View>(R.id.topIcon) as ImageView
        // val like = dialog.findViewById<View>(R.id.like) as ImageView
        val likeCkeck = dialog.findViewById<View>(R.id.likeCkeck) as CheckBox
        val dialogRecycler = dialog.findViewById<View>(R.id.dialogRecycler) as RecyclerView
        val adapter = ConfirmAffiliatesAdapter(mContext, confirmAffiliates.getData()!!.affiliates)
        dialogRecycler.adapter = adapter
        val activityUserName = dialog.findViewById<View>(R.id.activityUserName) as TextView
        val mTitle = dialog.findViewById<View>(R.id.mTitle) as TextView
        val mCancel = dialog.findViewById<View>(R.id.mCancel) as TextView
        val mJoin = dialog.findViewById<View>(R.id.mJoin) as TextView
        mJoin.setText(mContext!!.getString(R.string.confirm))
        if (confirmAffiliates.getData()!!.isConfirmed != null) {
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                //   like.setImageResource(R.drawable.hand_ico)
                likeCkeck.isChecked = true
            } else {
                likeCkeck.isChecked = false
                //  like.setImageResource(R.drawable.ic_inactive_hand_ico)
            }
        } else {
            likeLay.visibility = View.GONE
        }
        topIcon.setImageResource(R.drawable.ic_nav_event)
        topIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext!!, R.color.primaryColor)))
        //  topIcon.imageTintList=ContextCompat.getColor(mContext?,R.color.primaryColor)
        mTitle.setText(R.string.confirmTitle)


        activityUserName.text = userName
        if (!userImage.equals("")) {
            Glide.with(profileImage.context).load(userImage)/*.fitCenter()*/.into(profileImage)
        }
        //}
        dialog.setCancelable(true)
        dialog.show()
        likeLay.setOnClickListener(View.OnClickListener {
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                confirmAffiliates.getData()!!.isConfirmed = "0"
                //  like.setImageResource(R.drawable.ic_inactive_hand_ico)
                likeCkeck.isChecked = false
            } else {
                confirmAffiliates.getData()!!.isConfirmed = "1"
                //    like.setImageResource(R.drawable.hand_ico)
                likeCkeck.isChecked = true
            }
        })
        mCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        mJoin.setOnClickListener(View.OnClickListener {
            var mUserId: String = ""
            var affiliateId: String = ""
            if (confirmAffiliates.getData()!!.isConfirmed.equals("1")) {
                mUserId = userId
            }
            confirmAffiliates.getData()!!.affiliates!!
                    .asSequence()
                    .filter { it.isConfirmed.equals("1") }
                    .forEach {
                        affiliateId = if (affiliateId.equals("")) {
                            it.userAffiliateId!!
                        } else {
                            affiliateId + "," + it.userAffiliateId
                        }
                    }
            confirmActivity(activityId, affiliateId, activityEventId, mUserId, dialog, showSnack)
        })
    }

    internal fun popUpJoin(activityId: String, getJoinAffliates: GetJoinAffliates,clubId: String) {
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
        // val like = dialog.findViewById<View>(R.id.like) as ImageView
        val likeCkeck = dialog.findViewById<View>(R.id.likeCkeck) as CheckBox
        val dialogRecycler = dialog.findViewById<View>(R.id.dialogRecycler) as RecyclerView
        val adapter = JoinAffiliatesAdapter(mContext, getJoinAffliates.getData()!!.affiliates)
        dialogRecycler.adapter = adapter
        val activityUserName = dialog.findViewById<View>(R.id.activityUserName) as TextView
        val mTitle = dialog.findViewById<View>(R.id.mTitle) as TextView
        val mCancel = dialog.findViewById<View>(R.id.mCancel) as TextView
        val mJoin = dialog.findViewById<View>(R.id.mJoin) as TextView


        if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
            //    like.setImageResource(R.drawable.active_heart_ico)
            likeCkeck.isChecked = true
        } else {
            //  like.setImageResource(R.drawable.inactive_heart_ico)
            likeCkeck.isChecked = false
        }
        topIcon.setImageResource(R.drawable.ic_cards_heart_active)
        mTitle.setText(R.string.joinTitle)
        activityUserName.text = userName
        if (!userImage.equals("")) {
            Glide.with(profileImage.context).load(userImage)/*.fitCenter()*/.into(profileImage)
        }
        //}
        dialog.setCancelable(true)
        dialog.show()
        likeLay.setOnClickListener(View.OnClickListener {
            if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
                getJoinAffliates.getData()!!.isJoined = "0"
                // like.setImageResource(R.drawable.inactive_heart_ico)
                likeCkeck.isChecked = false
            } else {
                getJoinAffliates.getData()!!.isJoined = "1"
                //    like.setImageResource(R.drawable.active_heart_ico)
                likeCkeck.isChecked = true
            }
        })
        mCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        mJoin.setOnClickListener(View.OnClickListener {
            var mUserId: String = ""
            var affiliateId: String = ""
            if (getJoinAffliates.getData()!!.isJoined.equals("1")) {
                mUserId = userId
            }
            getJoinAffliates.getData()!!.affiliates!!
                    .asSequence()
                    .filter { it.isJoined.equals("1") }
                    .forEach {
                        affiliateId = if (affiliateId.equals("")) {
                            it.userAffiliateId!!
                        } else {
                            affiliateId + "," + it.userAffiliateId
                        }
                    }
            joinActivity(activityId, affiliateId, mUserId,clubId, dialog)
        })
    }


    /*internal fun popUpDateDetails(activitiesBean: ActivitiesBean.DataBean, eventBean: ActivitiesBean.DataBean.EventsBean?) {
        //    var isLike: Boolean = false;
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_date_details)
        dialog.window!!.setLayout(width * 10 / 11, WindowManager.LayoutParams.WRAP_CONTENT)
        //    dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        // dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        val activityTitle = dialog.findViewById<View>(R.id.activityTitle) as TextView
        val titleTxt = dialog.findViewById<View>(R.id.titleTxt) as TextView
        val timeTxt = dialog.findViewById<View>(R.id.timeTxt) as TextView
        val dateTxt = dialog.findViewById<View>(R.id.dateTxt) as TextView
        val locatonTxt = dialog.findViewById<View>(R.id.locatonTxt) as TextView
        val descTxt = dialog.findViewById<View>(R.id.descTxt) as TextView
        val affilitesChip = dialog.findViewById<View>(R.id.affilitesChip) as FlowLayout
        val statusTxt = dialog.findViewById<View>(R.id.statusTxt) as TextView
        val mCancel = dialog.findViewById<View>(R.id.mCancel) as TextView
        val mConfirm = dialog.findViewById<View>(R.id.mConfirm) as TextView
        val mClose = dialog.findViewById<View>(R.id.mClose) as TextView


        activityTitle.text = activitiesBean.activityName
        if (!eventBean?.event_title.isNullOrEmpty()) titleTxt.text = eventBean?.event_title
        if (!eventBean?.getTime().isNullOrEmpty()) timeTxt.text = eventBean?.getTime()
        if (!eventBean?.location.isNullOrEmpty()) locatonTxt.text = eventBean?.location
        if (!eventBean?.description.isNullOrEmpty()) descTxt.text = eventBean?.description
        if (!eventBean?.event_date.isNullOrEmpty()) dateTxt.text = eventBean?.event_date
        statusTxt.text = DateTimeUtil.getTimeAgo(stringToDate(now).time, stringToDate(eventBean?.event_date + " " + eventBean?.event_time).time, mContext, getResources().getString(R.string.date_left_to_confirm))
        confirmStatus = if (eventBean?.is_confirm.equals("0")) "1" else "0"
        if (TextUtils.isEmpty(eventBean?.confirm_userlist)) {
            addChip(affilitesChip, getString(R.string.a_notAvailable))
        } else {
            addChip(affilitesChip, eventBean?.confirm_userlist!!)
        }
        if (eventBean?.is_cancel.equals("1")) mCancel.visibility = View.GONE

        if (eventBean?.is_confirm.equals("1")) {
            mConfirm.setText(getString(R.string.un_confirm_date))
            mConfirm.setTextColor(ContextCompat.getColor(mContext!!, R.color.red_favroit))
        } else {
            mConfirm.setText(getString(R.string.confirm_date))
            mConfirm.setTextColor(ContextCompat.getColor(mContext!!, R.color.primaryColor))
        }
        mCancel.setOnClickListener(View.OnClickListener {
            showConfirmationCancelDialog(getString(R.string.cancel), activitiesBean.activityId!!,
                    eventBean?.activityEventId!!, dialog)
        })
        mConfirm.setOnClickListener(View.OnClickListener {
            showConfirmationCancelDialog(getString(R.string.confirm_date), activitiesBean.activityId!!,
                    eventBean?.activityEventId!!, dialog)
        })
        mClose.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.setCancelable(true)
        dialog.show()
    }*/

    private fun confirmActivity(activityId: String,
                                affiliateId: String,
                                activityEventId: String,
                                userId: String,
                                dialog1: Dialog,
                                showSnack: RelativeLayout) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.confirmActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    var msg = obj.getString("message")
                    if (obj.getString("status").equals("success")) {
                        dialog1.dismiss()
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
                    } else {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
                        //   Snackbar.make(showSnack, msg, Snackbar.LENGTH_LONG).show()
                        //  Util.showSnake(mContext!!,showSnack,1,msg)
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
                dialog.dismiss()
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
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun confirmMyActivity(activityId: String,
                                  activityEventId: String,
                                  userId: String,
                                  confirmStatus: String,
                                  dialog1: DialogInterface,
                                  showSnack: RelativeLayout) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.confirmMyActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    var msg = obj.getString("message")
                    if (obj.getString("status").equals("success")) {
                        dialog1.dismiss()
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
                    } else {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
                        //   Snackbar.make(showSnack, msg, Snackbar.LENGTH_LONG).show()
                        //  Util.showSnake(mContext!!,showSnack,1,msg)
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
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["activityId"] = activityId
                params["activityEventId"] = activityEventId
                params["userId"] = userId
                params["is_confirm"] = confirmStatus
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun joinActivity(activityId: String,
                             affiliateId: String,
                             userId: String,
                             clubId: String,
                             dialog1: Dialog? = null) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.joinActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog1?.dismiss()
                        pageListner?.resetState()
                        if (affiliateId.equals("") && userId.equals("")) {
                            joinActivityInFireBase(activityId,clubId, "remove")
                        } else {
                            joinActivityInFireBase(activityId,clubId, "join")
                        }
                        getAllActivitiesList(isPull = true)
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
                dialog.dismiss()
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
        }.execute(Frag_My_Activity::class.java.name)
    }

    /* private fun showConfirmationCancelDialog(action: String = "",
                                              activityId: String = "",
                                              activityEventId: String = "",
                                              dialog1: Dialog) {
         try {
             val builder1 = AlertDialog.Builder(mContext)
             builder1.setTitle(mContext!!.getString(R.string.alert))
             when (action) {
                 getString(R.string.cancel) -> {
                     builder1.setMessage(getString(R.string.activity_date_cancel))
                 }
                 getString(R.string.confirm) -> {
                      if (confirmStatus.equals("1")) {
                          builder1.setMessage(getString(R.string.activity_date_confirm))
                      } else {
                          builder1.setMessage(getString(R.string.activity_date_unconfirm))
                      }
                 }
             }
             builder1.setCancelable(false)
             builder1.setPositiveButton(mContext!!.getString(R.string.ok), { dialog, id ->
                 dialog1.dismiss()
                 when (action) {
                     getString(R.string.cancel) -> {
                         cancelDate(activityId, activityEventId, dialog)
                     }
                     getString(R.string.confirm) -> {
                         confirmMyActivity(activityId, activityEventId, userId, confirmStatus, dialog, snakLay)
                     }
                 }

             })

             builder1.setNegativeButton(mContext!!.getString(R.string.cancel),
                     DialogInterface.OnClickListener { dialog, id ->
                         dialog1.dismiss()
                         dialog.cancel()
                     })
             *//*       builder1.setOnShowListener( new OnShowListener() {
           @Override
           public void onShow(DialogInterface arg0) {
               dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_I_WANT);
           }
       });*//*
            val alert11 = builder1.create()
            alert11.show()
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext!!, R.color.nav_gray))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    /*private fun cancelDate(activityId: String = "", activityEventId: String = "",
                           dialog1: DialogInterface) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.cancelActivityDate}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog1?.dismiss()
                        pageListner?.resetState()
                        getAllActivitiesList(isPull = true)
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
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun addChip(chipHolder: FlowLayout, str: String) {
        if (str.isNotBlank()) {
            val tagList = str.split(",").map { it.trim() }
            for (tag in tagList) {
                val chip = object : ChipView(mContext, chipHolder.childCount.toString(), false) {
                    override fun getLayout(): Int {
                        return R.layout.z_cus_chip_view_confirm_user
                    }

                    override fun setDeleteListner(chipView: ChipView?) {
                    }
                }
                chip.text = tag
                chipHolder.addView(chip)
            }
        }
    }*/

    fun doFilter() {
        activityList.clear()
        activitiesAdapter?.notifyDataSetChanged()
        if (isMyActivity) {
            isMyActivity = false
            activityList.addAll(tempuraryActivityList)
        } else {
            isMyActivity = true
            for (dataBean in tempuraryActivityList) {
                if (dataBean.is_my_activity.equals("1")) activityList.add(dataBean)
            }
        }
        nodataLay.visibility = if (activityList.isEmpty()) View.VISIBLE else View.GONE
        activitiesAdapter?.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Frag_My_Activity().apply {
                    arguments = Bundle().apply {
                        /*putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)*/
                    }
                }
    }

    /*private fun stringToDate(string: String): Date {
        // yyyy-mm-dd hh:mm:ss
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val myDate = simpleDateFormat.parse(string)
        return myDate
    }*/

    fun onSwitchClub() {
        getAllActivitiesList(isPull = true)
    }

    private fun joinActivityInFireBase(activityId: String,clubId: String, status: String) {
        if (status.equals("join")) {
            FirebaseDatabase.getInstance()
                    .reference
                    .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                    .child(activityId)
                    .child(userId).setValue(userId).addOnCompleteListener {
                        val msg = ClubZ.currentUser!!.full_name+" has joined"
                        sendMessage(ChatUtil.ARG_ACTIVITY_JOIND,msg,clubId,activityId)
                    }
        } else {
            FirebaseDatabase.getInstance()
                    .reference
                    .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                    .child(activityId)
                    .child(userId).setValue(null).addOnCompleteListener {
                        val msg = ClubZ.currentUser!!.full_name+" has removed"
                        sendMessage(ChatUtil.ARG_ACTIVITY_REMOVE,msg,clubId,activityId)
                    }
        }
    }

    private fun sendMessage(chatType:String,msg:String,clubId:String,activityId:String) {
        var chatRoom = clubId + "_" + activityId + "_" + ChatUtil.ARG_ACTIVITIES

        //    var msg = ClubZ.currentUser!!.first_name+"has joined"

        //   Constant.hideSoftKeyboard(ChatActivity.this);
        val chatBean = ChatBean()
        chatBean.deleteby = ""
        chatBean.chatType = chatType
        chatBean.message = msg

        chatBean.senderId = ClubZ.currentUser?.id
        chatBean.senderName = ClubZ.currentUser?.full_name
        chatBean.timestamp = ServerValue.TIMESTAMP
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(chatRoom)) {
                    Log.e("TAG", "sendMessageToFirebaseUser: $chatRoom exists")
                    val gen_key = databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).child(chatRoom).ref.push()
                    gen_key.setValue(chatBean)
                } else {
                    Log.e("TAG", "sendMessageToFirebaseUser: success")
                    val gen_key = databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).child(chatRoom).ref.push()
                    gen_key.setValue(chatBean)
                    // getMessageFromFirebaseUser(mUid, rcvUId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //   mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        })
    }
}
