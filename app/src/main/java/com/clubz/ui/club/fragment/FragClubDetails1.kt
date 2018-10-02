package com.clubz.ui.club.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.TermsConditionDialog
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.data.model.Clubs
import com.clubz.utils.CircleTransform
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_club_details1.*
import org.json.JSONObject

/**
 * Created by mindiii on २१/३/१८.
 */
class FragClubDetails1 : Fragment() {
    lateinit var clubz: Clubs

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_club_details1, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        getClubDetails()
        view.setOnClickListener {}

        tv_terms.setOnClickListener {
            object : TermsConditionDialog(context!!, resources.getString(R.string.terms_conditions), clubz.terms_conditions) {
                override fun onCloseClicked() {
                    this.dismiss()
                }
            }.show()
        }
    }

    private fun setView() {
        titile_name.setText(clubz.club_name)
        club_category.text = clubz.club_category_name
        img_privacy.setImageResource(if (clubz.club_type == "1") R.drawable.ic_lock_open else R.drawable.ic_lock_black_24dp)
        tv_privacy.setText(if (clubz.club_type == "1") R.string.Public else R.string.Private)
        club_email.setText(clubz.club_email)
        club_phone.setText(String.format("%s %s", clubz.club_country_code, clubz.club_contact_no))
        club_city.setText(clubz.club_city)
        club_address.setText(clubz.club_location)
        club_web.setText(clubz.club_website)
        tvLeadby.text = clubz.full_name
        members.text = String.format("%d %s", 1, getString(R.string.members))
        try {
            foundation_date.setText(String.format("%s", Util.convertDate2(clubz.club_foundation_date)))
        } catch (ex: Exception) {
        }
        etv_description.setText(clubz.club_description)


        if (clubz.profile_image.isEmpty())
            Glide.with(image_member2.context).load(R.drawable.ic_user_white).into(image_member2)
        else
            Glide.with(image_member2.context).load(clubz.profile_image).into(image_member2)

        /*if(clubz.profile_image.isNotEmpty()){
            Picasso.with(image_member2.context).load(clubz.profile_image)
                    .transform(CircleTransform()).placeholder(R.drawable.ic_user_shape)
                    .fit().into(image_member2, object : Callback {
                        override fun onSuccess() {
                            image_member2?.setPadding(0,0,0,0)
                        }

                        override fun onError() { }
                    })
        }
*/
        try {
            if (clubz.club_image.isNotEmpty()) {
                img_club.visibility=View.VISIBLE
                Glide.with(img_club.context).load(clubz.club_image).into(img_club)
            }else{
                img_club.visibility=View.GONE
            }
        } catch (ex: Exception) {
        }
    }

    fun setData(clubz: Clubs): Fragment {
        this.clubz = clubz
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        ClubZ.instance.cancelPendingRequests(WebService.club_detail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ClubZ.instance.cancelPendingRequests(WebService.club_detail)
    }

    private fun getClubDetails() {
        val dialog = CusDialogProg(context)
        dialog.show()
        object : VolleyGetPost(activity, activity, "${WebService.club_detail}?clubId=${clubz.clubId}", true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        val clubz = Gson().fromJson<Clubs>(obj.getString("clubDetail"), Clubs::class.java)
                        members.text = String.format("%d %s", clubz.members + 1, getString(R.string.members))
                        club_city.setText(clubz.club_city)
                        /* Picasso.with(image_member2.context).load(clubz.profile_image).transform(CircleTransform()).placeholder(R.drawable.ic_user_shape)
                                 .fit().into(image_member2, object : Callback {
                             override fun onSuccess() {
                                 image_member2?.setPadding(0,0,0,0)
                             }

                             override fun onError() { }
                         })*/

                        if (!clubz.club_icon.endsWith("clubDefault.png")) {
                            Glide.with(image_icon.context).load(clubz.club_icon).into(image_icon)
                            /*Glide.with(image_icon.context).load(clubz.club_icon).transform(CircleTransform()).into(image_icon, object : Callback {
                                override fun onSuccess() {
                                    image_icon?.setPadding(0,0,0,0)
                                }

                                override fun onError() { }
                            })*/
                            /*val padding = resources.getDimension(R.dimen._8sdp).toInt()
                            image_icon.setPadding(padding, padding, padding, padding)
                            image_icon.background = ContextCompat.getDrawable(context, R.drawable.ic_shield_outline)
                            image_icon.setImageResource(R.drawable.ic_user_shape)*/
                        }
                        tvLeadby.text = clubz.user_role
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
//                params.put("clubId",clubz.clubId);
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute(WebService.club_detail)
    }
}