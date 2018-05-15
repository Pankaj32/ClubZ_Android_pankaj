package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
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
import android.view.animation.AnimationUtils
import android.view.animation.Animation


/**
 * Created by mindiii on २१/३/१८.
 */
class Frag_ClubDetails_2 : Fragment(), AdapterOwnClubMember.Listner, AdapterClubApplicant.Listner {

    lateinit var clubz :Clubs;
    var adapterOwnClubMember : AdapterOwnClubMember? = null
    var adapter_applicant : AdapterClubApplicant? = null
    var adapterClubMember : AdapterClubMember? = null

    var memberList : List<ClubMember> = emptyList()
    var applicantList : List<ClubMember> = emptyList()

    fun setData(clubz: Clubs): Fragment {
        this.clubz = clubz
        return this
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_club_details2,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        //text_top.visibility = View.GONE
        view!!.setOnClickListener{}

        ll_expand1.setOnClickListener {

            if(tv_expand1.text.equals(getString(R.string.collapse))){
                tv_expand1.text = getString(R.string.expand)
                //iv_expand1.animate().rotation(180f).start()
                val aniRotateClk = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
                aniRotateClk.setRepeatCount(Animation.INFINITE);
                iv_expand1.startAnimation(aniRotateClk)
                rv_members.visibility = View.GONE

                val param = cardViewMember.layoutParams as LinearLayout.LayoutParams
                param.weight = 0f
                param.height = WRAP_CONTENT

                param.apply {  }


            }else{
                tv_expand1.setText(R.string.collapse)
               // iv_expand1.animate().rotation(180f).start()
                val aniRotateClk = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
                iv_expand1.startAnimation(aniRotateClk)
                val param = cardViewMember.layoutParams as LinearLayout.LayoutParams
                param.height = 0
                param.weight = 1f
                rv_members.visibility = View.VISIBLE
            }
        }

        ll_expand2.setOnClickListener {

            if(tv_expand2.text.equals(getString(R.string.collapse))){
                tv_expand2.text = getString(R.string.expand)
               // iv_expand2.animate().rotation(180f).start()

                val aniRotateClk = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
                aniRotateClk.setRepeatCount(Animation.INFINITE);
                iv_expand2.startAnimation(aniRotateClk)

                rv_appcalints.visibility = View.GONE
                val param = cardViewApplicant.layoutParams as LinearLayout.LayoutParams
                param.weight = 0f
                param.height = WRAP_CONTENT
                param.apply {  }


            }else{
                tv_expand2.setText(R.string.collapse)
                //iv_expand2.animate().rotation(180f).start()
                val aniRotateClk = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
                iv_expand2.startAnimation(aniRotateClk)

                val param = cardViewApplicant.layoutParams as LinearLayout.LayoutParams
                param.height = 0
                param.weight = 1f
                rv_appcalints.visibility = View.VISIBLE
            }
        }
    }


    fun setView(){

        rv_members.layoutManager = LinearLayoutManager(context)
        if(clubz.user_id.equals(ClubZ.currentUser?.id)){

            adapterOwnClubMember = object : AdapterOwnClubMember(context, memberList, this,
                    !clubz.club_type.equals("1")){
            }
            rv_members.adapter = adapterOwnClubMember

            rv_appcalints.layoutManager = LinearLayoutManager(context)
            //rv_appcalints.addItemDecoration(SimpleDividerItemDecoration(context))
            adapter_applicant = object : AdapterClubApplicant(context, this){

            }
            rv_appcalints.adapter = adapter_applicant
            getOwnClubMembers()
            getApplicants()
        }else{

            cardViewApplicant.visibility = View.GONE
            adapterClubMember = object : AdapterClubMember(context, memberList){
            }
            rv_members.adapter = adapterClubMember
            getClubMembers()
        }
    }

    override fun onFocus() {
        tv_expand2.text = getString(R.string.expand)
        //iv_expand2.animate().rotation(180f).start()

        val aniRotateClk = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
        aniRotateClk.setRepeatCount(Animation.INFINITE);
        iv_expand2.startAnimation(aniRotateClk)

        rv_appcalints.visibility = View.GONE
        val param = cardViewApplicant.layoutParams as LinearLayout.LayoutParams
        param.weight = 0f
        param.height = WRAP_CONTENT
        param.apply {  }
    }

    fun getOwnClubMembers(){
        val dialog = CusDialogProg(context );
        dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity,"${WebService.club_member_list}?clubId=${clubz.clubId}",true){
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapterOwnClubMember?.setMemberList(memberList)

                    } else {
                        Util.showToast(obj.getString("message"),context)
                    }
                }catch (ex :Exception){
                   // Util.showToast(R.string.swr,context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
//                params.put("clubId",clubz.clubId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }

    fun getClubMembers() {
        //val dialog = CusDialogProg(context);
        //dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, "${WebService.club_member_list}?clubId=${clubz.clubId}", true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    //dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapterClubMember?.setMemberList(memberList)
                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    //Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
               // dialog.dismiss();
            }

            override fun onNetError() {
                //dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
//                params.put("clubId",clubz.clubId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }

    fun getApplicants() {
        val dialog = CusDialogProg(context);
        dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, "${WebService.club_applicant_list}?clubId=${clubz.clubId}", true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        applicantList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                        adapter_applicant?.setApplicantList(applicantList)
                    } else {
                        //Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                   // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
//                params.put("clubId",clubz.clubId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }


    override fun onUpdateClubMember(member: ClubMember?, pos: Int) {
        val dialog = CusDialogProg(context);
        dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.club_updateMemberStatus, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        member?.member_status = obj.getString("member_status")
                        adapterOwnClubMember?.updateMember(member, pos)
                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("clubUserId",member!!.clubUserId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }

    override fun onClickAction(member: ClubMember?, status: String?, pos: Int) {
        val dialog = CusDialogProg(context);
        dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(activity, WebService.club_member_action, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        adapter_applicant?.removeApplicent(pos)
                        if(clubz.user_id.equals(ClubZ.currentUser?.id)){
                            getOwnClubMembers()
                        }else getClubMembers()

                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("clubId",clubz.clubId);
               // params.put("clubUserId",member!!.clubUserId);
                params.put("userId",member!!.userId);
                params.put("answerStatus", status!!);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }


    override fun onTagAdd(tag: String?, member: ClubMember?, pos: Int) {
        val dialog = CusDialogProg(context);
        dialog.show();
        object : VolleyGetPost(activity, WebService.club_add_member_Tag, false) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        adapterOwnClubMember?.addTag(tag, pos)

                    } else {
                        Util.showToast(obj.getString("message"), context)
                    }
                } catch (ex: Exception) {
                    // Util.showToast(R.string.swr, context)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss();
            }

            override fun onNetError() {
                dialog.dismiss();
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("tagName", tag!!);
                params.put("clubUserId",member!!.clubUserId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language", SessionManager.getObj().getLanguage());
                return params
            }
        }.execute()
    }
}