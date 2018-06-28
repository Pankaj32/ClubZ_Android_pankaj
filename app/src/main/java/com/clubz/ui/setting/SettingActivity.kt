package com.clubz.ui.setting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.clubz.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        ivBack.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {

        when(p0?.id){
            R.id.ivBack -> { onBackPressed() }
        }
    }
}
