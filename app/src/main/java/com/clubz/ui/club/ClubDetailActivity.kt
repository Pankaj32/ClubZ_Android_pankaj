package com.clubz.ui.club

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.clubz.R
import com.clubz.data.model.Clubs
import com.clubz.ui.club.fragment.Frag_ClubDetails_1
import com.clubz.ui.club.fragment.Frag_ClubDetails_2
import com.clubz.ui.core.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_club_detail.*

fun Context.ClubDetailIntent( clubz : Clubs): Intent {
    return Intent(this, ClubDetailActivity::class.java).apply {
        putExtra(INTENT_CLUBZ, clubz)
    }
}

private const val INTENT_CLUBZ = "clubz"

class ClubDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var clubz : Clubs;
    lateinit var adapter : ViewPagerAdapter

    fun newIntent(context: Context, clubz : Clubs): Intent {
        val intent = Intent(context, ClubDetailActivity::class.java)
        intent.putExtra(INTENT_CLUBZ, clubz)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_detail)

        val intent = getIntent()
        clubz = intent!!.extras.getSerializable("clubz") as Clubs
        requireNotNull(clubz) { "no user_id provided in Intent extras" }

        title_tv.text = clubz.club_name
        for (views in arrayOf(backBtn)) views.setOnClickListener(this)
        setViewPager(view_pager_cd)
        tablayout_cd.setupWithViewPager(view_pager_cd)
    }

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment( Frag_ClubDetails_1().setData(clubz),resources.getString(R.string.t_detils) , " This is First")
        adapter.addFragment( Frag_ClubDetails_2().setData(clubz),resources.getString(R.string.t_members) , " This is second")
        viewPager.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.backBtn ->{ onBackPressed() }
        }
    }
}
