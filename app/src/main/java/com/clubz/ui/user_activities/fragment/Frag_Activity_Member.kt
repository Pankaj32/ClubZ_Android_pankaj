package com.clubz.ui.user_activities.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.expandable_recycler_view.ActivityMembersAdapter
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

class Frag_Activity_Member : Fragment() {

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
                                silentTxt?.isClickable = true
                                recyclerActivityMember?.visibility = View.VISIBLE
                                getActivityMembers()
                            } else {
                                silentTxt?.visibility = View.VISIBLE
                                silentTxt?.text = getString(R.string.first_join_this_activity)
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
        val activityMemberAdapter = ActivityMembersAdapter(mContext, activityDetails.getData())
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
}// Required empty public constructor
