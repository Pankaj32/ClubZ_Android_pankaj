package com.clubz.ui.profile

import android.os.Bundle
import com.clubz.R
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.view.MenuItem
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import com.clubz.data.model.Profile
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.WindowManager
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.model.Feed
import com.clubz.data.remote.WebService
import com.clubz.ui.cv.CusDialogProg
import com.clubz.utils.Util
import com.clubz.utils.VolleyGetPost
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() , AppBarLayout.OnOffsetChangedListener {

    private var appBarLayout: AppBarLayout? = null
    private var collapsedMenu: Menu? = null
    private var appBarExpanded = true
    private var profile : Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        intent.let {
            if(intent.hasExtra("profile")) profile = it.extras.getSerializable("profile") as Profile
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        val dWidth = windowManager.defaultDisplay
        appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout) as AppBarLayout
        val toolbarImage = findViewById<View>(R.id.toolbar_image) as ImageView
        val face = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.teko_medium)
        } else Typeface.createFromAsset(assets, "teko_medium.ttf")
        collapse_toolbar.setCollapsedTitleTypeface(face)
        collapse_toolbar.setExpandedTitleTypeface(face)
        toolbarImage.getLayoutParams().height = dWidth.width
        appbar_layout!!.addOnOffsetChangedListener(this)
        collapse_toolbar.setTitle(getString(R.string.dharmraj_acharya_bhurtel))
        initView()

        getProfile()
    }

    private fun setPlated(){
        //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.dharmrja)
        val bitmap = (toolbar_image.getDrawable() as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette: Palette? ->
            val mutedColor = palette?.getMutedColor(resources.getColor(R.color.primaryColor))
            collapse_toolbar.setContentScrimColor(mutedColor!!)
            collapse_toolbar.setStatusBarScrimColor(mutedColor)
            val window = getWindow()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(mutedColor)
            }
        }
    }


    private fun initView(){

        collapse_toolbar.setTitle(profile!!.fullName)
        tvDob.text = "1989, November 13"
        tv_phoneNo.text = "(+91) 9977141811"
        tv_landLine.text = "(+91) 0731 - 284243"
        tv_email.text = "dharmrajacharya@gmail.com"

        Picasso.with(this).load(profile!!.userImage).into(toolbar_image,  object : com.squareup.picasso.Callback{
            override fun onSuccess() {
                setPlated()
            }

            override fun onError() {
                setPlated()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.profile_menu, menu)
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
                drawable.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
            }
        } else {
            //expanded
            menu.getItem(0).setVisible(false)
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }


   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_chat -> return true
        }
        if (item.getTitle() === "Add") {
            Toast.makeText(this, "clicked add", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

        if (percentage>0.95) {
            ivChat.visibility = View.GONE
            appBarExpanded = false
            invalidateOptionsMenu()
        } else {
            ivChat.visibility = View.VISIBLE
            appBarExpanded = true
            invalidateOptionsMenu()
        }
    }


    fun getProfile(){
         val dialog = CusDialogProg(this@ProfileActivity);
         dialog.show();   // ?clubId=66&offset=0&limit=10
        object : VolleyGetPost(this@ProfileActivity,
                    WebService.get_profile+"?userId="+profile!!.userId
                        ,true) {

            override fun onVolleyResponse(response: String?) {
                try {
                    // dialog.dismiss();
                    Log.d("newsFeedsLike", response)
                    val obj = JSONObject(response)
                    if (obj.getString("status")=="success") {

                    }
                } catch (ex: Exception) {
                     Util.showToast(R.string.swr, this@ProfileActivity)
                }
            }

            override fun onVolleyError(error: VolleyError?) { dialog.dismiss(); }

            override fun onNetError() { dialog.dismiss()}

            override fun setParams(params: MutableMap<String, String>?): MutableMap<String, String> {
                return params!!
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = ClubZ.currentUser!!.auth_token
                params["language"] = SessionManager.getObj().getLanguage()
                return params
            }
        }.execute()
    }
}
