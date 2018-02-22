package com.clubz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.clubz.util.Util
import kotlinx.android.synthetic.main.activity_splash.*
import android.support.v4.content.res.TypedArrayUtils.getResourceId
import android.content.res.TypedArray
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
            val intent = Intent(this@Splash_Activity , Inro_Activity::class.java)
            startActivity(intent);
            finish();
        },5000);

    }


    class ImageCode(code :String , value :Int ){
       var code : String =""
       var image :Int = 0
    }

    /*fun getlist():ArrayList<ImageCode>{
        var list :ArrayList<ImageCode> = ArrayList<ImageCode>()
        var jsonary = JSONArray(Image_Codes_class.image_codes)
        for(i in_ 0..jsonary.length()-1){ list.add(ImageCode())        }
        return
    }*/

}
