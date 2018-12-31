package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R
import com.clubz.ui.club.adapter.AdapterClubApplicant
import com.clubz.ui.club.adapter.AdapterClubMember
import kotlinx.android.synthetic.main.frag_club_details2.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.ClubMember
import com.clubz.data.model.Clubs
import com.clubz.data.remote.WebService
import com.clubz.helper.Type_Token
import com.clubz.ui.club.adapter.AdapterOwnClubMember
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import org.json.JSONObject
import android.widget.ImageView
import android.widget.Toast
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.ui.dialogs.ZoomDialog
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.frag_club_details1.*

/**
 * Created by Dhrmraj Acharya on २१/३/१८.
 */
class FragClubDetails2 : Fragment(), AdapterOwnClubMember.Listner, AdapterClubApplicant.Listner {

    lateinit var clubz: Clubs
    var adapterOwnClubMember: AdapterOwnClubMember? = null
    var adapterApplicant: AdapterClubApplicant? = null
    var adapterClubMember: AdapterClubMember? = null
    var isCurrentCollapsed = true
    var isApplicantsCollapsed = true

    var memberList: List<ClubMember> = emptyList()
    var applicantList: List<ClubMember> = emptyList()

    fun setData(clubz: Clubs): Fragment {
        this.clubz = clubz
        return this
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_club_details2, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        //text_top.visibility = View.GONE
        view!!.setOnClickListener {}

        ll_expand1.setOnClickListener {

            if (isCurrentCollapsed) {
                isCurrentCollapsed = false
                //tv_expand1.text = getString(R.string.expand)
                setUpExpandAndCollapse(false, iv_expand1, cardViewMember)
                rv_members.visibility = View.GONE
                currentView.visibility = View.GONE
            } else {
                isCurrentCollapsed = true
                // tv_expand1.setText(R.string.collapsed)
                setUpExpandAndCollapse(true, iv_expand1, cardViewMember)
                rv_members.visibility = View.VISIBLE
                currentView.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpExpandAndCollapse(flag: Boolean, imageView: ImageView, cardView: CardView) {
        Util.setRotation(imageView, flag)
        if (flag) {
            imageView.setImageResource(R.drawable.ic_event_up_arrow)
            val param = cardView.layoutParams as LinearLayout.LayoutParams
            param.height = 0
            param.weight = 1f
        } else {

            imageView.setImageResource(R.drawable.ic_event_down_arrow)
            val param = cardView.layoutParams as LinearLayout.LayoutParams
            param.weight = 0f
            param.height = WRAP_CONTENT
            param.apply { }
        }
    }


    private fun setView() {

        rv_members.layoutManager = LinearLayoutManager(context)
        if (clubz.user_id == ClubZ.currentUser?.id) {

            adapterOwnClubMember = object : AdapterOwnClubMember(context, memberList, this,
                    clubz.club_type != "1") {
            }
            rv_members.adapter = adapterOwnClubMember

            if (clubz.club_type == "1") {
                cardViewApplicant.visibility = View.GONE

            } else if (clubz.club_type == "2") {
                cardViewApplicant.visibility = View.VISIBLE
                rv_appcalints.layoutManager = LinearLayoutManager(context)
                adapterApplicant = object : AdapterClubApplicant(context, this) {}
                rv_appcalints.adapter = adapterApplicant

                ll_expand2.setOnClickListener {

                    if (isApplicantsCollapsed) {
                        isApplicantsCollapsed = false
                        //   tv_expand2.text = getString(R.string.expand)
                        rv_appcalints.visibility = View.GONE
                        setUpExpandAndCollapse(false, iv_expand2, cardViewApplicant)
                        applicantView.visibility = View.VISIBLE
                    } else {
                        isApplicantsCollapsed = true
                        // tv_expand2.setText(R.string.collapsed)
                        rv_appcalints.visibility = View.VISIBLE
                        setUpExpandAndCollapse(true, iv_expand2, cardViewApplicant)
                        applicantView.visibility = View.GONE
                    }
                }

                getApplicants()
            }

            getOwnClubMembers()

        } else {

            cardViewApplicant.visibility = View.GONE
            adapterClubMember = object : AdapterClubMember(context, memberList) {
            }
            rv_members.adapter = adapterClubMember
            getClubMembers()
        }




    }

    override fun onFocus() {
        if (isApplicantsCollapsed) {
            isApplicantsCollapsed = false
            //  tv_expand2.text = getString(R.string.expand)
            rv_appcalints.visibility = View.GONE
            setUpExpandAndCollapse(false, iv_expand2, cardViewApplicant)
        }
    }

    private fun getOwnClubMembers() {
        val dialog = CusDialogProg(context)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, "${WebService.club_member_list}?clubId=${clubz.clubId}", true,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapterOwnClubMember?.setMemberList(memberList)

                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context!!)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun getClubMembers() {
        object : VolleyGetPost(activity, "${WebService.club_member_list}?clubId=${clubz.clubId}", true,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    //dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapterClubMember?.setMemberList(memberList)
                    }
                } catch (ex: Exception) {
                }
            }

            override fun onVolleyError(error: VolleyError?) {
            }

            override fun onNetError() {
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun getApplicants() {
        val dialog = CusDialogProg(context)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, "${WebService.club_applicant_list}?clubId=${clubz.clubId}", true,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        applicantList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapterApplicant?.setApplicantList(applicantList)
                    }
                } catch (ex: Exception) {
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    override fun onUpdateClubMember(member: ClubMember?, pos: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.club_updateMemberStatus, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        member?.member_status = obj.getString("member_status")
                        updateClubMemberInFireBase(member)
                        adapterOwnClubMember?.updateMember(member, pos)
                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubUserId"] = member!!.clubUserId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    override fun onClickAction(member: ClubMember?, status: String?, pos: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.club_member_action, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        adapterApplicant?.removeApplicent(pos)
                        if (clubz.user_id == ClubZ.currentUser?.id) {
                            getOwnClubMembers()
                        } else getClubMembers()

                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context!!)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["clubId"] = clubz.clubId
                params["userId"] = member!!.userId
                params["answerStatus"] = status!!
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    private fun updateClubMemberInFireBase(member: ClubMember?) {

        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(clubz.clubId)
                .child(member?.userId!!)
                .child("silent").setValue(member?.member_status)
        Toast.makeText(context,member.full_name+" "+if (member.member_status.equals("1"))getString(R.string.has_been_activated) else getString(R.string.has_been_inactivated),Toast.LENGTH_SHORT).show()
    }

    override fun onTagAdd(tag: String?, member: ClubMember?, pos: Int) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.club_add_member_Tag, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        var tadIdvalue = obj.getString("userTagId")
                        adapterOwnClubMember?.addTag(tag, pos,tadIdvalue)

                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context!!)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["tagName"] = tag!!
                params["clubUserId"] = member!!.clubUserId
                params["userId"] = member.userId
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    override fun onTagDelete(tagid: String?) {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, WebService.club_delete_member_Tag, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        adapterOwnClubMember?.removeTag(tagid)

                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, context!!)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["userTagId"] = tagid!!
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }
}