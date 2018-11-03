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
import com.clubz.ui.user_activities.model.GetActivityDetailsResponce
import com.clubz.ui.user_activities.model.GetJoinAffliates

import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_activity_details_new.*
import org.json.JSONObject

class FragActivityDetailsNew : Fragment(), View.OnClickListener {

    private var mContext: Context? = null
    private var activityId = ""
    private var type = ""
    private var hasAffliates = 0
    var activityDetails: GetActivityDetailsResponce? = null
    private var height: Int = 0
    private var width: Int = 0

    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    private var progressBar: ProgressBar? = null
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
            object : TermsConditionDialog(mContext!!, resources.getString(R.string.terms_conditions), activityDetails?.getData()?.terms_conditions!!) {
                override fun onCloseClicked() {
                    this.dismiss()
                }
            }.show()
        }
        imgLike.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
//                if (type != "my" && type != "others") {
                if (type.equals("others")) {
                    if (hasAffliates == 1) {
                        getUserJoinAfiliatesList(activityId)
                    } else {
                        if (activityDetails!!.getData()!!.is_like == "1") {
                            joinActivity(activityId, "", "")
                        }else{
                            joinActivity(activityId, "", userId)
                        }
                    }
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

        if (arguments != null) {
            activityId = arguments!!.getString(IDKEY)
            type = arguments!!.getString(TYPEKEY)
            hasAffliates = arguments!!.getInt(HSAFFLKEY)
            Log.e("ActivityId:  ", activityId)
            getActivityDetails()
        }
        username.setOnClickListener(this)
        imgActivity.setOnClickListener(this)
        activityLeader.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val IDKEY = "activityId"
        val TYPEKEY = "type"
        val HSAFFLKEY = "hasAffliates"
        fun newInstance(activityId: String, type: String, hasAffliates: Int): FragActivityDetailsNew {
            val fragment = FragActivityDetailsNew()
            val args = Bundle()
            args.putString(IDKEY, activityId)
            args.putString(TYPEKEY, type)
            args.putInt(HSAFFLKEY, hasAffliates)
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
                true) {
            override fun onVolleyResponse(response: String?) {
                progressBar?.visibility = View.VISIBLE
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        transView.visibility = View.GONE
                        progressBar?.visibility = View.GONE
                        activityDetails = Gson().fromJson(response, GetActivityDetailsResponce::class.java)
                        updateUi()
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
        }.execute(FragActivitiesDetails::class.java.name)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {
        activityName.text = activityDetails?.getData()?.name
        clubName.text = activityDetails?.getData()?.club_name
        /*if (activityDetails?.getData()?.is_like.equals("0")) {
            likeImg.setImageResource(R.drawable.inactive_heart_ico)
        } else {
            likeImg.setImageResource(R.drawable.active_heart_ico)
        }*/
        if (activityDetails?.getData()?.image?.isNotEmpty()!!) {
            Glide.with(imgActivity.context).load(activityDetails?.getData()?.image)/*.placeholder(R.drawable.new_img).fitCenter()*/.into(imgActivity)
        }
        if (activityDetails?.getData()?.leader_name?.isNotEmpty()!!) {
            activityLeader.text = activityDetails?.getData()?.leader_name
        }
        if (activityDetails?.getData()?.location.isNullOrEmpty()) {
            activityLocation.text = "Not Available"
        } else {
            activityLocation.text = activityDetails?.getData()?.location
        }
        fee.text = activityDetails?.getData()?.fee
        feeType.text = activityDetails?.getData()?.fee_type
        if (activityDetails?.getData()?.fee_type.equals("Free")) {
            fee.visibility = View.INVISIBLE
        }

        maxUser.text = activityDetails?.getData()?.max_users
        minUser.text = activityDetails?.getData()?.min_users
        if (type.equals("my")) {
            imgLike.setImageResource(R.drawable.ic_cards_heart_active)
        } else {
            if (activityDetails?.getData()?.is_like.equals("1")) {
                imgLike.setImageResource(R.drawable.ic_cards_heart_active)
            } else {
                imgLike.setImageResource(R.drawable.inactive_heart_ico)
            }
        }
        activityDesc.text = activityDetails?.getData()?.description
        username.text = activityDetails?.getData()?.creator_name
        if (activityDetails?.getData()?.creator_profile_image?.isNotEmpty()!!) {
            Glide.with(image_member2.context).load(activityDetails?.getData()?.creator_profile_image).into(image_member2)
        } else {
            // val padding = resources.getDimension(R.dimen._8sdp).toInt()
            // image_member2.setPadding(padding, padding, padding, padding)
            // image_member2.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_circle_blue)
            image_member2.setImageResource(R.drawable.user_place_holder)
        }
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
                // progressBar.visibility=View.GONE
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
            joinActivity(activityId, affiliateId, mUserId, dialog)
        })
    }

    fun joinActivity(activityId: String,
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
                user.userId = activityDetails!!.getData()!!.creator_id!!
                user.isLiked = "0"
                user.full_name = activityDetails?.getData()!!.creator_name!!
                user.profile_image = activityDetails?.getData()!!.creator_profile_image!!
                user.country_code = ""
                user.contact_no = activityDetails?.getData()!!.creator_phone!!
                user.contact_no_visibility = activityDetails?.getData()!!.contact_no_visibility!!
                if(!activityDetails?.getData()?.creator_id!!.equals(ClubZ.currentUser!!.id))showProfile(user)
            }
            R.id.activityLeader -> {
                val user = UserInfo()
                user.userId = activityDetails!!.getData()!!.leader_id!!
                user.isLiked = "0"
                user.full_name = activityDetails?.getData()!!.leader_name!!
                user.profile_image = activityDetails?.getData()!!.leader_prflimage!!
                user.country_code = ""
                user.contact_no = activityDetails?.getData()!!.leader_phno!!
                user.contact_no_visibility = activityDetails?.getData()!!.leader_contact_no_visibility!!
                if(!activityDetails?.getData()?.leader_id!!.equals(ClubZ.currentUser!!.id))showProfile(user)
            }
            R.id.imgActivity -> {
                val dialog = ZoomDialog(mContext!!, activityDetails?.getData()?.image!!)
                dialog.show()
            }
        }
    }

    private fun showProfile(user: UserInfo) {

        val dialog = object : ProfileDialog(mContext!!, user) {
            /*override fun OnFabClick(user: UserInfo) {
                Toast.makeText(mContext, "OnFabClick", Toast.LENGTH_SHORT).show()
            }*/

          /*  override fun OnChatClick(user: UserInfo) {
                Toast.makeText(mContext, "OnChatClick", Toast.LENGTH_SHORT).show()
            }*/

            /*override fun OnCallClick(user: UserInfo) {
                Toast.makeText(mContext, "OnCallClick", Toast.LENGTH_SHORT).show()
            }*/

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
        //  dialog.setCancelable(true)
        dialog.show()
    }
}
