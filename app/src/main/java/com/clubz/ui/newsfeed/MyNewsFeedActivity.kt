package com.clubz.ui.newsfeed

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.Window
import com.clubz.R
import com.clubz.ui.newsfeed.fragment.FragNewsList
import kotlinx.android.synthetic.main.activity_my_news_feed.*
import kotlinx.android.synthetic.main.menu_news_filter.*

class MyNewsFeedActivity : AppCompatActivity(), View.OnClickListener {

    private var dialog : Dialog? = null
    private var like = false
    private var comment = false
    private var club = false
    private var ifNeedTocallApi : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_news_feed)
        addFragment(FragNewsList.newInstance(true))
        backBtn.setOnClickListener(this)
        bubble_menu.setOnClickListener(this)
    }


    private fun addFragment(fragmentHolder: Fragment): Fragment? {
        return try {
            val fragmentManager = supportFragmentManager
            val fragmentName = fragmentHolder.javaClass.name
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            fragmentTransaction.add(R.id.container, fragmentHolder, fragmentName)
            fragmentTransaction.commit()
            fragmentHolder
        } catch (e: Exception) {
            null
        }
    }


    override fun onClick(v: View?) {

        when(v?.id){

            R.id.bubble_menu->{
                showFilterDialog()
            }

            R.id.backBtn->{
                onBackPressed()
            }

            /*R.id.ll_clearFilter->{
                if(!like && !comment && !club) ifNeedTocallApi = false
                club =      false
                like =      false
                comment =   false

                dialog?.ch_byClubs?.isChecked = false
                dialog?.ch_byLikes?.isChecked = false
                dialog?.ch_byComments?.isChecked = false
                dialog?.dismiss()
            }*/

            /*R.id.ch_byClubs->{
                ifNeedTocallApi = true
                club = dialog?.ch_byClubs?.isChecked!!
            }*/

            R.id.ch_byComments->{
                ifNeedTocallApi = true
                comment = dialog?.ch_byComments?.isChecked!!
            }

            R.id.ch_byLikes->{
                ifNeedTocallApi = true
                like = dialog?.ch_byLikes?.isChecked!!
            }
        }
    }

    private fun updateMyNewsFeed(){
        if(ifNeedTocallApi){
            ifNeedTocallApi = false
            val fragemet : List<Fragment> = supportFragmentManager.fragments
            var newsFeedFragment: FragNewsList? = null
            for(frag in fragemet){
                if(frag::class.java.simpleName==FragNewsList::class.java.simpleName){
                    newsFeedFragment = frag as FragNewsList
                    break
                }
            }
            newsFeedFragment?.setFilter(club, like, comment)
        }
    }


    @SuppressLint("RtlHardcoded")
    private fun showFilterDialog(){
        if(dialog==null){
            dialog = Dialog(this)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogWindow = dialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.menu_news_filter)

            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            dialog?.setCancelable(true)

            for (views in arrayOf(/*dialog?.ch_byClubs,*/ dialog?.ch_byComments, dialog?.ch_byLikes/*, dialog?.ll_clearFilter*/))
                views?.setOnClickListener(this)
        }
        dialog?.show()
        dialog?.setOnDismissListener({
            updateMyNewsFeed()
        })
    }
}
