package com.clubz.fragment.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.Home_Activity

import com.clubz.R
import kotlinx.android.synthetic.main.baseclub_list.*
import kotlinx.android.synthetic.main.frag_news.*

/**
 * Created by mindiii on 3/12/18.
 */

class Frag_News_List : Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_news, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(view in arrayOf(search_t)) view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.search_t-> (activity as Home_Activity).addFragment(Frag_Search_Club(),0)
        }
    }
}
