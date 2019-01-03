package com.clubz.ui.menuActivity

import android.annotation.SuppressLint
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
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Address
import com.clubz.data.model.NotificationSesssion
import com.clubz.data.model.UserLocation
import com.clubz.data.remote.GioAddressTask
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Util
import com.clubz.utils.Util.Companion.showToast
import com.clubz.utils.VolleyGetPost
import org.json.JSONObject


class AccountActivity : AppCompatActivity(), View.OnClickListener {
    private var langSpinnAdapter: ArrayAdapter<String>? = null
    private var deleteSpinnAdapter: ArrayAdapter<String>? = null
    val langArrayList = ArrayList<String>()
    val deleteArrayList = ArrayList<String>()
    var ENGLISH_LOCALE="en"
    var SPANISH_LOCALE="es"
    var mIsSpinnerFirstCall = true
    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private val ARG_HISTORY_PIC = "historyPic"
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
                var  latitute = p0!!.latLng.latitude.toString()
                var  longitute = p0!!.latLng.longitude.toString()
                val userLocation = UserLocation()
                userLocation.city = ""
                userLocation.latitude = p0!!.latLng.latitude
                userLocation.longitude = p0!!.latLng.longitude
                userLocation.address = p0!!.address.toString()
                userLocation.isLocationAvailable = true
                val task = @SuppressLint("StaticFieldLeak")
                object : GioAddressTask(this@AccountActivity) {
                    override fun onFail() {
                       // setLocation(latitute,longitute,"",p0!!.address.toString())
                        Toast.makeText(this@AccountActivity, resources.getString(R.string.location_wrong_alert), Toast.LENGTH_SHORT).show();
                    }

                    override fun onSuccess(address: Address) {
                        ClubZ.city = address.city.toString()
                        ClubZ.latitude = p0!!.latLng.latitude
                        ClubZ.longitude = p0!!.latLng.longitude
                        userLocation.city = ClubZ.city
                        mainLocation.setText(address.city.toString())
                        SessionManager.getObj().setLocation(userLocation)
                        SessionManager.getObj().setCity(address.city.toString())
                        setLocation(latitute,longitute,address.city.toString(),p0!!.address.toString())
                    }
                }
                task.execute(p0!!.latLng.latitude, p0!!.latLng.longitude)
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

      contact_us.setOnClickListener(View.OnClickListener {

          var Adminuserid = SessionManager.getObj().user.clubz_owner_id
          var userid = SessionManager.getObj().user.id

          if (!Adminuserid.equals(userid)) {
              startActivity(Intent(this, AllChatActivity::class.java)
                      .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                      .putExtra(ARG_HISTORY_ID, SessionManager.getObj().user.clubz_owner_id)
                      .putExtra(ARG_HISTORY_NAME, SessionManager.getObj().user.clubz_owner_name)
                      .putExtra(ARG_HISTORY_PIC, SessionManager.getObj().user.clubz_owner_profileImage)
              )
          } else {
              Toast.makeText(this, resources.getString(R.string.owner_alert_message), Toast.LENGTH_SHORT).show();
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
            if(!SessionManager.getObj().getCity().equals("")){
                mainLocation.setText(SessionManager.getObj().getCity())
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

    private fun setLocation(lat: String, longv :String,cityname: String, addressname :String) {
        val dialog = CusDialogProg(this@AccountActivity)
        dialog.show()
        object : VolleyGetPost(this@AccountActivity, WebService.updateLocation, false,
                true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {


                    }
                    else{
                        Toast.makeText(this@AccountActivity, obj.getString("message"), Toast.LENGTH_LONG).show()

                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()

                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["city"] =cityname
                params["latitude"] =  lat
                params["longitude"] =  longv
                params["address"] =  addressname

                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                //params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }
}