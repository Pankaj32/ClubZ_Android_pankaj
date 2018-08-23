package com.clubz.ui.user_activities.fragment

import android.annotation.SuppressLint
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.data.remote.WebService
import com.clubz.ui.activities.fragment.ItemListDialogFragment
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.ui.user_activities.activity.ActivitiesDetails
import com.clubz.ui.user_activities.adapter.*
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner
import com.clubz.ui.user_activities.model.*
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_add_events.*
import kotlinx.android.synthetic.main.frag_my_activity.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Frag_My_Activity : Fragment(), ActivityItemClickListioner, ItemListDialogFragment.Listener, SwipeRefreshLayout.OnRefreshListener {
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
    private var allActivitiesAdapter: AllActivitiesAdapter? = null
    private var activitiesAdapter: ActivitiesAdapter? = null
    private val allActivityList = ArrayList<AllActivitiesBean.DataBean>()
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
    private var hasAffliates: Int = 0

    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    private var confirmStatus: String = ""
    var isMyActivity: Boolean = false

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
        swiperefresh.setOnRefreshListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewMyActivities.itemAnimator = null
        recyclerViewMyActivities.layoutManager = lm
        recyclerViewMyActivities.setHasFixedSize(true)

        nodataLay.visibility = if (activityList.isEmpty()) View.VISIBLE else View.GONE
        activitiesAdapter = ActivitiesAdapter(mContext, activityList, this)
        recyclerViewMyActivities.adapter = activitiesAdapter

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getAllActivitiesList(offset = page * 10)
            }
        }
        recyclerViewMyActivities.addOnScrollListener(pageListner)
        getAllActivitiesList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context;
    }

    override fun onDetach() {
        super.onDetach()
    }


    fun getAllActivitiesList(listType: String = "", limit: String = "10", offset: Int = 0, isPull: Boolean? = false) {
        val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing) dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext,
                "${WebService.get_all_activity_list}?listType=${listType}&offset=${offset}&limit=${limit}" +
                        "&date=${Util.getCurrentDate()}&time=${Util.getCurrentTime()}", true)
        //WebService.get_activity_list + listType + "&limit=&offset=",
        {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val activityBean: ActivitiesBean = Gson().fromJson(response, ActivitiesBean::class.java)
                        hasAffliates = activityBean.hasAffiliates!!
                        updateAllUiOthers(activityBean, isPull)
                    } else {
                        nodataLay.visibility = View.VISIBLE
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

    private fun updateAllUiOthers(activityBean: ActivitiesBean, pull: Boolean?) {
        activityList.clear()
        if (pull!!) {
            tempuraryActivityList.clear()
            activitiesAdapter?.notifyDataSetChanged()
        }
        for (dataBean in activityBean.data!!) {
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
        } else {
            if (activity.is_like.equals("0")) {
                list.add(DialogMenu(getString(R.string.join_activity), R.drawable.ic_cards_heart))
            } else {
                list.add(DialogMenu(getString(R.string.leave_activity), R.drawable.ic_cards_heart))
            }

        }

        val a = ItemListDialogFragment()
        a.setInstance(this, list)
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
        //other

        if (activitiesBean.is_my_activity.equals("1")) {
            startActivity(Intent(mContext, ActivitiesDetails::class.java)
                    .putExtra("activityId", activityId)
                    .putExtra("activityName", activityName)
                    .putExtra("clubName", clubName)
                    .putExtra("From", "MyActivities")
                    .putExtra("type", "my")
                    .putExtra("hasAffliates", hasAffliates)
            )
        } else {
            userId = activitiesBean.userId!!
            userName = activitiesBean.full_name!!
            userProfileImg = activitiesBean.profile_image!!
            startActivity(Intent(mContext, ActivitiesDetails::class.java)
                    .putExtra("activityId", activityId)
                    .putExtra("From", "OthersActivity")
                    .putExtra("userId", userId)
                    .putExtra("userName", userName)
                    .putExtra("userProfileImg", userProfileImg)
                    .putExtra("activityName", activityName)
                    .putExtra("clubName", clubName)
                    .putExtra("type", "others")
                    .putExtra("hasAffliates", hasAffliates)
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

    //bottomsheet
    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> {
                val activities = activityList[actionPosition!!]
                if (activities.is_my_activity.equals("1")) {
                    popUpAddEvents(activities)
                } else {
                    if (hasAffliates == 1) {
                        getUserJoinAfiliatesList(activities.activityId!!)
                    } else {
                        joinActivity(activities.activityId!!, "", userId)
                    }
                }
            }
            1 -> {
                showConfirmationDialog(action = "remove")
            }
            2 -> {
                showConfirmationDialog(action = hideUnhide!!)
            }
            /*R.id.disableNotification -> {
                return true
            }*/
        }
    }

    var dialog: Dialog? = null
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
            val locationTxt = dialog!!.findViewById<View>(R.id.locationTxt) as TextView
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
            loctionLay.setOnClickListener {
                hideDialogKeyBoard()
                openAutocompleteActivity()
            }

            dialog!!.setOnDismissListener { dialogInterface ->
                dialog = null
            }
        }
        dialog?.show()
    }

    private fun hideDialogKeyBoard() {
        val inputManager = dialog!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(dialog!!.currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

    private fun showConfirmationDialog(action: String = "", activityId: String = "", activityEventId: String = "") {
        try {
            val builder1 = AlertDialog.Builder(mContext)
            builder1.setTitle("Alert")
            if (action.equals("confirm")) {
                var status = if (confirmStatus.equals("1")) "confirm" else "deny"
                builder1.setMessage("Are you sure you want to $status this Date?")
            } else {
                builder1.setMessage("Are you sure you want to $action this activity?")
            }
            builder1.setCancelable(false)
            builder1.setPositiveButton("Ok", { dialog, id ->
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

            builder1.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val alert11 = builder1.create()
            alert11.show()
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
                false) {
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
                false) {
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

    private fun datePicker(i1: Int, i2: Int, i3: Int, addDateTxt: TextView) {
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
                //val onTimeSet: Unit
                eventTime = "$p1:$p2"
                addTimeTxt.text = Util.setTimeFormat(eventTime)
            }
        }, hour, minute, false)
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

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

                dialog?.locationTxt?.setText(place.address)
                dialog?.show()
                Log.e("TAG", "Place Selected: " + place.name + " " + latitude + " " + longitute)
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

    private fun addEvent(eventTitle: String,
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
    }


    fun getUserConfirmAfiliatesList(activityId: String, activityEventId: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserConfirmAffiliates}?userId=${userId}&activityEventId=${activityEventId}&activityId=${activityId}",


                true) {
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

    private fun getUserJoinAfiliatesList(activityId: String) {
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
        mJoin.setText("Confirm")
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
            Picasso.with(profileImage.context).load(userImage).fit().into(profileImage)
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
        topIcon.setImageResource(R.drawable.active_heart_ico)
        mTitle.setText(R.string.joinTitle)
        activityUserName.text = userName
        if (!userImage.equals("")) {
            Picasso.with(profileImage.context).load(userImage).fit().into(profileImage)
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
            joinActivity(activityId, affiliateId, mUserId, dialog)
        })
    }

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
                false) {
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
                false) {
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
        }.execute(Frag_Find_Activities::class.java.name)
    }

    private fun joinActivity(activityId: String,
                             affiliateId: String,
                             userId: String,
                             dialog1: Dialog? = null) {
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

}
