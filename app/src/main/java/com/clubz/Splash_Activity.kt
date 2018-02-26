package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_splash.*
import android.support.v4.content.res.TypedArrayUtils.getResourceId
import android.content.res.TypedArray
import com.clubz.helper.SessionManager
import com.clubz.helper.Type_Token
import com.clubz.model.Country_Code
import com.google.gson.Gson


class Splash_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        Handler().postDelayed({
            val intent =if(SessionManager.getObj().isloggedin()) Intent(this@Splash_Activity , Home_Activity::class.java)
            else Intent(this@Splash_Activity , Inro_Activity::class.java)
            startActivity(intent);
            finish();
        },5000);

    }


}
