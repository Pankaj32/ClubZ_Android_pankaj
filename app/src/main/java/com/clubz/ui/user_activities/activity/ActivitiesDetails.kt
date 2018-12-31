package com.clubz.ui.user_activities.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.Window
import com.android.volley.VolleyError
import com.clubz.R
import com.clubz.chat.fragments.FragmentChat
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.data.remote.WebService
import com.clubz.helper.fcm.NotificatioKeyUtil
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.fragment.FragActivityDetailsNew
import com.clubz.ui.user_activities.fragment.Frag_Activity_Member
import com.clubz.ui.user_activities.model.ActivitiesBean
import com.clubz.ui.user_activities.model.GetActivityDetailsResponce
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activities_details.*
import kotlinx.android.synthetic.main.club_more_menu.*
import org.json.JSONObject

class ActivitiesDetails : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    lateinit var adapter: ViewPagerAdapter
    private var activityId = ""
    private var from = ""
    private var type = ""
    private var userId = ""
    private var userName = ""
    private var userProfileImg = ""
    private var activityName = ""
    private var clubName = ""
    private var clubId = ""

    private var activityBean: ActivitiesBean.DataBean? = null
    protected var menuDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activities_details)
        val bundle = intent.extras
        if (bundle != null) {
                try {
                    from = bundle.getString(NotificatioKeyUtil.Key_From)
                }catch (e:Exception){
                    from= NotificatioKeyUtil.Value_From_Notification
                }

                if(from.equals(NotificatioKeyUtil.Value_From_Notification)){
                    val activityId=bundle.getString(NotificatioKeyUtil.Key_Activity_Id)
                    getActivityDetails(activityId)
                }else {
                    activityBean = bundle.getParcelable("activityBean")
                   // from = bundle.getString("From")
                    type = bundle.getString("type")

                    activityId = activityBean!!.activityId!!
                    activityName = activityBean!!.activityName!!
                    clubName = activityBean!!.club_name!!
                    clubId = activityBean!!.clubId!!

                    headerTxt.text = activityName
                    clubNameTxt.text = clubName
                    setViewPager(viewPager)
                }
        }

        if (from.equals("OthersActivity")) {
            /*userId = activityBean!!.userId!!
            userName = activityBean!!.full_name!!*/
            /* activityId = bundle.getString("activityId")
             activityName = bundle.getString("activityName")
             clubName = bundle.getString("clubName")*/
            /* userProfileImg = activityBean!!.profile_image!!*/
            bubble_menu.visibility = View.GONE
        }
        // headerTxt.text = resources.getString(R.string.hint_activity_name)
        headerTxt.text = activityName
        clubNameTxt.text = clubName
        bubble_menu.visibility = View.GONE

        ivBack.setOnClickListener(this)
        bubble_menu.setOnClickListener(this)

        tablayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        /* if (from.equals("MyActivities")) {
             adapter.addFragment(FragActivityDetailsNew.newInstance(activityId, type,hasAffliates), resources.getString(R.string.a_activity_first_tab), " This is First")
             adapter.addFragment(Frag_Activity_Member.newInstance(activityId), resources.getString(R.string.a_activity_rd_tab), " This is Second")
         } else {*/
        adapter.addFragment(FragActivityDetailsNew.newInstance(activityBean, type), resources.getString(R.string.a_activity_first_tab), " This is First")
        adapter.addFragment(FragmentChat.newInstanceActivityChat(activityId, clubId, activityName), resources.getString(R.string.a_activity_snd_tab), " This is second")
        adapter.addFragment(Frag_Activity_Member.newInstance(activityId), resources.getString(R.string.a_activity_rd_tab), " This is Third")
        //  }
        viewPager.adapter = adapter
        //Chiranjib
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        /*headerTxt.text = if (position == 0)
            "Activity Name"
        else "Activity Name"*/
        KeyboardUtil.hideKeyboard(this)
      //  bubble_menu.visibility = if (position == 0) View.VISIBLE else View.GONE
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.bubble_menu -> {
                val list: ArrayList<DialogMenu> = arrayListOf()
                list.add(DialogMenu(getString(R.string.edit), R.drawable.ic_edit))
                showMenu(list)
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    protected fun showMenu(list: ArrayList<DialogMenu>?) {

        if (menuDialog == null) {
            menuDialog = Dialog(this)
            menuDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = menuDialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            menuDialog?.setContentView(R.layout.club_more_menu)

            menuDialog?.ll_menu1?.visibility = View.GONE
            if (list != null) {
                menuDialog?.ll_menu0?.visibility = View.VISIBLE
                menuDialog?.menu_iv0?.setImageResource(list[0].id)
                menuDialog?.menu_tv0?.text = list[0].title
            }

            menuDialog?.ll_menu0?.setOnClickListener {
                //handleMenuClick(list!![0])
            }

            // for (views in arrayOf(menuDialog?.ll_menu1, menuDialog?.ll_menu2)) views?.setOnClickListener(this)
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            menuDialog?.setCancelable(true)
        }
        menuDialog?.show()
    }


    private fun getActivityDetails(activityId:String) {
        val dialogProgress = CusDialogProg(this@ActivitiesDetails)
        dialogProgress.show()


        object : VolleyGetPost(this, this,
                "${WebService.getActivityDetails}?activityId=${activityId}",
                true, true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {

                        val activityDetails = Gson().fromJson(response, GetActivityDetailsResponce::class.java)
                        setData(activityDetails!!.getData())
                    } else {
                        //  nodataLay.visibility = View.VISIBLE
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
                /* params["eventTitle"] = eventTitle
                 params["eventDate"] = eventDate
                 params["eventTime"] = eventTime
                 params["location"] = location
                 params["latitude"] = latitude
                 params["longitude"] = longitute
                 params["activityId"] = activityId
                 params["description"] = description*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(FragActivityDetailsNew::class.java.name)
    }

    private fun setData(data: GetActivityDetailsResponce.DataBean?) {
        activityBean = ActivitiesBean.DataBean()
        activityBean?.activityId = data?.activityId
        activityBean?.userId = data?.creator_id
        activityBean?.creator_id = data?.creator_id
        activityBean?.name = data?.name
        activityBean?.location = data?.location
        activityBean?.latitude = data?.latitude
        activityBean?.fee_type = data?.fee_type
        activityBean?.fee = data?.fee
        activityBean?.min_users = data?.min_users
        activityBean?.max_users = data?.max_users
        activityBean?.user_role = data?.user_role
        activityBean?.description = data?.description
        activityBean?.creator_phone = data?.creator_phone
        activityBean?.contact_no_visibility = data?.contact_no_visibility
        activityBean?.leader_id = data?.leader_id
        activityBean?.terms_conditions = data?.terms_conditions
        activityBean?.image = data?.image
        activityBean?.is_like = data?.is_like
        activityBean?.leader_name = data?.leader_name
        activityBean?.leader_prflimage = data?.leader_prflimage
        activityBean?.leader_phno = data?.leader_phno
        activityBean?.leader_contact_no_visibility = data?.leader_contact_no_visibility
        activityBean?.full_name = data?.creator_name
        activityBean?.profile_image = data?.creator_profile_image
        activityBean?.club_name = data?.club_name
        activityBean?.clubId = data?.clubId
        activityBean?.totalUser = data?.totalUser
        activityBean?.is_my_activity = data?.is_my_activity

        activityId = activityBean!!.activityId!!
        activityName = activityBean!!.name!!
        clubName = activityBean!!.club_name!!
        clubId = activityBean!!.clubId!!
        if (activityBean?.is_my_activity.equals("1")) {
            type="my"
        }else type="others"
        headerTxt.text = activityName
        clubNameTxt.text = clubName
        setViewPager(viewPager)

    }
}
