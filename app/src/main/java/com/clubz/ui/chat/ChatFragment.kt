package com.clubz.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clubz.R


class ChatFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param bundle
         * @return A new instance of fragment FeedDetailFragment.
         */
        @JvmStatic
        fun newInstance(bundle: Bundle) = ChatFragment().apply {
            arguments = Bundle().apply {
                putBundle("bundle", bundle)
            }
        }
    }
}
