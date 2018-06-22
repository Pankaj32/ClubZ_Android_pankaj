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

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.expandable_recycler_view.ActivityMembersAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.ExpandableRecyclerAdapter
import com.clubz.ui.user_activities.model.GetActivityMembersResponce
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_activity_member.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Frag_Activity_Member.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Frag_Activity_Member.newInstance] factory method to
 * create an instance of this fragment.
 */
class Frag_Activity_Member : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mContext: Context? = null
    private var activityId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.frag_activity_member, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerActivityMember.layoutManager = LinearLayoutManager(mContext)
        if (arguments != null) {
            activityId = arguments.getString(IDKEY)
            getActivityMembers()
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

    fun getActivityMembers() {
        val dialogProgress = CusDialogProg(mContext!!)
        dialogProgress.show()
        //    ClubZ.instance.cancelPendingRequests(ClubsActivity::class.java.name)
        object : VolleyGetPost(activity!!, mContext,
                "${WebService.getActivitymembers}?activityId=${activityId}&offset=${""}&limit=${""}",
                true) {
            override fun onVolleyResponse(response: String?) {
                dialogProgress.dismiss()
                try {

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        dialogProgress?.dismiss()
                        var activityDetails = Gson().fromJson(response, GetActivityMembersResponce::class.java)
                        updateUi(activityDetails)
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
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(Frag_Activity_Member::class.java.name)
    }

    private fun updateUi(activityDetails: GetActivityMembersResponce) {
        var activityMemberAdapter = ActivityMembersAdapter(mContext, activityDetails.getData())
        activityMemberAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = activityDetails.getData()!![position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = activityDetails.getData()!![position]
            }

        })
        recyclerActivityMember.adapter = activityMemberAdapter
    }
}// Required empty public constructor
