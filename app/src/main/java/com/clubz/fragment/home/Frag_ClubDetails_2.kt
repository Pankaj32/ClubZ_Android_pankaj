package com.clubz.fragment.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R
import kotlinx.android.synthetic.main.frag_list.*

/**
 * Created by mindiii on २१/३/१८.
 */
class Frag_ClubDetails_2 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_list,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_top.visibility = View.GONE
        view!!.setOnClickListener{}
    }


}