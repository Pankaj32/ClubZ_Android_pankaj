package com.clubz.fragment.signup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.clubz.Adapter.MyViewPagerAdapter
import com.clubz.R
import com.clubz.Sign_up_Activity
import kotlinx.android.synthetic.main.frag_sign_up_one_2.*

/**
 * Created by mindiii on 2/6/18.
 */
class Frag_Sign_Up_One_2 : Fragment()  , View.OnClickListener {




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_sign_up_one_2, null);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for( view in arrayOf(confirm)) view.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.confirm -> (activity as Sign_up_Activity).replaceFragment(Frag_Sign_Up_Two())
     }
    }
}