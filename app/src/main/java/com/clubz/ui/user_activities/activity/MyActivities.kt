package com.clubz.ui.user_activities.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.View
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
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.clubz.utils.Util
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.dialog_add_events.*
import java.util.*


class MyActivities : AppCompatActivity(), ParentViewClickListioner, ChildViewClickListioner, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private var todayAdapter: MyActivitiesCategoryAdapter? = null
    private var isTodayOpen: Boolean = false
    private var isTomorrowOpen: Boolean = false
    private var isSoonOpen: Boolean = false
    private val INITIAL_POSITION = 0.0f
    private val ROTATED_POSITION = 180f
    private var todayList: List<GetMyactivitiesResponce.DataBean>? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_activities)
        recyclerViewMyActivities.layoutManager = LinearLayoutManager(this@MyActivities)
        back_f.setOnClickListener(this@MyActivities)
        addActivity.setOnClickListener(this@MyActivities)
        //   val display = getWindowManager().getDefaultDisplay()

        val diametric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(diametric)
        width = diametric.widthPixels
        height = diametric.heightPixels
    }

    override fun onResume() {
        super.onResume()
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
        nodataLay.visibility = if (todayList!!.size == 0) View.VISIBLE else View.GONE
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
        actionPosition = position
        showPopup(itemMenu, position)
    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(this@MyActivities, ActivitiesDetails::class.java))
    }

    override fun onItemLike(position: Int, type: String) {

    }

    override fun onItemChat(position: Int) {

    }

    override fun onItemJoin(position: Int, type: String) {

    }

    override fun onJoin(parentPosition: Int, childPosition: Int, type: String) {
        /*Toast.makeText(this@MyActivities, "parent " + parentPosition + " child " + childPosition, Toast.LENGTH_SHORT).show()
        Log.e("parent " + parentPosition + " child " + childPosition, "hh")*/
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.back_f -> {
                finish()
            }
            R.id.addActivity -> {
                startActivity(Intent(this@MyActivities, NewActivities::class.java))
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun showPopup(v: View, position: Int) {
        var activity = todayList!![actionPosition!!]
        val wrapper = ContextThemeWrapper(this, R.style.MyPopupMenu)
        val popup = PopupMenu(wrapper, v)
        val inflater = popup.getMenuInflater()
        inflater.inflate(R.menu.my_activities_menu, popup.getMenu())

        val hideActItem = popup.menu.findItem(R.id.hideAct)
        if (activity.is_hide.equals("0")) {
            hideActItem.title = "Hide this activity"
            hideActItem.icon = ContextCompat.getDrawable(this@MyActivities, R.drawable.ic_visibility_off)
            hideUnhide = "hide"
        } else {
            hideActItem.title = "Unhide this activity"
            hideActItem.icon = ContextCompat.getDrawable(this@MyActivities, R.drawable.ic_visibility)
            hideUnhide = "unhide"
        }
        val menuHelper = MenuPopupHelper(this@MyActivities, popup.menu as MenuBuilder, v)
        menuHelper.setForceShowIcon(true)

        popup.setOnMenuItemClickListener(this@MyActivities);
        menuHelper.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.addDate -> {
                var activities = todayList!![actionPosition!!]
                popUpAddEvents(activities)
                return true
            }
            R.id.removeAct -> {
                showConfirmationDialog("remove")
                return true
            }
            R.id.hideAct -> {
                showConfirmationDialog(hideUnhide!!)
                return true
            }
            R.id.disableNotification -> {
                return true
            }
            else -> return false
        }
    }

    fun showConfirmationDialog(action: String) {

        try {
            val builder1 = AlertDialog.Builder(this@MyActivities)
            builder1.setTitle("Alert")
            builder1.setMessage("Are you sure you want to " + action + " this activity?")
            builder1.setCancelable(false)
            builder1.setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        when (action) {
                            "remove" -> {
                                deleteMyActivity(todayList!!.get(actionPosition!!)!!.activityId!!)
                            }
                            "hide",
                            "unhide" -> {
                                hideMyActivity(todayList!!.get(actionPosition!!)!!.activityId!!)
                            }

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

    fun deleteMyActivity(activityId: String) {
        val dialog = CusDialogProg(this@MyActivities)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@MyActivities, this@MyActivities,
                "${WebService.deleteMyActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        getActivitiesList()
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
                params.put("activityId", activityId)

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(MyActivities::class.java.name)
    }

    fun hideMyActivity(activityId: String) {
        val dialog = CusDialogProg(this@MyActivities)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@MyActivities, this@MyActivities,
                "${WebService.hideMyActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        getActivitiesList()
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
                params.put("activityId", activityId)

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(MyActivities::class.java.name)
    }

    var dialog: Dialog? = null
    private fun popUpAddEvents(activities: GetMyactivitiesResponce.DataBean) {
        if (dialog == null) {
            dialog = Dialog(this@MyActivities)
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

            var latitute: String = ""
            var longitute: String = ""
            mTitle.text = activities.activityName

            dialog!!.setCancelable(false)
            mCancel.setOnClickListener(View.OnClickListener { dialog!!.dismiss() })
            mAdd.setOnClickListener(View.OnClickListener {
                var eventTitle = eventNameTxt.text.toString().trim()
                var location = locationTxt.text.toString().trim()
                var dese = descTxt.text.toString().trim()
                if (isvaildDataToSend(eventNameTxt, addDateTxt, addTimeTxt, locationTxt, containerLay)) addEvent(eventTitle, eventDate, eventTime, location, activities.activityId!!, dese)
            })
            datelay.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    datePicker(day, month, year, addDateTxt)
                }
            })
            timeLay.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    timePicker(addTimeTxt)
                }
            })
            locationTxt.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    openAutocompleteActivity()
                }
            })

            dialog!!.setOnDismissListener { dialogInterface ->
                dialog = null
            }
        }

        dialog?.show()
    }


    private fun isvaildDataToSend(eventNameTxt: EditText, addDateTxt: TextView, addTimeTxt: TextView, locationTxt: TextView, containerLay: RelativeLayout): Boolean {
        if (eventNameTxt.text.toString().isBlank()) {
            Util.showSnake(this, containerLay, R.string.a_EventName)
            eventNameTxt.requestFocus()
            return false
        } else if (addDateTxt.text.toString().isBlank()) {
            Util.showSnake(this, containerLay, R.string.a_date)
            return false
        } else if (addTimeTxt.text.toString().isBlank()) {
            Util.showSnake(this, containerLay, R.string.a_time)
            return false
        } else if (locationTxt.text.toString().isBlank()) {
            Util.showSnake(this, containerLay, R.string.a_location_dl)
            return false
        }
        return true
    }

    fun datePicker(i1: Int, i2: Int, i3: Int, addDateTxt: TextView) {
        val calendar = GregorianCalendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DATE)
        if (i1 != -1) {
            day = i1
            month = i2 - 1
            year = i3
        }
        val datepickerdialog = DatePickerDialog(this@MyActivities, R.style.DialogTheme2, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                val check = Date()
                check.year = p1 - 1900; check.month; check.date = p3
                var d = Date(System.currentTimeMillis() - 1000)
                d.hours = 0
                d.minutes = 0
                d.seconds = 0
                Util.e("Tag", "$d : ${p0!!.minDate} : $check")
                year = p1; month = p2 + 1;day = p3
                eventDate = "" + year + "/" + month + "/" + day
                addDateTxt.setText(Util.convertDate("$year-$month-$day"))
            }
        }, year, month, day)
        // datepickerdialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datepickerdialog.window!!.setBackgroundDrawableResource(R.color.white)

        datepickerdialog.show()
    }

    fun timePicker(addTimeTxt: TextView) {
        var mcurrentTime = Calendar.getInstance()
        var hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        var minute = mcurrentTime.get(Calendar.MINUTE)
        var mTimePicker = TimePickerDialog(this@MyActivities, R.style.DialogTheme2, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                val onTimeSet: Unit
                eventTime = "" + p1 + ":" + p2
                addTimeTxt.text = Util.setTimeFormat(eventTime)
            }
        }, hour, minute, false)
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    fun addEvent(eventTitle: String,
                 eventDate: String,
                 eventTime: String,
                 location: String,
                 activityId: String,
                 description: String) {
        val dialogProgress = CusDialogProg(this@MyActivities)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@MyActivities, this@MyActivities,
                "${WebService.addEvents}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog?.dismiss()
                        getActivitiesList()
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
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(MyActivities::class.java.name)
    }

    private fun openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            val message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode)

            Log.e("TAG", message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                val place = PlaceAutocomplete.getPlace(this, data)
                latitude = place.latLng.latitude.toString()
                longitute = place.latLng.longitude.toString()

                dialog?.locationTxt?.text = place.address
                dialog?.show()
                Log.e("TAG", "Place Selected: " + place.getName() + " " + latitude + " " + longitute);
            }
        }
    }
}
