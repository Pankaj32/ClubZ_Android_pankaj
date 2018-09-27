package com.clubz.ui.profile

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import android.view.View.inflate
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Profile
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.ChipEditText
import com.clubz.ui.cv.ChipView
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.FlowLayout
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile_edit.*
import org.json.JSONObject
import kotlin.math.log

class ProfileEditActivity : AppCompatActivity(), View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    lateinit var profile: Profile

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
        // typeAffliates.imeOptions=EditorInfo.IME_ACTION_DONE
        /*typeAffliates.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addhorizontalview(cheepContainer, typeAffliates.text.toString().trim())
                    typeAffliates.setText("")
                    return true
                }
                return false
            }

        })*/
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
//        plus.setOnClickListener(this)
        appbar_layout!!.addOnOffsetChangedListener(this)

        tvAboutMe.setText(profile.about_me)
        tvDob.text = profile.getFormatedDOB() //"13/08/1989"
        tv_landLine.setText(profile.contact_no)
        tv_mobileNo.setText(profile.contact_no)
        tv_email.setText(profile.email)
        collapse_toolbar.title = profile.full_name

        if (profile.profile_image.isNotBlank()) {
            Glide.with(this).load(profile.profile_image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            setPlated()
                            return false
                        }
                    })
                    .into(toolbar_image)
        }

        let {
            addChip(affilitesChip, profile.affiliates)
//            addhorizontalview(cheepContainer, profile.affiliates)
            addChip(skillsChip, profile.skills)
            addChip(interestChip, profile.interests)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvAboutMeVisibility -> {
                showVisibilityMenu(tvAboutMeVisibility)
            }
            R.id.tvDobVisibility -> {
                showVisibilityMenu(tvDobVisibility)
            }
            R.id.tvLandLineVisibility -> {
                showVisibilityMenu(tvLandLineVisibility)
            }
            R.id.tvMobileVisibility -> {
                showVisibilityMenu(tvMobileVisibility)
            }
            R.id.tvEmailVisibility -> {
                showVisibilityMenu(tvEmailVisibility)
            }
            R.id.tvAffilitesVisibility -> {
                showVisibilityMenu(tvAffilitesVisibility)
            }
            R.id.tvMySkillVisibility -> {
                showVisibilityMenu(tvMySkillVisibility)
            }
            R.id.tvMyInterestVisibility -> {
                showVisibilityMenu(tvMyInterestVisibility)
            }
            /* R.id.plus -> {
                 if (canAdd()) addChip(affilitesChip, affiliates.text.toString())
                 affiliates.setText("")
             }*/
        }
    }


    private fun getVisibilityOption(position: Int): String {
        return when (position) {
            0 -> return getString(R.string.hidden)
            1 -> return getString(R.string.Public)
            2 -> return getString(R.string.only_for_my_contact)
            3 -> return getString(R.string.only_for_club_member)
            else -> getString(R.string.Public)
        }
    }

    /*private fun canAdd(): Boolean {
        //  Util.hideKeyBoard()
        if (affiliates.text.isBlank()) {
            Util.showSnake(this, cordinator_layout, R.string.a_addaffil)
            return false
        }
        return true
    }
*/
    private fun addChip(chipHolder: FlowLayout, str: String) {
        if (str.isNotBlank()) {
            val tagList = str.split(",").map { it.trim() }
            Log.e("ChildCount: ", "" + chipHolder.childCount)
            if (chipHolder.childCount != 0) {
                chipHolder.removeViewAt(chipHolder.childCount - 1)
            }
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
            val chipEditText = object : ChipEditText(this@ProfileEditActivity) {
                override fun setDone(text: String?) {
                    addChip(affilitesChip, text!!)
                    hideSoftKeyboard()
                }

            }
            chipHolder.addView(chipEditText)
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

    fun hideSoftKeyboard() {
        try {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    private fun showVisibilityMenu(v: View) {
        val products = arrayOf(getString(R.string.hidden), getString(R.string.club_public),
                getString(R.string.only_for_my_contact), getString(R.string.only_for_club_member))
        val lpw = ListPopupWindow(this@ProfileEditActivity)
        lpw.anchorView = v
        lpw.setDropDownGravity(Gravity.RIGHT)
        lpw.height = ListPopupWindow.WRAP_CONTENT
        lpw.width = resources.getDimension(R.dimen._160sdp).toInt()
        lpw.setAdapter(ArrayAdapter(this@ProfileEditActivity, android.R.layout.simple_list_item_1, products)) // list_item is your textView with gravity.
        lpw.setOnItemClickListener { parent, view, position, id ->
            lpw.dismiss()

            when (v.id) {
                R.id.tvAboutMeVisibility -> {
                    aboutMeVisibility = position
                    tvAboutMeVisibility.text = getVisibilityOption(position)
                }

                R.id.tvDobVisibility -> {
                    dobVisibility = position
                    tvDobVisibility.text = getVisibilityOption(position)
                }

                R.id.tvLandLineVisibility -> {
                    contactNoVisibility = position
                    tvLandLineVisibility.text = getVisibilityOption(position)
                }

                R.id.tvMobileVisibility -> {
                    aboutMeVisibility = position
                    tvMobileVisibility.text = getVisibilityOption(position)
                }

                R.id.tvEmailVisibility -> {
                    emailVisibility = position
                    tvEmailVisibility.text = getVisibilityOption(position)
                }

                R.id.tvAffilitesVisibility -> {
                    affiliatesVisibility = position
                    tvAffilitesVisibility.text = getVisibilityOption(position)
                }

                R.id.tvMySkillVisibility -> {
                    affiliatesVisibility = position
                    tvMySkillVisibility.text = getVisibilityOption(position)
                }

                R.id.tvMyInterestVisibility -> {
                    interestVisibility = position
                    tvMyInterestVisibility.text = getVisibilityOption(position)
                }
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
