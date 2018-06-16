package com.clubz.ui.profile

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private var appBarLayout: AppBarLayout? = null
    private var collapsedMenu: Menu? = null
    private var appBarExpanded = true
    private var profile: Profile? = null
    private var isMyprofile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        intent.let {
            if (intent.hasExtra("profile")) profile = it.extras.getSerializable("profile") as Profile
            isMyprofile = profile?.userId == ClubZ.currentUser!!.id
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val diametric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(diametric)

        appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout) as AppBarLayout
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
        getProfile()
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


    private fun initView() {
        if (isMyprofile) {
            ll_silenceUser.visibility = View.GONE
            ivChat.visibility = View.GONE
        } else {
            ll_silenceUser.visibility = View.VISIBLE
            ivChat.visibility = View.VISIBLE
            appbar_layout!!.addOnOffsetChangedListener(this)
        }

        collapse_toolbar.title = profile!!.full_name
        if (!profile!!.profile_image.isBlank()) {
            Picasso.with(this).load(profile!!.profile_image).into(toolbar_image, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    setPlated()
                }

                override fun onError() {
                    setPlated()
                }
            })
        }

    }

    private fun updateView() {
        collapse_toolbar.title = profile!!.full_name
        tvDob.text = profile?.getFormatedDOB()
        tv_phoneNo.text = profile?.getContactNo()
        tv_landLine.text = profile?.getContactNo()
        tv_email.text = profile?.email
        tvAboutMe.text = profile?.about_me

        addChip(affilitesChip, profile!!.affiliates)
        addChip(skillsChip, profile!!.skills)
        addChip(interestChip, profile!!.interests)
    }

    private fun addChip(chipHolder: FlowLayout, str: String) {
        if (str.isNotBlank()) {
            val tagList = str.split(",").map { it.trim() }
            for (tag in tagList) {
                val chip = object : ChipView(this@ProfileActivity, chipHolder.childCount.toString(), false) {
                    override fun getLayout(): Int {
                        return R.layout.z_cus_chip_view_newsfeed
                    }

                    override fun setDeleteListner(chipView: ChipView?) {
                    }
                }
                chip.text = tag
                chipHolder.addView(chip)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        collapsedMenu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
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


    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
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


    private fun getProfile() {
        val dialog = CusDialogProg(this@ProfileActivity)
        dialog.show()   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(this@ProfileActivity,
                WebService.get_profile + "?userId=" + profile!!.userId
                , true) {

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
}
