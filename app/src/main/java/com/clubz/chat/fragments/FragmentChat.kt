package com.clubz.chat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.clubz.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentChat.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentChat.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentChat : Fragment() {
    // TODO: Rename and change types of parameters
    private var activityId=""
    private var userId=""
    private var userName=""
    private var userProfileImg=""


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            activityId = arguments.getString(ARG_ACTIVITYID)
            userId = arguments.getString(ARG_USERID)
            userName = arguments.getString(ARG_USERNAME)
            userProfileImg = arguments.getString(ARG_USERPROFILEIMG)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_ACTIVITYID = "activityId"
        private val ARG_USERID = "userId"
        private val ARG_USERNAME = "userName"
        private val ARG_USERPROFILEIMG = "userProfileImg"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentChat.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(activityId: String, userId: String, userName: String, userProfileImg: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_ACTIVITYID, activityId)
            args.putString(ARG_USERID, userId)
            args.putString(ARG_USERNAME, userName)
            args.putString(ARG_USERPROFILEIMG, userProfileImg)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
