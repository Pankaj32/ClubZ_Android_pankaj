package com.clubz.ui.club

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.Window
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.model.Clubs
import com.clubz.ui.club.fragment.Frag_ClubDetails_1
import com.clubz.ui.club.fragment.Frag_ClubDetails_2
import com.clubz.ui.core.ViewPagerAdapter
import com.clubz.ui.newsfeed.CreateNewsFeedActivity
import kotlinx.android.synthetic.main.activity_club_detail.*
import kotlinx.android.synthetic.main.dialog_clubdetail_menu.*

fun Context.ClubDetailIntent( clubz : Clubs): Intent {
    return Intent(this, ClubDetailActivity::class.java).apply {
        putExtra(INTENT_CLUBZ, clubz)
    }
}

private const val INTENT_CLUBZ = "clubz"

class ClubDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var clubz : Clubs
    lateinit var adapter : ViewPagerAdapter
    var dialog : Dialog? = null

   /* fun newIntent(context: Context, clubz : Clubs): Intent {
        val intent = Intent(context, ClubDetailActivity::class.java)
        intent.putExtra(INTENT_CLUBZ, clubz)
        return intent
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_detail)

        clubz = intent!!.extras.getSerializable("clubz") as Clubs
        requireNotNull(clubz) { "no user_id provided in Intent extras" }

        title_tv.text = clubz.club_name
        for (views in arrayOf(backBtn, bubble_menu)) views.setOnClickListener(this)
        setViewPager(view_pager_cd)
        bubble_menu.visibility = if(clubz.user_id == ClubZ.currentUser?.id) View.VISIBLE else View.GONE
    }

    private fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment( Frag_ClubDetails_1().setData(clubz),resources.getString(R.string.t_detils) , " This is First")
        if(clubz.user_id==ClubZ.currentUser!!.id){

            adapter.addFragment( Frag_ClubDetails_2().setData(clubz),resources.getString(R.string.t_members) , " This is second")
            tablayout_cd.setupWithViewPager(view_pager_cd)

        }else {
            tablayout_cd.setSelectedTabIndicatorHeight(0)
            tablayout_cd.visibility = View.GONE
        }
        viewPager.adapter = adapter
    }


    override fun onClick(v: View?) {
        when(v?.id){

            R.id.backBtn ->{ onBackPressed() }

            R.id.bubble_menu ->{
                showDialog()
            }

            R.id.menuCreateNewsFeed ->{
                dialog?.dismiss()
                startActivity(Intent(this@ClubDetailActivity,
                        CreateNewsFeedActivity::class.java).putExtra("clubId", clubz.clubId))
            }

            R.id.menuEditNewsFeed ->{ }
        }
    }


    @SuppressLint("RtlHardcoded")
    private fun showDialog(){

        if(dialog==null){
            dialog = Dialog(this)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogWindow = dialog?.window
            dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.dialog_clubdetail_menu)

            val lp = dialogWindow?.attributes
            dialogWindow?.setGravity(Gravity.TOP or Gravity.RIGHT)
            lp?.y = -100
            dialogWindow?.attributes = lp
            dialog?.setCancelable(true)

            for (views in arrayOf(dialog?.menuCreateNewsFeed, dialog?.menuEditNewsFeed))
                views?.setOnClickListener(this)
        }
        dialog?.show()
    }
}
