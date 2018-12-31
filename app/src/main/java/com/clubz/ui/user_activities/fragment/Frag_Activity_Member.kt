package com.clubz.ui.user_activities.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.model.Profile
import com.clubz.data.model.UserInfo
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.dialogs.ProfileDialog
import com.clubz.ui.profile.ProfileActivity
import com.clubz.ui.user_activities.expandable_recycler_view.ActivityMembersAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.ActivityMembersViewHolder
import com.clubz.ui.user_activities.expandable_recycler_view.ExpandableRecyclerAdapter
import com.clubz.ui.user_activities.model.GetActivityMembersResponce
import com.clubz.utils.VolleyGetPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_activity_member.*
import org.json.JSONObject

class Frag_Activity_Member : Fragment() , ActivityMembersViewHolder.Listner {


    override fun onProfileClick(dataBean: GetActivityMembersResponce.DataBean?) {

        showProfile(dataBean)
    }

    private var mContext: Context? = null
    private var activityId = ""
    var activityJoiendListner: ValueEventListener? = null
    private var databaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_activity_member, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerActivityMember.layoutManager = LinearLayoutManager(mContext)
        if (arguments != null) {
            activityId = arguments!!.getString(IDKEY)
            getActivitiesJoined()
        }
    }
    // TODO: Rename method, update argument and hook method into UI event

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context

    }

    override fun onDetach() {
        super.onDetach()

    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val IDKEY = "activityId"
        fun newInstance(activityId: String): Frag_Activity_Member {
            val fragment = Frag_Activity_Member()
            val args = Bundle()
            args.putString(IDKEY, activityId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun getActivityMembers() {
        val dialogProgress = CusDialogProg(mContext!!)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(activity!!, mContext,
                "${WebService.getActivitymembers}?activityId=${activityId}&offset=${""}&limit=${""}",
                true, true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress?.dismiss()
                        val activityDetails = Gson().fromJson(response, GetActivityMembersResponce::class.java)
                        updateUi(activityDetails)
                    } else {
                        nodataLay.visibility = View.VISIBLE
                        dataLay.visibility = View.GONE
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
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Activity_Member::class.java.name)
    }

    private fun getActivitiesJoined() {
        activityJoiendListner = databaseReference
                .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                .child(activityId).addValueEventListener(object : ValueEventListener {


                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (mContext != null) {
                            var isActivityJoind = false
                            for (values in p0.children) {
                                if (values.getValue(String::class.java).equals(ClubZ.currentUser!!.id)) {
                                    isActivityJoind = true
                                    break
                                }
                            }
                            if (isActivityJoind) {
                                silentTxt?.visibility = View.GONE
                                nodataLay.visibility = View.GONE
                                silentTxt?.isClickable = true
                                recyclerActivityMember?.visibility = View.VISIBLE
                                getActivityMembers()
                            } else {
                                //silentTxt?.visibility = View.VISIBLE
                                nodataLay.visibility = View.VISIBLE
                               // silentTxt?.text = getString(R.string.first_join_this_activity)
                                recyclerActivityMember?.visibility = View.GONE
                            }
                        }
                    }
                })

    }

    private fun updateUi(activityDetails: GetActivityMembersResponce) {
        for (i in 0..activityDetails.getData()!!.size - 1) {
            val memberData = activityDetails.getData()!![i]
            if (memberData.affiliates != null) {
                for (j in 0..memberData.affiliates!!.size - 1) {
                    val afliate = memberData.affiliates!![j]
                    afliate.position = j
                    afliate.size = memberData.affiliates!!.size - 1
                }
            }
        }
        val activityMemberAdapter = ActivityMembersAdapter(mContext, activityDetails.getData(),this)
        activityMemberAdapter?.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = activityDetails.getData()!![position]
            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = activityDetails.getData()!![position]
            }

        })
        recyclerActivityMember.adapter = activityMemberAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (databaseReference != null) {
            if (activityJoiendListner != null) databaseReference
                    .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                    .child(activityId).removeEventListener(activityJoiendListner!!)
        }
    }

    private fun showProfile(dataBean: GetActivityMembersResponce.DataBean?) {
        val user = UserInfo()
        user.profile_image = dataBean?.profile_image!!
        user.userId = dataBean?.userId!!
        user.full_name = dataBean?.full_name!!
        user.contact_no =dataBean?.contact_no!!
        user.contact_no_visibility = dataBean?.contact_no_visibility!!
        user.clubId = dataBean?.club_id!!

        val dialog = object : ProfileDialog(context!!, user) {
            override fun OnProfileClick(user: UserInfo) {
                if (user.userId.isNotEmpty()) {
                    val profile = Profile()
                    profile.userId = user.userId
                    profile.full_name = user.full_name
                    profile.profile_image = user.profile_image
                    context?.startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", profile))
                } else {
                    Toast.makeText(context, getString(R.string.under_development), Toast.LENGTH_SHORT).show()
                }
            }

        }
        //  dialog.setCancelable(true)
        dialog.show()
    }
}// Required empty public constructor
