package com.clubz.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.DialogMenu
import com.clubz.data.model.Profile
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.FlowLayout
import com.clubz.ui.newsfeed.fragment.FragNewsList
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.club_more_menu.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private var successfullyUpdate = 100
    private var collapsedMenu: Menu? = null
    private var appBarExpanded = true
    private var profile: Profile? = null
    private var isMyprofile = false
    private var isUpdatedProfile = false
    protected var menuDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        intent.let {
            if (intent.hasExtra("profile")) profile = it.extras.getSerializable("profile") as Profile
            isMyprofile = profile?.userId == ClubZ.currentUser!!.id
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val diametric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(diametric)

        val toolbarImage = findViewById<View>(R.id.toolbar_image) as ImageView
        val face =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.teko_medium)
                else Typeface.createFromAsset(assets, "teko_medium.ttf")
        collapse_toolbar.run {
            setCollapsedTitleTypeface(face)
            setExpandedTitleTypeface(face)
        }
        toolbarImage.layoutParams.height = diametric.widthPixels
        initView()
        appbar_layout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appbar_layout.getTotalScrollRange()
                }
                if (scrollRange + verticalOffset == 0) {
                    belloLay.visibility = View.GONE
                    isShow = true
                } else if (isShow) {
                    belloLay.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
        getProfile()
        /*val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${user?.country_code} ${user?.contact_no}"))
        context.startActivity(intent)*/
        tv_landLine.setOnClickListener(this)
        tv_phoneNo.setOnClickListener(this)
        tv_email.setOnClickListener(this)

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

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_landLine -> {
                if (profile!!.landline_no.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${profile?.landline_no}"))
                    startActivity(intent)
                }
            }
            R.id.tv_phoneNo -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "${profile?.country_code} ${profile?.contact_no}"))
                startActivity(intent)
            }
            R.id.tv_email -> {
                val email = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
                // prompts email clients only

                email.type = "message/rfc822"
                /*email.putExtra(Intent.EXTRA_EMAIL, recipients)
                email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString())
                email.putExtra(Intent.EXTRA_TEXT, body.getText().toString())*/
                try {

                    // the user can choose the email client
                    startActivity(Intent.createChooser(email, "Choose an email client from..."))
                } catch (ex: android.content.ActivityNotFoundException) {
                    Toast.makeText(this@ProfileActivity, "No email client installed.",
                            Toast.LENGTH_LONG).show()

                }

            }
        }

    }

    private fun initView() {
        collapse_toolbar.title = profile!!.full_name
        if (!profile!!.profile_image.isBlank()) {
            if (!profile!!.profile_image.endsWith("defaultUser.png")) {
                Picasso.with(this).load(profile!!.profile_image)
                        .fit()
                        .into(toolbar_image, object : Callback {
                            override fun onSuccess() {
                                setPlated()
                            }

                            override fun onError() {
                                try {
                                    setPlated()
                                }catch (e:Exception){

                                }
                            }
                        })
            }
        }
        if (isMyprofile) {
            ll_silenceUser.visibility = View.GONE
            ivChat.visibility = View.GONE
        } else {
            ll_silenceUser.visibility = View.VISIBLE
            ivChat.visibility = View.VISIBLE
            appbar_layout!!.addOnOffsetChangedListener(this)
        }
    }

    private fun updateView() {
        if (!profile!!.profile_image.isBlank()) {
            if (!profile!!.profile_image.endsWith("defaultUser.png")) {
                Picasso.with(this).load(profile!!.profile_image)
                        .fit()
                        .into(toolbar_image, object : Callback {
                            override fun onSuccess() {
                                setPlated()
                            }

                            override fun onError() {
                                try {
                                    setPlated()
                                }catch (e:Exception){

                                }
                            }
                        })
            }
        }

        collapse_toolbar.title = profile!!.full_name
        if (isMyprofile) {
            updateMyProfile()
        } else {
            updateOthersProfile()
        }
    }

    private fun updateOthersProfile() {
        if (profile!!.dob.equals("0000-00-00")) {
            dobCard.visibility = View.GONE
        } else {
            dobCard.visibility = View.VISIBLE
            if (profile!!.dob_visibility.equals("1")) {
                tvDob.text = Util.convertDobDate(profile!!.getFormatedDOB())
            } else dobCard.visibility = View.GONE
        }
        if (profile!!.about_me_visibility.equals("1")) {
            aboutCard.visibility = View.VISIBLE
            if (!profile!!.about_me.trim().equals("")) tvAboutMe.text = profile?.about_me
        } else aboutCard.visibility = View.GONE
        if (profile!!.contact_no_visibility.equals("1")) {
            phCard.visibility = View.VISIBLE
            tv_phoneNo.text = profile?.getContactNo()
        } else {
            phCard.visibility = View.GONE

        }
        if (profile!!.landline_no_visibility.equals("1")) {
            if (profile!!.landline_no.isNotEmpty()) {
                landCrd.visibility = View.VISIBLE
                tv_landLine.text = profile?.landline_no
            } else landCrd.visibility = View.GONE

        } else {
            landCrd.visibility = View.GONE
        }
        if (profile!!.email_visibility.equals("1")) {
            emaiCard.visibility = View.VISIBLE
            tv_email.text = profile?.email
        } else emaiCard.visibility = View.GONE

        if (profile!!.affiliates_visibility.equals("1")) {
            affliatesCard.visibility = View.VISIBLE
            if (!profile!!.affiliates.isEmpty()) {
                noAffiliatesTxt.visibility = View.GONE
                affilitesChip.visibility = View.VISIBLE
                if (affilitesChip.childCount != 0) affilitesChip.removeAllViews()
                addChip(affilitesChip, profile!!.affiliates)
            } else {
                noAffiliatesTxt.visibility = View.VISIBLE
                affilitesChip.visibility = View.GONE
            }
        } else affliatesCard.visibility = View.GONE
        if (profile!!.skills_visibility.equals("1")) {
            if (!profile!!.skills.isEmpty()) {
                noSkillTxt.visibility = View.GONE
                skillsChip.visibility = View.VISIBLE
                if (skillsChip.childCount != 0) skillsChip.removeAllViews()
                addChip(skillsChip, profile!!.skills)
            } else {
                noSkillTxt.visibility = View.VISIBLE
                skillsChip.visibility = View.GONE
            }
        } else skillCard.visibility = View.GONE
        if (profile!!.interest_visibility.equals("1")) {
            if (!profile!!.interests.isEmpty()) {
                noInterestTxt.visibility = View.GONE
                if (interestChip.childCount != 0) interestChip.removeAllViews()
                addChip(interestChip, profile!!.interests)
                interestChip.visibility = View.VISIBLE
            } else {
                noInterestTxt.visibility = View.VISIBLE
                interestChip.visibility = View.GONE
            }
        } else interestCard.visibility = View.GONE
    }

    fun updateMyProfile() {
        if (!profile!!.dob.equals("0000-00-00")) {
            tvDob.text = Util.convertDobDate(profile!!.getFormatedDOB())
        }
        if (!profile!!.about_me.trim().equals("")) tvAboutMe.text = profile?.about_me
        tv_phoneNo.text = profile?.getContactNo()

        tv_email.text = profile?.email

        if (!profile!!.landline_no.isEmpty()) tv_landLine.text = profile?.landline_no
        if (!profile!!.affiliates.isEmpty()) {
            noAffiliatesTxt.visibility = View.GONE
            affilitesChip.visibility = View.VISIBLE
            if (affilitesChip.childCount != 0) affilitesChip.removeAllViews()
            addChip(affilitesChip, profile!!.affiliates)
        } else {
            noAffiliatesTxt.visibility = View.VISIBLE
            affilitesChip.visibility = View.GONE
        }
        if (!profile!!.skills.isEmpty()) {
            noSkillTxt.visibility = View.GONE
            skillsChip.visibility = View.VISIBLE
            if (skillsChip.childCount != 0) skillsChip.removeAllViews()
            addChip(skillsChip, profile!!.skills)
        } else {
            noSkillTxt.visibility = View.VISIBLE
            skillsChip.visibility = View.GONE
        }
        if (!profile!!.interests.isEmpty()) {
            noInterestTxt.visibility = View.GONE
            if (interestChip.childCount != 0) interestChip.removeAllViews()
            addChip(interestChip, profile!!.interests)
            interestChip.visibility = View.VISIBLE
        } else {
            noInterestTxt.visibility = View.VISIBLE
            interestChip.visibility = View.GONE
        }
    }

    private fun addChip(chipHolder: FlowLayout, str: String) {

        val tagList = str.split(",").map { it.trim() }
        for (tag in tagList) {
            val chip = object : ChipView(this@ProfileActivity, chipHolder.childCount.toString(), false) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(if (isMyprofile) R.menu.my_profile_menu else R.menu.profile_menu, menu)
        collapsedMenu = menu
        return true
        /*if (isMyprofile) {
            menuInflater.inflate(R.menu. profile_menu, menu)
            collapsedMenu = menu
        }else{
            val list: ArrayList<DialogMenu> = arrayListOf()
            list.add(DialogMenu(getString(R.string.edit), R.drawable.ic_edit))
            showMenu(list)
        }
        return true*/
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        if (isMyprofile) {

        } else {
            if (collapsedMenu != null && (!appBarExpanded || collapsedMenu!!.size() != 1)) {
                //collapsed
                /*collapsedMenu!!.add("Chat")
                        .setIcon(R.drawable.ic_chat_outline)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)*/

                val m = menu.getItem(0)
                m?.isEnabled = true
                val drawable = m?.icon
                if (drawable != null) {
                    drawable.mutate()
                    drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                //expanded
                menu.getItem(0).isVisible = false

            }
            return super.onPrepareOptionsMenu(collapsedMenu)
        }

        return super.onPrepareOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onback()
                return true
            }
            R.id.action_chat -> return true
            R.id.action_edit -> {
                val list: ArrayList<DialogMenu> = arrayListOf()
                list.add(DialogMenu(getString(R.string.edit), R.drawable.ic_edit))
                showMenu(list)
                /*var intent = Intent(this@ProfileActivity, ProfileEditActivity::class.java)
                        .putExtra("profile", profile)
                startActivityForResult(intent, successfullyUpdate)*/
                //Toast.makeText(this@ProfileActivity, R.string.under_development, Toast.LENGTH_SHORT).show()
            }
        }
        if (item.title === "Add") {
            Toast.makeText(this, "clicked add", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {

        if (!isMyprofile) {
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()
            if (percentage > 0.95) {
                ivChat.visibility = View.GONE
                appBarExpanded = false
                invalidateOptionsMenu()
            } else {
                ivChat.visibility = View.VISIBLE
                appBarExpanded = true
                invalidateOptionsMenu()
            }
        }
    }


    private fun getProfile() {
        val dialog = CusDialogProg(this@ProfileActivity)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(this@ProfileActivity,
                WebService.get_profile + "?userId=" + profile!!.userId
                , true, true) {

            override fun onVolleyResponse(response: String?) {
                try {
                    dialog.dismiss()
                    Log.d("profile", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        profile = Gson().fromJson(obj.getString("data"), Profile::class.java)
                        updateView()
                    }
                } catch (ex: Exception) {
                    Util.showToast(R.string.swr, this@ProfileActivity)
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss(); }

            override fun onNetError() {
                dialog.dismiss()
            }

            override fun setParams(params: MutableMap<String, String>?): MutableMap<String, String> {
                return params!!
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().language
                return params
            }
        }.execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            successfullyUpdate -> {
                isUpdatedProfile = true
                getProfile()
            }
        }
    }

    override fun onBackPressed() {
        onback()
    }

    private fun onback() {
        if (isUpdatedProfile) {
            setResult(successfullyUpdate, Intent())
        }
        finish()
    }

    protected fun showMenu(list: ArrayList<DialogMenu>?) {
        if (menuDialog == null) {
            menuDialog = Dialog(this)
            menuDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogWindow = menuDialog?.window
            dialogWindow?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            menuDialog?.setContentView(R.layout.club_more_menu)

            if (list != null) {
                menuDialog?.ll_menu0?.visibility = View.VISIBLE
                menuDialog?.ll_menu1?.visibility = View.GONE
                menuDialog?.view?.visibility = View.GONE
                menuDialog?.menu_iv0?.setImageResource(list[0].id)
                menuDialog?.menu_tv0?.text = list[0].title
            }

            menuDialog?.ll_menu0?.setOnClickListener {
                menuDialog!!.dismiss()
                var intent = Intent(this@ProfileActivity, ProfileEditActivity::class.java)
                        .putExtra("profile", profile)
                startActivityForResult(intent, successfullyUpdate)
            }

            // for (views in arrayOf(menuDialog?.ll_menu1, menuDialog?.ll_menu2)) views?.setOnClickListener(this)
            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            menuDialog?.setCancelable(true)
        }
        menuDialog?.show()
    }
}
