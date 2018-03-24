package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_splash.*
import android.support.v4.content.res.TypedArrayUtils.getResourceId
import android.content.res.TypedArray
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.clubz.helper.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.model.Country_Code
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.crashlytics.android.Crashlytics




class Splash_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

// Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_splash)
        Util.checklaunage(this)
        /*Glide.with(this)
                .load(R.drawable.new_splash_bg)
                .asGif()
                .crossFade()
                .into(stars_iv);*/
    }

    override fun onStart() {
        super.onStart()
        startEffect()
       /* Handler().postDelayed({

        },2000);*/


    }

    fun startEffect(){
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.one_fade_s)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.two_fade_s)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.slide_left_s)
        val animat3_2 = AnimationUtils.loadAnimation(this, R.anim.slide_right_s)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.four_fade_s)

        ribbion.setAnimation(animat1);
        image.setAnimation(animat2);
        line1.setAnimation(animat3);
        line2.setAnimation(animat3_2);
        text1.setAnimation(animat4);
        text2.setAnimation(animat4);
        text3.setAnimation(animat4);
        text_view.setAnimation(animat4);


        animat1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                ribbion.visibility= View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                image.visibility= View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                line1.visibility= View.VISIBLE
                line2.visibility= View.VISIBLE

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat4.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                text1.visibility= View.VISIBLE
                text2.visibility= View.VISIBLE
                text3.visibility= View.VISIBLE
                text_view.visibility= View.VISIBLE
                Handler().postDelayed({
                    dimmedEffet()
                },600);
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    fun dimmedEffet() {
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.one_fade)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.two_fade)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.slide_left)
        val animat3_2 = AnimationUtils.loadAnimation(this, R.anim.slide_right)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.four_fade)

        ribbion.startAnimation(animat1);
        image.startAnimation(animat2);
        line1.startAnimation(animat3_2);
        line2.startAnimation(animat3);
        text1.startAnimation(animat4);
        text2.startAnimation(animat4);
        text3.startAnimation(animat4);
        text_view.startAnimation(animat4);



        animat1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                ribbion.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                image.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                line1.visibility= View.INVISIBLE
                line2.visibility= View.INVISIBLE

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat4.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                text1.visibility= View.INVISIBLE
                text2.visibility= View.INVISIBLE
                text3.visibility= View.INVISIBLE
                text_view.visibility= View.INVISIBLE
                Handler().postDelayed({
                    val intent =if(SessionManager.getObj().isloggedin()) Intent(this@Splash_Activity , Home_Activity::class.java)
                    else Intent(this@Splash_Activity , Inro_Activity::class.java)
                    startActivity(intent);
                    finish();
                },200);
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    /*
    email: 6-256
    Name:  2-100
    password: 6-30
    */


}
