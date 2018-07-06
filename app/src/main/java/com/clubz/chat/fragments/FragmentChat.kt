package com.clubz.chat.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.adapter.ChatRecyclerAdapter
import com.clubz.chat.model.ChatBean
import com.clubz.chat.model.FeedBean
import com.clubz.chat.util.ChatUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_chat.*

import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentChat.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentChat.newInstanceActivityChat] factory method to
 * create an instance of this fragment.
 */
class FragmentChat : Fragment(), View.OnClickListener {
    private var mContext: Context? = null
    // TODO: Rename and change types of parameters
    private var chatFor = ""

    private var activityId = ""
    private var clubId = ""
    private var userId = ""
    private var userName = ""
    private var userProfileImg = ""

    private var feedsId = ""


    private var mstorage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var app: FirebaseApp? = null
    private var chatRoom = ""
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var mChatRecyclerAdapter: ChatRecyclerAdapter? = null


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        app = FirebaseApp.getInstance()
        mstorage = FirebaseStorage.getInstance(app!!)
        if (arguments != null) {
            chatFor = arguments.getString(ARG_CHATFOR)
            when (chatFor) {
                "feeds" -> {
                    clubId = arguments.getString(ARG_CLUB_ID)
                    feedsId = arguments.getString(ARG_FEED_ID)
                    chatRoom = clubId + "_" + feedsId + "_" + chatFor
                    getFeedStatus()
                    // getMessageFromFirebaseUser()
                }
                "activities" -> {
                    activityId = arguments.getString(ARG_ACTIVITYID)
                    userId = arguments.getString(ARG_USERID)
                    userName = arguments.getString(ARG_USERNAME)
                    userProfileImg = arguments.getString(ARG_USERPROFILEIMG)
                }
            }
        }
        sentButton.setOnClickListener(this)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sentButton -> {

                if (txtMsg.text.toString().isNotEmpty()) {
                    sendMessage(txtMsg.text.toString(), "text", chatFor)
                } else {
                    Toast.makeText(mContext, R.string.please_type, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendMessage(message: String, msgType: String, chatType: String) {
        if (message != "") {
            var msg = ""
            txtMsg.setText("")
            //   Constant.hideSoftKeyboard(ChatActivity.this);
            val chatBean = ChatBean()
            chatBean.deleteby = ""
            chatBean.chatType = chatType
            if (msgType == "text") {
                chatBean.image = 0
                chatBean.imageUrl = ""
                chatBean.message = message
                msg = message
            } else {
                chatBean.image = 1
                chatBean.imageUrl = message
                chatBean.message = ""
                msg = "Image"
            }
            chatBean.senderId = ClubZ.currentUser?.id
            chatBean.senderName = ClubZ.currentUser?.full_name
            chatBean.timestamp = ServerValue.TIMESTAMP

            /*if (Integer.parseInt(mUid) < Integer.parseInt(rcvUId)) {
                chatRoom = mUid + "_" + rcvUId
            } else {
                chatRoom = rcvUId + "_" + mUid
            }*/
            val finalMsg = msg
            databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(chatRoom)) {
                        Log.e("TAG", "sendMessageToFirebaseUser: $chatRoom exists")
                        val gen_key = databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).child(chatRoom).ref.push()
                        gen_key.setValue(chatBean)
                    } else {
                        Log.e("TAG", "sendMessageToFirebaseUser: success")
                        val gen_key = databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).child(chatRoom).ref.push()
                        gen_key.setValue(chatBean)
                        // getMessageFromFirebaseUser(mUid, rcvUId)
                    }

                    /*sendmyChatHistory(chatBean, databaseReference, msgType)
                    sendOppChatHistory(chatBean, databaseReference, msgType)
                    sendPushNotificationToReceiver(chatBean.name, chatBean.name,
                            finalMsg,
                            chatBean.uid,
                            FirebaseInstanceId.getInstance().token,
                            chatRoom,
                            requestId,
                            requestType,
                            rcvPrflImg,
                            rcvFbTkn)*/
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //   mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                }
            })
        }
    }

    fun getFeedStatus() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_NEWS_FEED)
                .child(clubId)
                .child(feedsId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val feedsBean = dataSnapshot?.getValue(FeedBean::class.java)
                        if (feedsBean?.isCommentAllow.equals("1")) {
                            getMessageFromFirebaseUser()
                        }else{
                            txtMsg.setFocusable(false)
                            txtMsg.setText("Comment disable")
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

                })

    }

    fun getMessageFromFirebaseUser() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).ref.orderByKey().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                FirebaseDatabase.getInstance()
                        .reference
                        .child(ChatUtil.ARG_CHAT_ROOMS)
                        .child(chatRoom).addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                                val chatBean = dataSnapshot?.getValue(ChatBean::class.java)
                                if (mChatRecyclerAdapter == null) {
                                    mChatRecyclerAdapter = ChatRecyclerAdapter(mContext, ArrayList<ChatBean>()/*, object : ChatAdapterClickListner() {
                                    fun clickedItemPosition(url: String) {
                                        showZoomImage(url)
                                    }
                                }*/)
                                    chatRecycler.adapter = mChatRecyclerAdapter
                                } else {
                                    mChatRecyclerAdapter?.add(chatBean)
                                }
                                try {
                                    chatRecycler.scrollToPosition(mChatRecyclerAdapter!!.itemCount - 1)
                                } catch (e: Exception) {

                                }
                                noDataTxt.visibility = View.GONE
                                progressbar.visibility = View.GONE
                            }

                            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                            }

                            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                            }

                            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                progressbar.visibility = View.GONE
                            }
                        })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //  mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        })
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        private val ARG_CHATFOR = "chatFor"
        private val ARG_CLUB_ID = "clubId"
        //activities
        private val ARG_ACTIVITYID = "activityId"
        private val ARG_USERID = "userId"
        private val ARG_USERNAME = "userName"
        private val ARG_USERPROFILEIMG = "userProfileImg"
        //feeds
        private val ARG_FEED_ID = "feedId"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentChat.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstanceActivityChat(activityId: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, "activities")
            args.putString(ARG_ACTIVITYID, activityId)
            fragment.arguments = args
            return fragment
        }

        // TODO: Rename and change types and number of parameters
        fun newInstanceFeedsChat(feedsId: String, clubId: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, "feeds")
            args.putString(ARG_CLUB_ID, clubId)
            args.putString(ARG_FEED_ID, feedsId)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
