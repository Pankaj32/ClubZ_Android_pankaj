package com.clubz.fragment.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.Cus_Views.CusDialogProg
import com.clubz.R
import com.clubz.helper.SessionManager
import com.clubz.helper.WebService
import com.clubz.model.Clubs
import com.clubz.model.User
import com.clubz.util.CircleTransform
import com.clubz.util.Util
import com.clubz.util.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_club_details1.*
import org.json.JSONObject

/**
 * Created by mindiii on २१/३/१८.
 */
class Frag_ClubDetails_1 : Fragment() {
    lateinit var clubz :Clubs;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_club_details1,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        getClubDetails()
        view!!.setOnClickListener{}
    }

    fun setView(){
        titile_name.setText(clubz.club_name)
        club_category.setText(clubz.club_category_name)
        img_privacy.setImageResource(if(clubz.club_type.equals("1")) R.drawable.ic_unlocked_padlock_black else R.drawable.ic_locked_padlock_black)
        privacy_status.setText(if(clubz.club_type.equals("1")) R.string.Public else R.string.Private)
        club_email.setText(clubz.club_email)
        club_phone.setText(clubz.club_country_code+""+clubz.club_contact_no)
        club_address.setText(clubz.club_address)
        club_web.setText(clubz.club_website)
        username.setText(clubz.full_name)
        members.setText("0 "+resources.getString(R.string.members))
        try {
            date.setText(resources.getString(R.string.since)+" "+Util.convertDate2(clubz.club_foundation_date))
        }catch (ex :Exception){}
        tv_descrip_detials.setText(clubz.club_description)
        try{
            Picasso.with(context).load(clubz.club_image).into(img_club)
        }catch (ex:Exception){

        }
    }

    fun setData(clubz: Clubs): Fragment {
        this.clubz = clubz
        return this
    }

    fun getClubDetails(){
        val dialog = CusDialogProg(context );
        dialog.show();
        object : VolleyGetPost(activity,activity,WebService.club_detail,false){
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss();
                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                       val clubz =  Gson().fromJson<Clubs>(obj.getString("clubDetail"), Clubs::class.java)
                        members.setText(clubz.members+" "+resources.getString(R.string.members))
                        Picasso.with(context).load(clubz.profile_image).transform(CircleTransform()).placeholder(R.drawable.ic_user_shape).into(image_member2, object : Callback {
                            override fun onSuccess() {
                                image_member2.setPadding(0,0,0,0)
                            }

                            override fun onError() {

                            }
                        })

                        if(!clubz.club_icon.endsWith("defaultProduct.png"))Picasso.with(context).load(clubz.club_icon).transform(CircleTransform()).into(image_icon, object : Callback {
                            override fun onSuccess() {
                                image_icon.setPadding(0,0,0,0)
                            }

                            override fun onError() {

                            }
                        })
                        usrerole.setText(clubz.user_role)
                    } else {
                        Util.showToast(obj.getString("message"),context)
                    }
                }catch (ex :Exception){
                    Util.showToast(R.string.swr,context)
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
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params.put("authToken", SessionManager.getObj().getUser().auth_token);
                params.put("language",SessionManager.getObj().getLanguage());
                return params

            }
        }.execute()

    }


}