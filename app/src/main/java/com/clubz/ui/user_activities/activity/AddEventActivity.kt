package com.clubz.ui.user_activities.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.VolleyError
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.model.ActivitiesBean
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.activity_add_event.*
import org.json.JSONObject
import java.util.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager


class AddEventActivity : AppCompatActivity() {
    var activities: ActivitiesBean.DataBean? = null
    var day = -1
    var month = -1
    var year = -1
    var eventDate: String = ""
    var eventTime: String = ""
    var latitude = ""
    var longitute = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        intent?.let {
            activities = it.extras.getParcelable("activity") as ActivitiesBean.DataBean
                fees.setText(activities!!.fee)
        }
        mCancel.setOnClickListener({ finish() })
        mAdd.setOnClickListener({
            val eventTitle = eventNameTxt.text.toString().trim()
            val location = locationTxt.text.toString().trim()
            val dese = descTxt.text.toString().trim()
            if (isvaildDataToSend(eventNameTxt, addDateTxt, addTimeTxt, locationTxt, containerLay)) addEvent(eventTitle, eventDate, eventTime, location, activities!!.activityId!!, dese)
        })
        datelay.setOnClickListener {
            KeyboardUtil.hideKeyboard(this)
            datePicker(day, month, year, addDateTxt)
        }
        timeLay.setOnClickListener {
            KeyboardUtil.hideKeyboard(this)
            timePicker(addTimeTxt)
        }
        back_f.setOnClickListener {
            finish()
        }
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
        val datepickerdialog = DatePickerDialog(this, R.style.DialogTheme2, object : DatePickerDialog.OnDateSetListener {
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
        val mTimePicker = TimePickerDialog(this, R.style.DialogTheme2, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                val hr = if (p1 < 10) "0$p1" else "$p1"
                val min = if (p2 < 10) "0$p2" else "$p2"
                //val onTimeSet: Unit
                eventTime = "$hr:$min"
                addTimeTxt.text = Util.setTimeFormat(eventTime)
            }
        }, hour, minute, false)
        mTimePicker.setTitle(getString(R.string.selectTime))
        mTimePicker.show()
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

    private fun addEvent(eventTitle: String,
                         eventDate: String,
                         eventTime: String,
                         location: String,
                         activityId: String,
                         description: String) {
        val dialogProgress = CusDialogProg(this)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this,
                WebService.addEvents,
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false,true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        finish()
                    } else {

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
        }.execute(AddEventActivity::class.java.name)
    }
}
