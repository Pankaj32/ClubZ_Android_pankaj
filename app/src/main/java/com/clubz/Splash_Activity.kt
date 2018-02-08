package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.util.Util

class Splash_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Util.checklaunage(this)
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            val intent = Intent(this@Splash_Activity , Sign_In_Activity::class.java)
            startActivity(intent);
            finish();
        },2000);

    }
}
