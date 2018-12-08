package com.clubz.ui.setting

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.support.v7.widget.PopupMenu
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.ui.main.HomeActivity
import com.clubz.utils.Util
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : AppCompatActivity(), View.OnClickListener{

    var ENGLISH_LOCALE="en"
    var SPANISH_LOCALE="es"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.checklaunage(this)
        setContentView(R.layout.activity_setting)
        ivBack.setOnClickListener(this)
        rl_change_language.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.ivBack -> {

                super.onBackPressed()
            }
            R.id.rl_change_language -> {
                permissionPopUp(rl_change_language)
            }
        }
    }

    private fun permissionPopUp(view: View) {
        val wrapper = ContextThemeWrapper(this, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, view, Gravity.CENTER)
      // popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        popupMenu.getMenu().add(1, R.id.pop1, 1, "English");
        popupMenu.getMenu().add(1, R.id.pop2, 2, "Spanish");
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.pop1 -> {
                    SessionManager.getObj().language=ENGLISH_LOCALE
                    Util.checklaunage(this)
                    startActivity(Intent(this@SettingActivity, HomeActivity::class.java))
                    finish()

                }
                R.id.pop2 -> {
                    SessionManager.getObj().language=SPANISH_LOCALE
                    Util.checklaunage(this)
                    startActivity(Intent(this@SettingActivity, HomeActivity::class.java))
                    finish()
                }

            }
            true
        }

        popupMenu.show()
    }

}
