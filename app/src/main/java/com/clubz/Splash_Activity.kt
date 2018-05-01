package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.clubz.helper.SessionManager
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.crashlytics.android.Crashlytics




class Splash_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

        //Initialize Fabric with the debug-disabled crashlytics.
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
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.dark_strip_in)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.fox_in)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.club_title_in)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.top_line_in)
        val animat5 = AnimationUtils.loadAnimation(this, R.anim.bottom_line_in)
        val animat6 = AnimationUtils.loadAnimation(this, R.anim.slogan_in)

        ribbion.setAnimation(animat1);
        image.setAnimation(animat2);
        text1.setAnimation(animat3);

        line1.setAnimation(animat4);
        line2.setAnimation(animat5);


        text2.setAnimation(animat6);
        text3.setAnimation(animat6);
        text_view.setAnimation(animat6);


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
                },2000);
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    fun dimmedEffet() {
        val animat1 = AnimationUtils.loadAnimation(this, R.anim.dark_strip_out)
        val animat2 = AnimationUtils.loadAnimation(this, R.anim.fox_out)
        val animat3 = AnimationUtils.loadAnimation(this, R.anim.club_title_out)
        val animat4 = AnimationUtils.loadAnimation(this, R.anim.top_line_out)
        val animat5 = AnimationUtils.loadAnimation(this, R.anim.bottom_line_out)
        val animat6 = AnimationUtils.loadAnimation(this, R.anim.slogan_out)

        ribbion.startAnimation(animat1);
        image.startAnimation(animat2);
        text1.startAnimation(animat3);

        line1.startAnimation(animat4);
        line2.startAnimation(animat5);


        text2.startAnimation(animat6);
        text3.startAnimation(animat6);
        text_view.startAnimation(animat6);



        animat2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                image.visibility= View.INVISIBLE
                text1.visibility= View.INVISIBLE

                text_view.visibility= View.INVISIBLE
                text2.visibility= View.INVISIBLE
                text3.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat4.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                line1.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        animat5.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                line2.visibility= View.INVISIBLE

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
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
                    val intent =if(SessionManager.getObj().isloggedin()) Intent(this@Splash_Activity , HomeActivity::class.java)
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
