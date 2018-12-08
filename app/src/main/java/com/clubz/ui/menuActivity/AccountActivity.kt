package com.clubz.ui.menuActivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.ArrayAdapter
import com.clubz.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_account.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.clubz.data.local.pref.SessionManager
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Util


class AccountActivity : AppCompatActivity(), View.OnClickListener {
    private var langSpinnAdapter: ArrayAdapter<String>? = null
    private var deleteSpinnAdapter: ArrayAdapter<String>? = null
    val langArrayList = ArrayList<String>()
    val deleteArrayList = ArrayList<String>()
    var ENGLISH_LOCALE="en"
    var SPANISH_LOCALE="es"
    var mIsSpinnerFirstCall = true
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.checklaunage(this)
        setContentView(R.layout.activity_account)
        Setuserdata();

        ivBack.setOnClickListener(this)
        autocompleteFragment = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                mainLocation.setText(p0!!.address)
                /*latitute = p0!!.latLng.latitude.toString()
                longitute = p0!!.latLng.longitude.toString()*/
            }

            override fun onError(p0: Status?) {

            }

        })

        spinnerLang.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                // your code here

                var Selectedlan = langArrayList[position]

                if(!mIsSpinnerFirstCall) {

                    if (Selectedlan.equals("English") || Selectedlan.equals("Inglés")) {
                        SessionManager.getObj().language = ENGLISH_LOCALE
                        Util.checklaunage(this@AccountActivity)
                        startActivity(Intent(this@AccountActivity, HomeActivity::class.java))
                        finish()
                    }
                    if (Selectedlan.equals("Spanish") || Selectedlan.equals("Español")) {

                        SessionManager.getObj().language = SPANISH_LOCALE
                        Util.checklaunage(this@AccountActivity)
                        startActivity(Intent(this@AccountActivity, HomeActivity::class.java))
                        finish()
                    }

                }
                mIsSpinnerFirstCall = false;


            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        })
        iv_arrow_expand.setOnClickListener(View.OnClickListener {

            if(plan_layout.visibility == GONE){
                iv_arrow_expand.setImageResource(R.drawable.ic_event_up_arrow)
                Util.setRotation(iv_arrow_expand, true)
                plan_layout.setVisibility(View.VISIBLE)
            }
            else{
                iv_arrow_expand.setImageResource(R.drawable.ic_event_down_arrow)
                Util.setRotation(iv_arrow_expand, true)
                plan_layout.setVisibility(View.GONE)
            }

        })



    }

    fun Setuserdata(){
        var getlanguage =  SessionManager.getObj().language

        if(getlanguage.equals("en")||getlanguage.equals("")){
            langArrayList.add(getString(R.string.english))
            langArrayList.add(getString(R.string.spanish))
        }
        else{
            langArrayList.add(getString(R.string.spanish))
            langArrayList.add(getString(R.string.english))
        }

        deleteArrayList.add(getString(R.string.no_way))
        langSpinnAdapter = ArrayAdapter(this@AccountActivity, R.layout.spinner_item, R.id.spinnText, langArrayList)
        deleteSpinnAdapter = ArrayAdapter(this@AccountActivity, R.layout.spinner_item, R.id.spinnText, deleteArrayList)
        spinnerLang.adapter = langSpinnAdapter
        spinnerDeleteAccount.adapter = deleteSpinnAdapter

        if(SessionManager.getObj().user!=null){
             tvPhNo.setText(SessionManager.getObj().user.country_code+" "+SessionManager.getObj().user.contact_no)
            if(!SessionManager.getObj().user.userCity.equals("")){
                mainLocation.setText(SessionManager.getObj().user.userCity)
            }

        }
        if(SessionManager.getObj().membershipPlan!=null){
            if(!SessionManager.getObj().membershipPlan.plan_name.equals("")){
                txt_privacy.setText(SessionManager.getObj().membershipPlan.plan_name)
            }
            if(!SessionManager.getObj().membershipPlan.plan_type.equals("")){
                plan_type_text.setText(SessionManager.getObj().membershipPlan.plan_type)
            }
            if(!SessionManager.getObj().membershipPlan.plan_price.equals("")&&!SessionManager.getObj().membershipPlan.plan_price.equals("0")){
                plan_type_text.setText("$ "+SessionManager.getObj().membershipPlan.plan_price+" "+SessionManager.getObj().membershipPlan.plan_type)
            }

            if(!SessionManager.getObj().membershipPlan.club_create.equals("")&&!SessionManager.getObj().membershipPlan.club_create.equals("0")){
                tvClubname.setText(R.string.create_search_and_join_club_you_like)
            }
            if(!SessionManager.getObj().membershipPlan.news_create.equals("")&&!SessionManager.getObj().membershipPlan.news_create.equals("0")){
                tvNewsname.setText(R.string.create_stay_update_with_the_clubs_you_belong)
            }
            if(!SessionManager.getObj().membershipPlan.activity_create.equals("")&&!SessionManager.getObj().membershipPlan.activity_create.equals("0")){
                tvActivityname.setText(R.string.create_join_those_activities_that_you_like_so_much)
            }
            if(!SessionManager.getObj().membershipPlan.chat_create.equals("")&&!SessionManager.getObj().membershipPlan.chat_create.equals("1")){
                tvChatname.setText(R.string.cannot_stay_in_contact_with_your_partners_neighbors_or_friends)
            }
            if(!SessionManager.getObj().membershipPlan.ads_create.equals("")&&!SessionManager.getObj().membershipPlan.ads_create.equals("1")){
                tvAdsname.setText(R.string.cannot_promote_your_things_fast_and_easy)
            }

        }


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }
}