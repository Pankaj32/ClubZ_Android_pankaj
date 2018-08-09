package com.clubz.ui.user_activities.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import com.android.volley.VolleyError

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.data.remote.WebService
import com.clubz.ui.activities.fragment.ItemListDialogFragment
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.activity.ActivitiesDetails
import com.clubz.ui.user_activities.adapter.MyActivitiesAdapter
import com.clubz.ui.user_activities.listioner.ActivityItemClickListioner
import com.clubz.ui.user_activities.model.GetMyactivitiesResponce
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_add_events.*
import kotlinx.android.synthetic.main.frag_my_activity.*
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match


class Frag_My_Activity : Fragment(), ActivityItemClickListioner, ItemListDialogFragment.Listener {

    // TODO: Rename and change types of parameters

    private var todayAdapter: MyActivitiesAdapter? = null
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
    var mContext: Context? = null


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
    }

    override fun onResume() {
        super.onResume()
        getActivitiesList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context;
    }

    override fun onDetach() {
        super.onDetach()
    }

    fun getActivitiesList(listType: String = "", limit: String = "", offset: String = "") {
        val dialog = CusDialogProg(mContext)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext,
                "${WebService.get_my_activity_list}?listType=${listType}&offset=${offset}&limit=${limit}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val myActivitiesResponce: GetMyactivitiesResponce = Gson().fromJson(response, GetMyactivitiesResponce::class.java)
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
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
    }

    private fun updateUi(myActivitiesResponce: GetMyactivitiesResponce) {
        for (i in 0 until myActivitiesResponce.getData()!!.size) {
            val todayData = myActivitiesResponce.getData()!![i]
            for (j in 0 until todayData.events!!.size) {
                val eventData = todayData.events!![j]
                eventData.childIndex = j
                eventData.parentIndex = i
                if (eventData.is_confirm == "1") todayData.is_Confirm = true
            }
        }
        todayList = myActivitiesResponce.getData()
        nodataLay.visibility = if (todayList!!.isEmpty()) View.VISIBLE else View.GONE
        todayAdapter = MyActivitiesAdapter(mContext, todayList, this)
        recyclerViewMyActivities.adapter = todayAdapter
    }

    override fun onLongPress(type: String, activityPosition: Int) {
        actionPosition = activityPosition
        val activity = todayList!![actionPosition!!]
        val list: ArrayList<DialogMenu> = arrayListOf()
        list.add(DialogMenu(getString(R.string.add_date), R.drawable.ic_add_24))
        list.add(DialogMenu(getString(R.string.remove_activity), R.drawable.ic_delete_icon))
        if (activity.is_hide.equals("0")) {
            list.add(DialogMenu(getString(R.string.hide_activity), R.drawable.ic_visibility_off))
            hideUnhide = "hide"
        } else {
            list.add(DialogMenu(getString(R.string.un_hide_activity), R.drawable.ic_visibility))
            hideUnhide = "unhide"
        }
        val a = ItemListDialogFragment()
        a.setInstance(this, list)
        a.show(fragmentManager, "draj")
        //ItemListDialogFragment.newInstance(list).show(fragmentManager, "draj")
    }

    override fun onItemClick(type: String, activityPosition: Int) {
        startActivity(Intent(mContext, ActivitiesDetails::class.java).putExtra("activityId", todayList!![activityPosition].activityId).putExtra("activityName", todayList!![activityPosition].activityName).putExtra("clubName", todayList!![activityPosition].club_name)
                .putExtra("From", "MyActivities"))
    }

    override fun onJoinClick(type: String, activityPosition: Int) {

    }

    override fun onConfirm(type: String, activityPosition: Int, eventPosition: Int) {

    }

    //bottomsheet
    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> {
                val activities = todayList!![actionPosition!!]
                popUpAddEvents(activities)
            }
            1 -> {
                showConfirmationDialog("remove")
            }
            2 -> {
                showConfirmationDialog(hideUnhide!!)
            }
        /*R.id.disableNotification -> {
            return true
        }*/
        }
    }

    var dialog: Dialog? = null
    private fun popUpAddEvents(activities: GetMyactivitiesResponce.DataBean) {
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
            datelay.setOnClickListener { datePicker(day, month, year, addDateTxt) }
            timeLay.setOnClickListener { timePicker(addTimeTxt) }
            loctionLay.setOnClickListener { openAutocompleteActivity() }

            dialog!!.setOnDismissListener { dialogInterface ->
                dialog = null
            }
        }
        dialog?.show()
    }

    private fun showConfirmationDialog(action: String) {
        try {
            val builder1 = AlertDialog.Builder(mContext)
            builder1.setTitle("Alert")
            builder1.setMessage("Are you sure you want to $action this activity?")
            builder1.setCancelable(false)
            builder1.setPositiveButton("Ok", { dialog, id ->
                when (action) {
                    "remove" -> {
                        deleteMyActivity(todayList!![actionPosition!!].activityId!!)
                    }
                    "hide",
                    "unhide" -> {
                        hideMyActivity(todayList!![actionPosition!!].activityId!!)
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
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }.execute(Frag_My_Activity::class.java.name)
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
