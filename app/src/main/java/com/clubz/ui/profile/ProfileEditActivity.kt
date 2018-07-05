package com.clubz.ui.profile

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.ListPopupWindow
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Profile
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.FlowLayout
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import org.json.JSONObject

class ProfileEditActivity : AppCompatActivity(), View.OnClickListener, AppBarLayout.OnOffsetChangedListener  {

    lateinit var profile : Profile

    val fullName = ""
    val aboutMe = ""
    val contactNo = ""
    val dob = ""
    val email = ""
    var aboutMeVisibility = 0
    var dobVisibility = 0
    var contactNoVisibility = 0
    var emailVisibility = 0
    var affiliatesVisibility = 0
    var skillsVisibility = 0
    var interestVisibility = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        intent.let {
            if (intent.hasExtra("profile")) profile = it.extras.getSerializable("profile") as Profile
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val diametric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(diametric)

        val face =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.teko_medium)
                else Typeface.createFromAsset(assets, "teko_medium.ttf")
        collapse_toolbar.run {
            setCollapsedTitleTypeface(face)
            setExpandedTitleTypeface(face)
        }
        toolbar_image.layoutParams.height = diametric.widthPixels
        initView()
    }

    private fun initView() {
        tvAboutMeVisibility.text = getString(R.string.Public)
        tvDobVisibility.text = getString(R.string.Public)
        tvLandLineVisibility.text = getString(R.string.Public)
        tvMobileVisibility.text = getString(R.string.Public)
        tvEmailVisibility.text = getString(R.string.Public)
        tvAffilitesVisibility.text = getString(R.string.Public)
        tvMySkillVisibility.text = getString(R.string.Public)
        tvMyInterestVisibility.text = getString(R.string.Public)

        tvAboutMeVisibility.setOnClickListener(this)
        tvDobVisibility.setOnClickListener(this)
        tvLandLineVisibility.setOnClickListener(this)
        tvMobileVisibility.setOnClickListener(this)
        tvEmailVisibility.setOnClickListener(this)
        tvAffilitesVisibility.setOnClickListener(this)
        tvMySkillVisibility.setOnClickListener(this)
        tvMyInterestVisibility.setOnClickListener(this)
        appbar_layout!!.addOnOffsetChangedListener(this)

        tvAboutMe.setText(profile.about_me)
        tvDob.text = profile.getFormatedDOB() //"13/08/1989"
        tv_landLine.setText(profile.contact_no)
        tv_mobileNo.setText(profile.contact_no)
        tv_email.setText(profile.email)
        collapse_toolbar.title = profile.full_name

        if (profile.profile_image.isNotBlank()) {
            Picasso.with(this).load(profile.profile_image).into(toolbar_image, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    setPlated()
                }

                override fun onError() {
                    setPlated()
                }
            })
        }

        let {
            addChip(affilitesChip, profile.affiliates)
            addChip(skillsChip, profile.skills)
            addChip(interestChip, profile.interests)
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tvAboutMeVisibility ->{ showVisibilityMenu(tvAboutMeVisibility)}
            R.id.tvDobVisibility ->{ showVisibilityMenu(tvDobVisibility)}
            R.id.tvLandLineVisibility ->{ showVisibilityMenu(tvLandLineVisibility)}
            R.id.tvMobileVisibility ->{ showVisibilityMenu(tvMobileVisibility)}
            R.id.tvEmailVisibility ->{ showVisibilityMenu(tvEmailVisibility)}
            R.id.tvAffilitesVisibility ->{ showVisibilityMenu(tvAffilitesVisibility)}
            R.id.tvMySkillVisibility ->{ showVisibilityMenu(tvMySkillVisibility)}
            R.id.tvMyInterestVisibility ->{ showVisibilityMenu(tvMyInterestVisibility)}
        }
    }


    private fun getVisibilityOption(position: Int):String{
       return when (position) {
           0 -> return getString(R.string.hiddne)
           1 -> return getString(R.string.Public)
           2 -> return getString(R.string.only_for_my_contact)
           3 -> return getString(R.string.only_for_club_member)
           else -> getString(R.string.Public)
       }
    }

    private fun addChip(chipHolder: FlowLayout, str: String) {
        if (str.isNotBlank()) {
            val tagList = str.split(",").map { it.trim() }
            for (tag in tagList) {
                val chip = object : ChipView(this@ProfileEditActivity, chipHolder.childCount.toString(), true) {
                    override fun getLayout(): Int {
                        return R.layout.z_cus_chip_view_profile
                    }

                    override fun setDeleteListner(chipView: ChipView?) {
                    }
                }
                chip.text = tag
                chipHolder.addView(chip)
            }
        }
    }

    private fun setPlated() {
        //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.dharmrja)
        val bitmap = (toolbar_image.drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette: Palette? ->
            val mutedColor = palette?.getMutedColor(ContextCompat.getColor(this, R.color.primaryColor))
            collapse_toolbar.setContentScrimColor(mutedColor!!)
            collapse_toolbar.setStatusBarScrimColor(mutedColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = mutedColor
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()
        if (percentage > 0.95) {
            ivProfileImage.visibility = View.GONE
        } else {
            ivProfileImage.visibility = View.VISIBLE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_profile_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val m = menu.getItem(0)
        m?.isEnabled = true
        val drawable = m?.icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
        }
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_chat -> return true
        }
        if (item.title === "Add") {
            Toast.makeText(this, "clicked add", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showVisibilityMenu(v:View){
        val products =  arrayOf(getString(R.string.hiddne), getString(R.string.club_public),
                getString(R.string.only_for_my_contact), getString(R.string.only_for_club_member))
        val lpw =  ListPopupWindow(this@ProfileEditActivity)
        lpw.anchorView = v
        lpw.setDropDownGravity(Gravity.RIGHT)
        lpw.height = ListPopupWindow.WRAP_CONTENT
        lpw.width = resources.getDimension(R.dimen._160sdp).toInt()
        lpw.setAdapter(ArrayAdapter(this@ProfileEditActivity, android.R.layout.simple_list_item_1, products)) // list_item is your textView with gravity.
        lpw.setOnItemClickListener { parent, view, position, id ->
            lpw.dismiss()

            when(v.id){
                R.id.tvAboutMeVisibility -> { aboutMeVisibility = position
                    tvAboutMeVisibility.text = getVisibilityOption(position)}

                R.id.tvDobVisibility ->{ dobVisibility = position
                    tvDobVisibility.text = getVisibilityOption(position)}

                R.id.tvLandLineVisibility ->{ contactNoVisibility = position
                    tvLandLineVisibility.text = getVisibilityOption(position)}

                R.id.tvMobileVisibility ->{ aboutMeVisibility = position
                    tvMobileVisibility.text = getVisibilityOption(position)}

                R.id.tvEmailVisibility ->{ emailVisibility = position
                    tvEmailVisibility.text = getVisibilityOption(position)}

                R.id.tvAffilitesVisibility ->{ affiliatesVisibility = position
                    tvAffilitesVisibility.text = getVisibilityOption(position)}

                R.id.tvMySkillVisibility ->{ affiliatesVisibility = position
                    tvMySkillVisibility.text = getVisibilityOption(position)}

                R.id.tvMyInterestVisibility ->{ interestVisibility = position
                    tvMyInterestVisibility.text = getVisibilityOption(position)}
            }
        }
        lpw.show()
    }


    private fun updateProfile() {
        val dialog = CusDialogProg(this@ProfileEditActivity)
        dialog.show()
        object : VolleyGetPost(this@ProfileEditActivity, WebService.update_profile, false) {

            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    Log.d("profile", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        profile = Gson().fromJson(obj.getString("data"), Profile::class.java)
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, this@ProfileEditActivity)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss(); }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                params["fullName"] = fullName
                params["aboutMe"] = aboutMe
                params["contactNo"] = contactNo
                params["countryCode"] = profile.country_code
                params["dob"] = dob
                params["email"] = email
                params["skills"] = ""
                params["addAffiliates"] = ""
                params["aboutMeVisibility"] = aboutMeVisibility.toString()
                params["dobVisibility"] = dobVisibility.toString()
                params["contactNoVisibility"] = contactNoVisibility.toString()
                params["emailVisibility"] = emailVisibility.toString()
                params["affiliatesVisibility"] = affiliatesVisibility.toString()
                params["skillsVisibility"] = skillsVisibility.toString()
                params["interestVisibility"] = interestVisibility.toString()
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
