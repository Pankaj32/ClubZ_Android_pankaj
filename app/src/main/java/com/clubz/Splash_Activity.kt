package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_splash.*

class Splash_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Util.checklaunage(this)

        Glide.with(this)
                .load(R.drawable.new_splash_bg)
                .asGif()
                .crossFade()
                .into(stars_iv);
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            val intent = Intent(this@Splash_Activity , Sign_In_Activity::class.java)
            startActivity(intent);
            finish();
        },5000);

    }
}
