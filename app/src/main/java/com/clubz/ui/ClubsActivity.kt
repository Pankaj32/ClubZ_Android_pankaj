package com.clubz.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.clubz.R

class ClubsActivity : AppCompatActivity() {

    private val bool : Boolean = false

    companion object {
        private val INTENT_USER_ID = "user_id"
        fun newIntent(context: Context, bool: Boolean): Intent {
            val intent = Intent(context, ClubsActivity::class.java)
            intent.putExtra(INTENT_USER_ID, bool)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)
    }
}
