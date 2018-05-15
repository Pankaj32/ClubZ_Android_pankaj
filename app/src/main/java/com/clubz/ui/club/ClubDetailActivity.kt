package com.clubz.ui.club

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.R
import com.clubz.data.model.Clubs
import com.clubz.ui.club.fragment.Frag_ClubDetails_1
import com.clubz.ui.club.fragment.Frag_ClubDetails_2
import kotlinx.android.synthetic.main.activity_club_detail.*

fun Context.ClubDetailIntent( clubz : Clubs): Intent {
    return Intent(this, ClubDetailActivity::class.java).apply {
        putExtra(INTENT_CLUBZ, clubz)
    }
}

private const val INTENT_CLUBZ = "clubz"

class ClubDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var clubz : Clubs;
    lateinit var adapter : ViewPagerAdapter

    fun newIntent(context: Context, clubz : Clubs): Intent {
        val intent = Intent(context, ClubDetailActivity::class.java)
        intent.putExtra(INTENT_CLUBZ, clubz)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_detail)

        val intent = getIntent()
        clubz = intent!!.extras.getSerializable("clubz") as Clubs
        requireNotNull(clubz) { "no user_id provided in Intent extras" }

        title_tv.text = clubz.club_name
        for (views in arrayOf(backBtn)) views.setOnClickListener(this)
        setViewPager(view_pager_cd)
        tablayout_cd.setupWithViewPager(view_pager_cd)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragger( Frag_ClubDetails_1().setData(clubz),resources.getString(R.string.t_detils) , " This is First")
        adapter.addFragger( Frag_ClubDetails_2().setData(clubz),resources.getString(R.string.t_members) , " This is second")
        viewPager.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.backBtn ->{ onBackPressed() }
        }
    }



    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        //Here We need to implement getCount and GetItem methods
        //And for implementing This We are Creating ArrayList for adding Fragment and For Adding Title
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragger(fragment: Fragment, title: String, menu: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        //This is The method which is Overrided by us
        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        fun setTile(position: Int, fragment: Fragment, title: String) {
            mFragmentList[position] = fragment
            mFragmentTitleList[position] = title
        }
    }




    /* fun getOwnClubMembers(){
         val dialog = CusDialogProg(this);
         dialog.show();   // ?clubId=66&offset=0&limit=10
         object : VolleyGetPost(this,"${WebService.club_member_list}?clubId=${clubz.clubId}",true){
             override fun onVolleyResponse(response: String?) {
                 try {
                     dialog.dismiss();
                     val obj = JSONObject(response)
                     if (obj.getString("status").equals("success")) {
                         memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                         adapterOwnClubMember?.setMemberList(memberList)

                     } else {
                         Util.showToast(obj.getString("message"),this@ClubDetailActivity)
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
         object : VolleyGetPost(this, "${WebService.club_member_list}?clubId=${clubz.clubId}", true) {
             override fun onVolleyResponse(response: String?) {
                 try {
                     //dialog.dismiss();
                     val obj = JSONObject(response)
                     if (obj.getString("status").equals("success")) {
                         memberList = Gson().fromJson<List<ClubMember>>(obj.getString("data"), Type_Token.club_member_list)
                         adapterClubMember?.setMemberList(memberList)
                     } else {
                         Util.showToast(obj.getString("message"), this@ClubDetailActivity)
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
         val dialog = CusDialogProg(this@ClubDetailActivity);
         dialog.show();   // ?clubId=66&offset=0&limit=10
         object : VolleyGetPost(this@ClubDetailActivity, "${WebService.club_applicant_list}?clubId=${clubz.clubId}", true) {
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
         val dialog = CusDialogProg(this);
         dialog.show();   // ?clubId=66&offset=0&limit=10
         object : VolleyGetPost(this, WebService.club_updateMemberStatus, false) {
             override fun onVolleyResponse(response: String?) {
                 try {
                     dialog.dismiss();
                     val obj = JSONObject(response)
                     if (obj.getString("status").equals("success")) {
                         member?.member_status = obj.getString("member_status")
                         adapterOwnClubMember?.updateMember(member, pos)
                     } else {
                         Util.showToast(obj.getString("message"), this@ClubDetailActivity)
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
         val dialog = CusDialogProg(this);
         dialog.show();   // ?clubId=66&offset=0&limit=10
         object : VolleyGetPost(this, WebService.club_member_action, false) {
             override fun onVolleyResponse(response: String?) {
                 try {
                     dialog.dismiss();
                     val obj = JSONObject(response)
                     if (obj.getString("status").equals("success")) {
                         adapter_applicant?.removeApplicent(pos)

                     } else {
                         Util.showToast(obj.getString("message"), this@ClubDetailActivity)
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
     }*/
}
