package com.clubz.ui.profile

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.graphics.Palette
import android.support.v7.widget.ListPopupWindow
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.*
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.model.UserBean
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Profile
import com.clubz.data.model.User
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.AppHelper
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.cv.*
import com.clubz.utils.Constants
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.Util
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mvc.imagepicker.ImagePicker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

class ProfileEditActivity : AppCompatActivity(),
        View.OnClickListener,
        AppBarLayout.OnOffsetChangedListener {

    lateinit var profile: Profile
    private var successfullyUpdate = 100;

    var aboutMe = ""
    var dob = ""
    var updatedSkills = ""
    var updatedInterest = ""
    var removedAffiliates = ""
    var addedAffiliates = ""
    var aboutMeVisibility = 0
    var dobVisibility = 0
    var contactNoVisibility = 0
    var landNoVisibility = 0
    var emailVisibility = 0
    var affiliatesVisibility = 0
    var skillsVisibility = 0
    var interestVisibility = 0
    private var isCameraSelected = false
    private var imageUri: Uri? = null
    var profilImgBitmap: Bitmap? = null
    private var affiliatesParams = LinkedHashMap<String, String>()
    private var skillParams = HashMap<String, String>()
    private var interestParams = HashMap<String, String>()

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

        appbar_layout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appbar_layout.getTotalScrollRange()
                }
                if (scrollRange + verticalOffset == 0) {
                    collapse_toolbar.title = profile.full_name
                    belloLay.visibility = View.GONE
                    isShow = true
                } else if (isShow) {
                    collapse_toolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    belloLay.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
    }

    private fun initView() {
        setVisibility()
        tvAboutMeVisibility.setOnClickListener(this)
        tvDobVisibility.setOnClickListener(this)
        tvLandLineVisibility.setOnClickListener(this)
        tvMobileVisibility.setOnClickListener(this)
        tvEmailVisibility.setOnClickListener(this)
        tvAffilitesVisibility.setOnClickListener(this)
        tvMySkillVisibility.setOnClickListener(this)
        tvMyInterestVisibility.setOnClickListener(this)
        tvDob.setOnClickListener(this)
        ivProfileImage.setOnClickListener(this)
//        plus.setOnClickListener(this)
        appbar_layout!!.addOnOffsetChangedListener(this)

        tvAboutMe.setText(profile.about_me)

        if (!profile.dob.equals("0000-00-00")) {
            tvDob.text = Util.convertDobDate(profile.getFormatedDOB())//"13/08/1989"
            dob = profile.getFormatedDOB()
        }
        tv_landLine.setText(profile.landline_no)
        tv_mobileNo.setText(profile.contact_no)
        tv_email.setText(profile.email)
        edNameTxt.setText(profile.full_name)
        /*collapse_toolbar.title = profile.full_name*/


        if (profile.profile_image.isNotBlank()) {
            Log.e("Profile Image: ", profile.profile_image)
            if (!profile.profile_image.endsWith("defaultUser.png")) {
                Picasso.with(this).load(profile!!.profile_image)
                        .fit()
                        .into(toolbar_image, object : Callback {
                            override fun onSuccess() {
                                setPlated()
                            }

                            override fun onError() {
                                try {
                                    setPlated()
                                } catch (e: Exception) {

                                }
                            }
                        })
            }
        }

        let {
            addChip(affilitesChip, profile.affiliates, "add affiliates")
//            addhorizontalview(cheepContainer, profile.affiliates)
            addChip(skillsChip, profile.skills, "add skill")
            addChip(interestChip, profile.interests, "add interest")
        }
    }

    fun setVisibility() {
        when (ClubZ.currentUser!!.about_me_visibility) {
            "0" -> {
                aboutMeVisibility = 0
                tvAboutMeVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                aboutMeVisibility = 1
                tvAboutMeVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                aboutMeVisibility = 2
                tvAboutMeVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                aboutMeVisibility = 3
                tvAboutMeVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.dob_visibility) {
            "0" -> {
                dobVisibility = 0
                tvDobVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                dobVisibility = 1
                tvDobVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                dobVisibility = 2
                tvDobVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                dobVisibility = 3
                tvDobVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.contact_no_visibility) {
            "0" -> {
                contactNoVisibility = 0
                tvLandLineVisibility.text = getString(R.string.hidden)
                tvMobileVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                contactNoVisibility = 1
                tvLandLineVisibility.text = getString(R.string.Public)
                tvMobileVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                contactNoVisibility = 2
                tvLandLineVisibility.text = getString(R.string.only_for_my_contact)
                tvMobileVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                contactNoVisibility = 3
                tvLandLineVisibility.text = getString(R.string.only_for_club_member)
                tvMobileVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.landline_no_visibility) {
            "0" -> {
                landNoVisibility = 0
                tvLandLineVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                landNoVisibility = 1
                tvLandLineVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                landNoVisibility = 2
                tvLandLineVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                landNoVisibility = 3
                tvLandLineVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.email_visibility) {
            "0" -> {
                emailVisibility = 0
                tvEmailVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                emailVisibility = 1
                tvEmailVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                emailVisibility = 2
                tvEmailVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                emailVisibility = 3
                tvEmailVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.affiliates_visibility) {
            "0" -> {
                affiliatesVisibility = 0
                tvAffilitesVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                affiliatesVisibility = 1
                tvAffilitesVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                affiliatesVisibility = 2
                tvAffilitesVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                affiliatesVisibility = 3
                tvAffilitesVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.skills_visibility) {
            "0" -> {
                skillsVisibility = 0
                tvMySkillVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                skillsVisibility = 1
                tvMySkillVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                skillsVisibility = 2
                tvMySkillVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                skillsVisibility = 3
                tvMySkillVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        when (ClubZ.currentUser!!.interest_visibility) {
            "0" -> {
                interestVisibility = 0
                tvMyInterestVisibility.text = getString(R.string.hidden)
            }
            "1" -> {
                interestVisibility = 1
                tvMyInterestVisibility.text = getString(R.string.Public)
            }
            "2" -> {
                interestVisibility = 2
                tvMyInterestVisibility.text = getString(R.string.only_for_my_contact)
            }
            "3" -> {
                interestVisibility = 3
                tvMyInterestVisibility.text = getString(R.string.only_for_club_member)
            }
        }
        // tvMySkillVisibility.text = getString(R.string.Public)
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
            R.id.ivProfileImage -> {
                permissionPopUp()
            }
            R.id.tvDob -> {
                val splitarray = profile.getFormatedDOB().split("/")

                var day = splitarray[2].toInt()
                var month = splitarray[1].toInt()
                var year = splitarray[0].toInt()
                KeyboardUtil.hideKeyboard(this)
                datePicker(day, month, year, tvDob)

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
    private fun addChip(chipHolder: FlowLayout, str: String, hint: String) {

        val chipEditText = object : ChipEditText(this@ProfileEditActivity, R.layout.chip_edit_text, hint) {
            override fun setDone(text: String?) {
                if (!TextUtils.isEmpty(text)) {
                    addChip(chipHolder, text!!, hint)
                    hideSoftKeyboard()
                }
            }

        }

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
                        chipEditText.textView.setText("")
                        when (chipHolder.id) {
                            R.id.affilitesChip -> {
                                affiliatesParams.remove(tag)
                                if (removedAffiliates.equals("")) {
                                    removedAffiliates = tag
                                } else removedAffiliates = removedAffiliates + "," + tag
                                if (affiliatesParams.size == 9) chipHolder.addView(chipEditText)
                            }
                            R.id.skillsChip -> {
                                skillParams.remove(tag)
                                if (skillParams.size == 9) chipHolder.addView(chipEditText)
                            }
                            R.id.interestChip -> {
                                interestParams.remove(tag)
                                if (interestParams.size == 9) chipHolder.addView(chipEditText)
                            }
                        }
                    }
                }
                chip.text = tag
                when (chipHolder.id) {
                    R.id.affilitesChip -> {
                        if (!affiliatesParams.contains(tag)) {
                            affiliatesParams.put(tag, tag)
                            chipHolder.addView(chip)
                        } else Toast.makeText(this@ProfileEditActivity, "This affiliates already exist", Toast.LENGTH_SHORT).show()
                    }
                    R.id.skillsChip -> {
                        if (!skillParams.contains(tag)) {
                            skillParams.put(tag, tag)
                            chipHolder.addView(chip)
                        } else Toast.makeText(this@ProfileEditActivity, "This skill already exist", Toast.LENGTH_SHORT).show()
                    }
                    R.id.interestChip -> {
                        if (!interestParams.contains(tag)) {
                            interestParams.put(tag, tag)
                            chipHolder.addView(chip)
                        } else Toast.makeText(this@ProfileEditActivity, "This interest already exist", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        when (chipHolder.id) {
            R.id.affilitesChip -> {
                if (affiliatesParams.size < 10) chipHolder.addView(chipEditText)
            }
            R.id.skillsChip -> {
                if (skillParams.size < 10) chipHolder.addView(chipEditText)
            }
            R.id.interestChip -> {
                if (interestParams.size < 10) chipHolder.addView(chipEditText)
            }
        }
        /*chipHolder.addView(chipEditText)*/
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
        menuInflater.inflate(R.menu.my_profile_check, menu)
        /*menu.getItem(0).icon=getDrawable(R.drawable.ic_check_white_24dp)*/
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
            R.id.action_edit -> {
                for (text in affiliatesParams.entries) {
                    if (addedAffiliates.equals("")) {
                        addedAffiliates = text.value
                    }
                    else addedAffiliates = addedAffiliates + "," + text.value
                }
                for (text in skillParams.entries) {
                    if (updatedSkills.equals("")) {
                        updatedSkills = text.value
                    } else updatedSkills = updatedSkills + "," + text.value
                }
                for (text in interestParams.entries) {
                    if (updatedInterest.equals("")) {
                        updatedInterest = text.value
                    } else updatedInterest = updatedInterest + "," + text.value
                }
                val name = edNameTxt.text.toString().trim()
                if (name.isNotEmpty()) {
                    if (Util.isConnectingToInternet(this)) {
                        updateProfile()
                    } else {
                        object : Internet_Connection_dialog(this) {
                            override fun tryaginlistner() {
                                this.dismiss()
                                updateProfile()
                            }
                        }.show()
                    }
                } else {
                    Util.showSnake(this, cordinator_layout, 0, getString(R.string.name_should_not_be_empty))
                }
            }
        }
        /*if (item.title === "Add") {
            Toast.makeText(this, "clicked add", Toast.LENGTH_SHORT).show()
        }*/
        return super.onOptionsItemSelected(item)
    }

    private fun showVisibilityMenu(v: View) {

        KeyboardUtil.hideKeyboard(this)

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
                    landNoVisibility = position
                    tvLandLineVisibility.text = getVisibilityOption(position)
                }

                R.id.tvMobileVisibility -> {
                    contactNoVisibility = position
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
                    skillsVisibility = position
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
        val request = object : VolleyMultipartRequest(Request.Method.POST, WebService.update_profile,
                Response.Listener<NetworkResponse> { response ->
                    val data = String(response.data)
                    Util.e("data", data)
                    dialog.dismiss()
                    //{"status":"success","message":"Club added successfully"}
                    try {
                        val obj = JSONObject(data)
                        val status = obj.getString("status")
                        if (status == "success") {
                            Toast.makeText(this@ProfileEditActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                            SessionManager.getObj().createSession(Gson().fromJson<User>(obj.getString("userDetail"), User::class.java))
                            ClubZ.currentUser = SessionManager.getObj().user
                            updateUserInFirebase()
                        } else {
                            Toast.makeText(this@ProfileEditActivity, obj.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@ProfileEditActivity, R.string.swr, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@ProfileEditActivity, "Something went wrong", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["fullName"] = edNameTxt.text.toString()
                params["aboutMe"] = tvAboutMe.text.toString()
                params["contactNo"] = profile.contact_no
                params["landlineNo"] = tv_landLine.text.toString()
                params["countryCode"] = profile.country_code
                params["dob"] = dob
                params["email"] = profile.email
                params["skills"] = updatedSkills
                params["addAffiliates"] = addedAffiliates
                params["removedAffiliates"] = removedAffiliates
                params["interests"] = updatedInterest
                params["aboutMeVisibility"] = aboutMeVisibility.toString()
                params["dobVisibility"] = dobVisibility.toString()
                params["contactNoVisibility"] = contactNoVisibility.toString()
                params["landlineNoVisibility"] = landNoVisibility.toString()
                params["emailVisibility"] = emailVisibility.toString()
                params["affiliatesVisibility"] = affiliatesVisibility.toString()
                params["skillsVisibility"] = skillsVisibility.toString()
                params["interestVisibility"] = interestVisibility.toString()

                Util.e("parms create", params.toString())
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart>? {
                val params = java.util.HashMap<String, DataPart>()
                if (profilImgBitmap != null) {
                    params["profileImage"] = DataPart("profile.jpg", AppHelper.getFileDataFromDrawable(profilImgBitmap), "image")
                }
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                params["authToken"] = SessionManager.getObj().user.auth_token
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        ClubZ.instance.addToRequestQueue(request)

    }

    private fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, ivProfileImage, Gravity.CENTER)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            isCameraSelected = true
            when (item.itemId) {
                R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                    when {
                        this@ProfileEditActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTCAMERA)
                        this@ProfileEditActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> callIntent(Constants.INTENTREQUESTREAD)
                        else -> callIntent(Constants.INTENTCAMERA)
                    }
                } else {
                    callIntent(Constants.INTENTCAMERA)
                }
                R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                    isCameraSelected = false
                    if (this@ProfileEditActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constants.INTENTREQUESTREAD)
                    } else {
                        callIntent(Constants.INTENTGALLERY)
                    }
                } else {
                    callIntent(Constants.INTENTGALLERY)
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg")
                imageUri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(this@ProfileEditActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                // ImagePicker.pickImage(this@ProfileEditActivity)
                //   com.clubz.utils.picker.ImagePicker.pickImage(this@ProfileEditActivity)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@ProfileEditActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@ProfileEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@ProfileEditActivity, arrayOf(Manifest.permission.INTERNET),
                        Constants.MY_PERMISSIONS_REQUEST_INTERNET)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@ProfileEditActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ProfileEditActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@ProfileEditActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@ProfileEditActivity, requestCode, resultCode, data)
                if (imageUri != null) {
                    CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@ProfileEditActivity)
                    /*CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@ProfileEditActivity)*/

                } else {
                    Toast.makeText(this@ProfileEditActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
                /*try {
                    if (imageUri != null)
                        profilImgBitmap = MediaStore.Images.Media.getBitmap(this@ProfileEditActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    profilImgBitmap = ImageRotator.rotate(profilImgBitmap, rotation)
                    if (profilImgBitmap != null) {
                        val padding = 0
                        toolbar_image.setPadding(padding, padding, padding, padding)
                        toolbar_image.setImageBitmap(profilImgBitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }*/
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@ProfileEditActivity)
                    /*CropImage.activity(imageUri)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300).start(this@ProfileEditActivity)*/
                } else {
                    Toast.makeText(this@ProfileEditActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
                /*try {
                    if (imageUri != null)
                        activityImage = MediaStore.Images.Media.getBitmap(this@ProfileEditActivity.contentResolver, imageUri)
                    val rotation = ImageRotator.getRotation(this, imageUri, true)
                    activityImage = ImageRotator.rotate(activityImage, rotation)
                    if (activityImage != null) {
                        val padding = 0
                        imgActivity.setPadding(padding, padding, padding, padding)
                        imgActivity.setImageBitmap(activityImage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }*/
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data) // : CropImage.ActivityResult
                try {
                    if (result != null)
                        profilImgBitmap = MediaStore.Images.Media.getBitmap(this@ProfileEditActivity.contentResolver, result.uri)

                    if (profilImgBitmap != null) {
                        val padding = 0
                        toolbar_image.setPadding(padding, padding, padding, padding)
                        toolbar_image.setImageBitmap(profilImgBitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    private fun updateUserInFirebase() {
        val chatUserBean = UserBean()
        chatUserBean.uid = ClubZ.currentUser?.id
        chatUserBean.email = ClubZ.currentUser?.email
        chatUserBean.firebaseToken = FirebaseInstanceId.getInstance().token
        chatUserBean.name = ClubZ.currentUser?.full_name
        chatUserBean.profilePic = ClubZ.currentUser?.profile_image
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_USERS)
                .child(chatUserBean.uid!!)
                .setValue(chatUserBean).addOnCompleteListener {
                    setResult(successfullyUpdate, Intent())
                    finish()
                }
    }

    private fun datePicker(i1: Int, i2: Int, i3: Int, addDateTxt: TextView) {
        val calendar = GregorianCalendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DATE)
        if (i1 != -1) {
            day = i1
            month = i2 - 1
            year = i3
        }
        val datepickerdialog = DatePickerDialog(this@ProfileEditActivity, R.style.DialogTheme2, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                val check = Date()
                check.year = p1 - 1900; check.month; check.date = p3
                val d = Date(System.currentTimeMillis() - 1000)
                d.hours = 0
                d.minutes = 0
                d.seconds = 0
                Util.e("Tag", "$d : ${p0!!.minDate} : $check")
                year = p1; month = p2 + 1;day = p3
                dob = "" + year + "/" + month + "/" + day
                addDateTxt.text = Util.convertDobDate(dob)
            }
        }, year, month, day)
        datepickerdialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datepickerdialog.window!!.setBackgroundDrawableResource(R.color.white)
        datepickerdialog.setTitle(getString(R.string.dob))
        datepickerdialog.show()
    }
}
