package com.clubz.ui.user_activities.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Profile
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.TermsConditionDialog
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.dialogs.ZoomDialog
import com.clubz.ui.profile.ProfileActivity
import com.clubz.ui.user_activities.adapter.JoinAffiliatesAdapter
import com.clubz.ui.user_activities.model.ActivitiesBean
import com.clubz.ui.user_activities.model.GetActivityDetailsResponce
import com.clubz.ui.user_activities.model.GetJoinAffliates

import com.clubz.utils.VolleyGetPost
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_activity_details_new.*
import org.json.JSONObject

class FragActivityDetailsNew : Fragment(), View.OnClickListener {

    private var mContext: Context? = null
    private var activityId = ""
    private var activityOwnerId = ""
    private var type = ""
    private var hasAffliates = ""
    private var activityDetails: GetActivityDetailsResponce? = null
    private var height: Int = 0
    private var width: Int = 0

    private var userId: String = ""
    private var leaderId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    private var progressBar: ProgressBar? = null
    private var activityBean: ActivitiesBean.DataBean? = null
    /*  private progressBar=*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_activity_details_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        termsNCondition.setOnClickListener {
            var trmCon = ""
            trmCon = if (activityDetails != null) activityDetails?.getData()?.terms_conditions!! else activityBean!!.terms_conditions!!
            object : TermsConditionDialog(mContext!!, resources.getString(R.string.terms_conditions), trmCon) {
                override fun onCloseClicked() {
                    this.dismiss()
                }
            }.show()
        }


        imgLike.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
//                if (type != "my" && type != "others") {
                if (type.equals("others")) {
                    if (hasAffliates.equals("1")) {
                        getUserJoinAfiliatesList(activityId,activityOwnerId,activityDetails!!.getData()!!.clubId!!)
                    } else {
                        if (activityDetails != null) {
                            if (activityDetails!!.getData()!!.is_like == "1") {
                                joinActivity(activityDetails!!.getData()!!.creator_id!!,activityId, "", "",activityDetails!!.getData()!!.clubId!!)
                            } else {
                                joinActivity(activityDetails!!.getData()!!.creator_id!!,activityId, "", userId,activityDetails!!.getData()!!.clubId!!)
                            }
                        } else {
                            if (activityBean?.is_like == "1") {
                                joinActivity(activityDetails!!.getData()!!.creator_id!!,activityId, "", "",activityDetails!!.getData()!!.clubId!!)
                            } else {
                                joinActivity(activityDetails!!.getData()!!.creator_id!!,activityId, "", userId,activityDetails!!.getData()!!.clubId!!)
                            }
                        }
                    }
                }
                else{
                    if(activityDetails!!.getData()!!.clubId!!!=null)
                    getUserJoinAfiliatesList(activityId,activityOwnerId,activityDetails!!.getData()!!.clubId!!)
                }
            }

        })
    }

    private fun initializeView() {
        val diametric = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(diametric)
        width = diametric.widthPixels
        height = diametric.heightPixels

        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        hasAffliates = SessionManager.getObj().user.hasAffiliates
        if (arguments != null) {
            activityBean = arguments!!.getParcelable(KEYBEAN)
            activityId = activityBean!!.activityId!!
            activityOwnerId = activityBean!!.creator_id!!

            type = arguments!!.getString(TYPEKEY)

            if(!type.equals("others")){
                if(hasAffliates.equals("0")){
                    imgLike.visibility = View.GONE
                }
            }
            Log.e("ActivityId:  ", activityId)
            setData()
            getActivityDetails()
        }
        username.setOnClickListener(this)
        imgActivity.setOnClickListener(this)
        activityLeader.setOnClickListener(this)
    }

    private fun setData() {
        val details = GetActivityDetailsResponce.DataBean()
        details.activityId = activityBean!!.activityId
        details.creator_id = activityBean!!.userId
        details.name = activityBean!!.name
        details.location = activityBean!!.location
        details.latitude = activityBean!!.latitude
        details.longitude = activityBean!!.longitude
        details.fee_type = activityBean!!.fee_type
        details.fee = activityBean!!.fee
        details.min_users = activityBean!!.min_users
        details.max_users = activityBean!!.max_users
        details.user_role = activityBean!!.user_role
        details.description = activityBean!!.description
        details.creator_phone = activityBean!!.creator_phone
        details.contact_no_visibility = activityBean!!.contact_no_visibility
        details.leader_id = activityBean!!.leader_id
        details.terms_conditions = activityBean!!.terms_conditions
        details.image = activityBean!!.image
        details.is_like = activityBean!!.is_like
        details.leader_name = activityBean!!.leader_name
        details.leader_prflimage = activityBean!!.leader_prflimage
        details.leader_phno = activityBean!!.leader_phno
        details.leader_contact_no_visibility = activityBean!!.leader_contact_no_visibility
        details.creator_name = activityBean!!.full_name
        details.creator_profile_image = activityBean!!.profile_image
        details.club_name = activityBean!!.club_name
        details.clubId = activityBean!!.clubId
        details.totalUser = activityBean!!.totalUser
        updateUi(details)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    companion object {
        val KEYBEAN = "activityBean"
        val TYPEKEY = "type"

        fun newInstance(activitiesBean: ActivitiesBean.DataBean?, type: String): FragActivityDetailsNew {
            val fragment = FragActivityDetailsNew()
            val args = Bundle()
            args.putParcelable(KEYBEAN, activitiesBean)
            args.putString(TYPEKEY, type)

            fragment.arguments = args
            return fragment
        }
    }

    private fun getActivityDetails() {
        /*val dialogProgress = CusDialogProg(mContext!!)
        dialogProgress.show()*/
        progressBar?.visibility = View.VISIBLE
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(activity!!, mContext,
                "${WebService.getActivityDetails}?activityId=${activityId}",
                true, false) {
            override fun onVolleyResponse(response: String?) {
                progressBar?.visibility = View.VISIBLE
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        transView.visibility = View.GONE
                        progressBar?.visibility = View.GONE
                        activityDetails = Gson().fromJson(response, GetActivityDetailsResponce::class.java)
                        updateUi(activityDetails!!.getData())
                    } else {
                        //  nodataLay.visibility = View.VISIBLE
                    }
                    // searchAdapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                progressBar?.visibility = View.GONE
            }

            override fun onNetError() {
                progressBar?.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private fun updateUi(data: GetActivityDetailsResponce.DataBean?) {
        leaderId = data?.leader_id!!
        activityName.text = data.name
        clubName.text = data.club_name
        /*if (activityDetails?.getData()?.is_like.equals("0")) {
            likeImg.setImageResource(R.drawable.inactive_heart_ico)
        } else {
            likeImg.setImageResource(R.drawable.active_heart_ico)
        }*/
        if (!data.image.isNullOrEmpty()) {
            Glide.with(imgActivity.context).load(data.image)/*.placeholder(R.drawable.new_img).fitCenter()*/.into(imgActivity)
        }
        if (!data.leader_name.isNullOrEmpty()) {
            activityLeader.text = data.leader_name
        }
        if (data.location.isNullOrEmpty()) {
            activityLocation.text = "Not Available"
        } else {
            activityLocation.text = data.location
        }
        fee.text = data.fee
        feeType.text = data.fee_type
        if (data.fee_type.equals("Free")) {
            fee.visibility = View.INVISIBLE
        }

        maxUser.text = data.max_users
        minUser.text = data.min_users
        if (type.equals("my")) {
            imgLike.setImageResource(R.drawable.ic_cards_heart_active)
        } else {
            if (data.is_like.equals("1")) {
                imgLike.setImageResource(R.drawable.ic_cards_heart_active)
            } else {
                imgLike.setImageResource(R.drawable.inactive_heart_ico)
            }
        }
        activityDesc.text = data.description
        username.text = data.creator_name
        if (data.creator_profile_image?.isNotEmpty()!!) {
            Glide.with(image_member2.context).load(data.creator_profile_image).into(image_member2)
        } else {
            // val padding = resources.getDimension(R.dimen._8sdp).toInt()
            // image_member2.setPadding(padding, padding, padding, padding)
            // image_member2.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_circle_blue)
            image_member2.setImageResource(R.drawable.user_place_holder)
        }
    }

    private fun getUserJoinAfiliatesList(activityId: String,activitycreatorId: String, clubId:String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(mContext as Activity?, mContext,
                "${WebService.getuserJoinAffiliates}?userId=${userId}&activityId=${activityId}",/*userId=74&activityId=7*/
                true, false) {
            override fun onVolleyResponse(response: String?) {
                dialog.dismiss()
                // progressBar.visibility=View.GONE
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        //  popUpJoin(type)
                        var getJoinAffliates: GetJoinAffliates = Gson().fromJson(response, GetJoinAffliates::class.java)
                        popUpJoin(activityId, getJoinAffliates,activitycreatorId,clubId)
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
        }.execute(FragActivityDetailsNew::class.java.name)
    }

    internal fun popUpJoin(activityId: String, getJoinAffliates: GetJoinAffliates,activityownerid:String,clubID:String) {
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
        if(activityownerid.equals(userId)){
            likeCkeck.isClickable = false
            likeLay.setOnClickListener { null }
        }
        else{
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
        }

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
            joinActivity(activityownerid,activityId, affiliateId, mUserId,clubID, dialog)
        })
    }

    fun joinActivity(
                     activityownerID:String,
                     activityId: String,
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
                        val objaffiliate = obj.getJSONObject("affiliate_name")
                        var getAddedaffiliatename:String = objaffiliate.get("addAffiliates").toString()
                        var getDeletedaffiliatename:String = objaffiliate.get("removeAffiliates").toString()
                        var ownerremovestatus:String = objaffiliate.get("ownerJoinStatus").toString()


                        dialog1?.dismiss()
                       /* if (affiliateId.equals("") && userId.equals("")) {
                            joinActivityInFireBase(activityId, "remove")
                        } else {
                            joinActivityInFireBase(activityId, "join")
                        }*/
                        joinActivityInFireBase(ownerremovestatus,activityId,clubId, getAddedaffiliatename,getDeletedaffiliatename,activityownerID,userId)

                        getActivityDetails()
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
        }.execute(FragActivityDetailsNew::class.java.name)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.username -> {
                val user = UserInfo()
                if (activityDetails != null) {
                    user.userId = activityDetails!!.getData()!!.creator_id!!
                    user.isLiked = "0"
                    user.full_name = activityDetails?.getData()!!.creator_name!!
                    user.profile_image = activityDetails?.getData()!!.creator_profile_image!!
                    user.country_code = ""
                    user.contact_no = activityDetails?.getData()!!.creator_phone!!
                    user.contact_no_visibility = activityDetails?.getData()!!.contact_no_visibility!!
                    user.clubId = activityDetails?.getData()!!.clubId!!
                } else {
                    user.userId = activityBean?.creator_id!!
                    user.isLiked = "0"
                    user.full_name = activityBean!!.full_name!!
                    user.profile_image = activityBean!!.profile_image!!
                    user.country_code = ""
                    user.contact_no = activityBean!!.creator_phone!!
                    user.contact_no_visibility = activityBean!!.contact_no_visibility!!
                    user.clubId = activityBean!!.clubId!!
                }
                if (!user.userId.equals(ClubZ.currentUser!!.id)) showProfile(user)
            }
            R.id.activityLeader -> {


                if(activityDetails!!.getData()!!.leader_id!=null&&!activityDetails!!.getData()!!.leader_id.equals("0")){

                val user = UserInfo()
                if (activityDetails != null) {
                    user.userId = activityDetails!!.getData()!!.leader_id!!
                    user.isLiked = "0"
                    user.full_name = activityDetails?.getData()!!.leader_name!!
                    user.profile_image = activityDetails?.getData()!!.leader_prflimage!!
                    user.country_code = ""
                    user.contact_no = activityDetails?.getData()!!.leader_phno!!
                    user.contact_no_visibility = activityDetails?.getData()!!.leader_contact_no_visibility!!
                    user.clubId = activityDetails?.getData()!!.clubId!!
                } else {
                    user.userId = activityBean!!.leader_id!!
                    user.isLiked = "0"
                    user.full_name = activityBean!!.leader_name!!
                    user.profile_image = activityBean!!.leader_prflimage!!
                    user.country_code = ""
                    user.contact_no = activityBean!!.leader_phno!!
                    user.contact_no_visibility = activityBean!!.leader_contact_no_visibility!!
                    user.clubId = activityBean!!.clubId!!
                }
                if (!leaderId.equals(ClubZ.currentUser!!.id)) showProfile(user)

                }
            }
            R.id.imgActivity -> {
                var image = ""
                if (activityDetails != null) {
                    image = activityDetails?.getData()?.image.toString()
                } else image = activityBean!!.image!!
                val dialog = ZoomDialog(mContext!!, image)

                dialog.show()
            }
        }
    }

    private fun showProfile(user: UserInfo) {

        val dialog = object : ProfileDialog(mContext!!, user) {

            override fun OnProfileClick(user: UserInfo) {
                if (user.userId.isNotEmpty()) {
                    val profile = Profile()
                    profile.userId = user.userId
                    profile.full_name = user.full_name
                    profile.profile_image = user.profile_image
                    mContext?.startActivity(Intent(mContext, ProfileActivity::class.java).putExtra("profile", profile))
                } else {
                    Toast.makeText(mContext, getString(R.string.under_development), Toast.LENGTH_SHORT).show()
                }
            }

        }
        dialog.show()
    }

    private fun joinActivityInFireBase(ownerremovestatus:String,activityId: String,clubId: String,addaffilate: String,removeaffilate: String,activityownerID: String,userId: String) {




        if(activityownerID.equals(userId)){

            if(addaffilate.isNotEmpty()){
                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                        .child(activityId)
                        .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                            val msg = ClubZ.currentUser!!.full_name+" has joined "+addaffilate
                            sendMessage(ChatUtil.ARG_ACTIVITY_JOIND,msg,clubId,activityId)
                        }
            }
            if(removeaffilate.isNotEmpty()){
                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                        .child(activityId)
                        .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                            val msg = ClubZ.currentUser!!.full_name+" has removed "+removeaffilate
                            sendMessage(ChatUtil.ARG_ACTIVITY_JOIND,msg,clubId,activityId)
                        }
            }
            if(addaffilate.isEmpty()&&removeaffilate.isEmpty()){
                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                        .child(activityId)
                        .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                            val msg = ClubZ.currentUser!!.full_name+" has joined"
                            sendMessage(ChatUtil.ARG_ACTIVITY_JOIND,msg,clubId,activityId)
                        }
            }


        }
        else {

            if (ownerremovestatus.equals("1")) {

                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                        .child(activityId)
                        .child(SessionManager.getObj().user.id).setValue(userId).addOnCompleteListener {
                            val msg = ClubZ.currentUser!!.full_name + " has joined"
                            sendMessage(ChatUtil.ARG_ACTIVITY_JOIND, msg, clubId, activityId)
                        }

                if (addaffilate.isNotEmpty()) {
                    FirebaseDatabase.getInstance()
                            .reference
                            .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                            .child(activityId)
                            .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                                val msg = ClubZ.currentUser!!.full_name + " has joined " + addaffilate
                                sendMessage(ChatUtil.ARG_ACTIVITY_JOIND, msg, clubId, activityId)
                            }
                }
                if (removeaffilate.isNotEmpty()) {
                    FirebaseDatabase.getInstance()
                            .reference
                            .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                            .child(activityId)
                            .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                                val msg = ClubZ.currentUser!!.full_name + " has removed " + removeaffilate
                                sendMessage(ChatUtil.ARG_ACTIVITY_JOIND, msg, clubId, activityId)
                            }
                }


            }
            if (ownerremovestatus.equals("0")) {



                if (addaffilate.isNotEmpty()) {
                    FirebaseDatabase.getInstance()
                            .reference
                            .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                            .child(activityId)
                            .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                                val msg = ClubZ.currentUser!!.full_name + " has joined " + addaffilate
                                sendMessage(ChatUtil.ARG_ACTIVITY_JOIND, msg, clubId, activityId)
                            }
                }
                if (removeaffilate.isNotEmpty()) {
                    FirebaseDatabase.getInstance()
                            .reference
                            .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                            .child(activityId)
                            .child(SessionManager.getObj().user.id).setValue(SessionManager.getObj().user.id).addOnCompleteListener {
                                val msg = ClubZ.currentUser!!.full_name + " has removed " + removeaffilate
                                sendMessage(ChatUtil.ARG_ACTIVITY_JOIND, msg, clubId, activityId)
                            }
                }



            }
            if(ownerremovestatus.equals("")){
                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                        .child(activityId)
                        .child(SessionManager.getObj().user.id).setValue(null).addOnCompleteListener {
                            val msg = ClubZ.currentUser!!.full_name+" has removed "
                            sendMessage(ChatUtil.ARG_ACTIVITY_JOIND,msg,clubId,activityId)
                        }

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
//chatRoom = clubId + "_" + activityId + "_" + chatFor