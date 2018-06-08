package com.clubz.ui.newsfeed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.clubz.R
import com.clubz.ui.newsfeed.fragment.Frag_News_List
import kotlinx.android.synthetic.main.activity_my_news_feed.*

class MyNewsFeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_news_feed)
        addFragment(Frag_News_List.newInstance(true))

        backBtn.setOnClickListener(View.OnClickListener { v: View? ->
            onBackPressed()
        })

    }


    fun addFragment(fragmentHolder: Fragment): Fragment? {
        try {
            val fragmentManager = supportFragmentManager
            val fragmentName = fragmentHolder.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            fragmentTransaction.add(R.id.container, fragmentHolder, fragmentName)
            fragmentTransaction.commit()
            return fragmentHolder
        } catch (e: Exception) {
            return null
        }
    }
}
