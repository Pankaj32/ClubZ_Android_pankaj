package com.clubz.ui.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.utils.Util
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.clubz.BuildConfig
import com.clubz.ui.guide.Inro_Activity
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.ui.main.HomeActivity
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.crashlytics.android.Crashlytics

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.checklaunage(this)
        setContentView(R.layout.activity_splash)

    }

    override fun onStart() {
        super.onStart()
        startEffect()
    }

    private fun startEffect(){
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.dark_strip_in)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.fox_in)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.club_title_in)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.top_line_in)
        val animat5 = AnimationUtils.loadAnimation(this, R.anim.bottom_line_in)
        val animat6 = AnimationUtils.loadAnimation(this, R.anim.slogan_in)

        ribbion.animation = animat1
        image.animation = animat2
        text1.animation = animat3

        line1.animation = animat4
        line2.animation = animat5

        text2.animation = animat6
        text3.animation = animat6
        text_view.animation = animat6


        animat1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                ribbion.visibility= View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                image.visibility= View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                line1.visibility= View.VISIBLE
                line2.visibility= View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat4.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                text1.visibility= View.VISIBLE
                text2.visibility= View.VISIBLE
                text3.visibility= View.VISIBLE
                text_view.visibility= View.VISIBLE
                Handler().postDelayed({
                    dimmedEffet()
                },2000)
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })
    }

   private fun dimmedEffet() {
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.dark_strip_out)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.fox_out)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.club_title_out)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.top_line_out)
        val animat5 = AnimationUtils.loadAnimation(this, R.anim.bottom_line_out)
        val animat6 = AnimationUtils.loadAnimation(this, R.anim.slogan_out)

        ribbion.startAnimation(animat1)
        image.startAnimation(animat2)
        text1.startAnimation(animat3)

        line1.startAnimation(animat4)
        line2.startAnimation(animat5)

        text2.startAnimation(animat6)
        text3.startAnimation(animat6)
        text_view.startAnimation(animat6)

        animat2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                image.visibility = View.INVISIBLE
                text1.visibility = View.INVISIBLE
                text_view.visibility = View.INVISIBLE
                text2.visibility = View.INVISIBLE
                text3.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat4.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                line1.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat5.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                line2.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })

        animat1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                text1.visibility= View.INVISIBLE
                text2.visibility= View.INVISIBLE
                text3.visibility= View.INVISIBLE
                ribbion.visibility= View.INVISIBLE

                Handler().postDelayed({
                    val intent = if(SessionManager.getObj().isloggedin()) Intent(this@SplashActivity , HomeActivity::class.java)
                    else Intent(this@SplashActivity , Inro_Activity::class.java)
                    startActivity(intent)
                    //startActivity(Intent(this@SplashActivity, ProfileActivity::class.java))
                    finish()
                },200)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}
