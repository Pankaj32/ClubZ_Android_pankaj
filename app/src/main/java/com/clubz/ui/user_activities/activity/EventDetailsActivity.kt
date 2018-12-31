package com.clubz.ui.user_activities.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.ActivityBean
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.FlowLayout
import com.clubz.ui.user_activities.model.ActivitiesBean
import com.clubz.utils.DateTimeUtil
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import kotlinx.android.synthetic.main.activity_event_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsActivity : AppCompatActivity() {
    private var activitiesBean: ActivitiesBean.DataBean? = null
    private var eventBean: ActivitiesBean.DataBean.EventsBean? = null
    private var now = ""
    private var confirmStatus = ""
    private var isMyactivty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        intent?.let {
            activitiesBean = it.extras.getParcelable("activity") as ActivitiesBean.DataBean
            eventBean = it.extras.getParcelable("event") as ActivitiesBean.DataBean.EventsBean
            now = it.extras.getString("now")
            isMyactivty = it.extras.getBoolean("ismyactivity")
        }

        activityTitle.text = activitiesBean?.activityName
        if (!eventBean?.event_title.isNullOrEmpty()) titleTxt.text = eventBean?.event_title
        if (!eventBean?.getTime().isNullOrEmpty()) timeTxt.text = eventBean?.getTime()
        if (!eventBean?.location.isNullOrEmpty()) locatonTxt.text = eventBean?.location
        if (!eventBean?.description.isNullOrEmpty()) descTxt.text = eventBean?.description
        if (!eventBean?.event_date.isNullOrEmpty()) dateTxt.text = Util.convertDate(eventBean?.event_date!!)
        statusTxt.text = DateTimeUtil.getTimeAgo(stringToDate(now).time, stringToDate(eventBean?.event_date + " " + eventBean?.event_time).time, this, getResources().getString(R.string.date_left_to_confirm))
        confirmStatus = if (eventBean?.is_confirm.equals("0")) "1" else "0"
        if (TextUtils.isEmpty(eventBean?.confirm_userlist)) {
            addChip(affilitesChip, getString(R.string.a_notAvailable))
        } else {
            addChip(affilitesChip, eventBean?.confirm_userlist!!)
        }
        if (eventBean?.is_cancel.equals("1")) {
            mCancel.visibility = View.GONE
            mConfirm.visibility = View.GONE
            statusTxt.text = getString(R.string.date_cancelled)
            statusTxt.setTextColor(resources.getColor(R.color.red_favroit))
             }
        if (eventBean?.is_confirm.equals("1")) {
            mConfirm.setText(getString(R.string.un_confirm_date))
            mConfirm.setTextColor(ContextCompat.getColor(this, R.color.red_favroit))
        } else {
            mConfirm.setText(getString(R.string.confirm_date))
            mConfirm.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))
        }
        mCancel.setOnClickListener(View.OnClickListener {
            showConfirmationCancelDialog(getString(R.string.cancel), activitiesBean!!.activityId!!,
                    eventBean?.activityEventId!!)
        })
        mConfirm.setOnClickListener(View.OnClickListener {
            showConfirmationCancelDialog(getString(R.string.confirm_date), activitiesBean!!.activityId!!,
                    eventBean?.activityEventId!!)
        })
        mClose.setOnClickListener(View.OnClickListener { finish() })
        back_f.setOnClickListener({
            finish()
        })

        if(!isMyactivty){
            mCancel.visibility = View.GONE
            mConfirm.visibility = View.GONE
            confirmUserLay.visibility = View.GONE
        }
    }

    private fun addChip(chipHolder: FlowLayout, str: String) {
        if (str.isNotBlank()) {
            val tagList = str.split(",").map { it.trim() }
            for (tag in tagList) {
                val chip = object : ChipView(this, chipHolder.childCount.toString(), false) {
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
    }

    private fun showConfirmationCancelDialog(action: String = "",
                                             activityId: String = "",
                                             activityEventId: String = "") {
        try {
            val builder1 = AlertDialog.Builder(this)
            builder1.setTitle(getString(R.string.alert))
            when (action) {
                getString(R.string.cancel) -> {
                    builder1.setMessage(getString(R.string.activity_date_cancel))
                }
                getString(R.string.confirm_date) -> {
                    if (confirmStatus.equals("1")) {
                        builder1.setMessage(getString(R.string.activity_date_confirm))
                    } else {
                        builder1.setMessage(getString(R.string.activity_date_unconfirm))
                    }
                }
            }
            builder1.setCancelable(false)
            builder1.setPositiveButton(getString(R.string.ok), { dialog, id ->
                when (action) {
                    getString(R.string.cancel) -> {
                        cancelDate(activityId, activityEventId, dialog)
                    }
                    getString(R.string.confirm_date) -> {
                        confirmMyActivity(activityId, activityEventId, ClubZ.currentUser!!.id, confirmStatus, dialog, containerLay)
                    }
                }

            })

            builder1.setNegativeButton(getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            /*       builder1.setOnShowListener( new OnShowListener() {
           @Override
           public void onShow(DialogInterface arg0) {
               dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_I_WANT);
           }
       });*/
            val alert11 = builder1.create()
            alert11.show()
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@EventDetailsActivity, R.color.nav_gray))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelDate(activityId: String = "", activityEventId: String = "",
                           dialog1: DialogInterface) {
        val dialog = CusDialogProg(this)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@EventDetailsActivity, this,
                "${WebService.cancelActivityDate}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialog1?.dismiss()
                        finish()
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
        }.execute(EventDetailsActivity::class.java.name)
    }

    private fun confirmMyActivity(activityId: String,
                                  activityEventId: String,
                                  userId: String,
                                  confirmStatus: String,
                                  dialog1: DialogInterface,
                                  showSnack: RelativeLayout) {
        val dialog = CusDialogProg(this)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(this@EventDetailsActivity, this,
                "${WebService.confirmMyActivity}",
                //WebService.get_activity_list + listType + "&limit=&offset=",
                false, true) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                try {

                    val obj = JSONObject(response)
                    val msg = obj.getString("message")
                    if (obj.getString("status").equals("success")) {
                        dialog1.dismiss()
                        finish()
                    } else {
                        Toast.makeText(this@EventDetailsActivity, msg, Toast.LENGTH_LONG).show()
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
        }.execute(EventDetailsActivity::class.java.name)
    }

    private fun stringToDate(string: String): Date {
        // yyyy-mm-dd hh:mm:ss
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      //  simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val myDate = simpleDateFormat.parse(string)
        return myDate
    }
}
