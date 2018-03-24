package com.clubz.fragment.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R
import com.clubz.model.Clubs

import kotlinx.android.synthetic.main.frag_club_details.*

/**
 * Created by mindiii on २१/३/१८.
 */
class Frag_ClubDetails : Fragment() {

    lateinit var clubz :Clubs
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_club_details,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager(view_pager_cd)
        tablayout_cd.setupWithViewPager(view_pager_cd)
        view!!.setOnClickListener{}
    }

    lateinit var adapter :ViewPagerAdapter

    fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragger( Frag_ClubDetails_1().setData(clubz),resources.getString(R.string.t_detils) , " This is First")
        adapter.addFragger( Frag_ClubDetails_2(),resources.getString(R.string.t_members) , " This is second")
        viewPager.adapter = adapter
    }

    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        //Here We need to implement getCount and GetItem methods
        //ANd for implementing This We are Creating ArrayList for adding Fragment and For Adding Title
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragger(fragment: Fragment, title: String, menu: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        //This is The method which is Overrided by us
        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        fun setTile(position: Int, fragment: Fragment, title: String) {
            mFragmentList[position] = fragment
            mFragmentTitleList[position] = title
        }
    }

    fun setData(obj: Clubs): Fragment {
        clubz= obj;
        return this;
    }


}