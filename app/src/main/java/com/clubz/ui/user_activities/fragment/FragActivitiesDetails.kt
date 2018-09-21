package com.clubz.ui.user_activities.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.TermsConditionDialog
import com.clubz.ui.user_activities.model.GetActivityDetailsResponce
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_frag_activities_details.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragActivitiesDetails.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragActivitiesDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragActivitiesDetails : Fragment() {

    // TODO: Rename and change types of parameters

    private var mContext: Context? = null
    private var activityId = ""
    var activityDetails: GetActivityDetailsResponce? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_activities_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            activityId = arguments!!.getString(IDKEY)
            Log.e("ActivityId:  ", activityId)
            getActivityDetails()
        }
        termsNCondition.setOnClickListener {
            object : TermsConditionDialog(context!!, resources.getString(R.string.terms_conditions), activityDetails?.getData()?.terms_conditions!!) {
                override fun onCloseClicked() {
                    this.dismiss()
                }
            }.show()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val IDKEY = "activityId"
        fun newInstance(activityId: String): FragActivitiesDetails {
            val fragment = FragActivitiesDetails()
            val args = Bundle()
            args.putString(IDKEY, activityId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun getActivityDetails() {
        val dialogProgress = CusDialogProg(mContext!!)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(activity!!, mContext,
                "${WebService.getActivityDetails}?activityId=${activityId}",
                true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress?.dismiss()
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
            Picasso.with(imgActivity.context).load(activityDetails?.getData()?.image).into(imgActivity)
        }
        if (activityDetails?.getData()?.leader_name?.isNotEmpty()!!) {
            activityLeader.text = activityDetails?.getData()?.leader_name
            leader.text = getString(R.string.leader)
        } else {
            leaderLay.visibility = View.GONE
        }
        activityLocation.text = activityDetails?.getData()?.location
        fee.text = activityDetails?.getData()?.fee
        feeType.text = activityDetails?.getData()?.fee_type
        if (activityDetails?.getData()?.fee_type.equals("Free")) {
            fee.visibility = View.GONE
            feeView.visibility = View.GONE
        }
        maxMinUser.text = String().format(getString(R.string.max_min_text),
                activityDetails?.getData()?.max_users, activityDetails?.getData()?.min_users)
        if (activityDetails?.getData()?.next_event == null) {
            nextLay.visibility = View.GONE
            noNextTxt.visibility = View.VISIBLE
        } else {
            eventTitle.text = activityDetails?.getData()?.next_event?.event_title
            eventDate.text = activityDetails?.getData()?.next_event?.event_date
            eventLocation.text = activityDetails?.getData()?.next_event?.location
            eventTime.text = Util.setTimeFormat(activityDetails?.getData()?.next_event?.event_time!!)
            if (activityDetails?.getData()?.next_event?.description?.isNotEmpty()!!) {
                eventDesc.text = activityDetails?.getData()?.next_event?.description
            } else {
                eventDesc.text = "not available"
            }

        }
        genDescription.text = activityDetails?.getData()?.description
        username.text = activityDetails?.getData()?.creator_name
        if (activityDetails?.getData()?.creator_profile_image?.isNotEmpty()!!) {
            Picasso.with(image_member2.context).load(activityDetails?.getData()?.creator_profile_image).into(image_member2)
        } else {
            val padding = resources.getDimension(R.dimen._8sdp).toInt()
            image_member2.setPadding(padding, padding, padding, padding)
            image_member2.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_circle_blue)
            image_member2.setImageResource(R.drawable.ic_user_shape)
        }
        //  usrerole.text = activityDetails.getData()?.user_role
    }
}// Required empty public constructor
