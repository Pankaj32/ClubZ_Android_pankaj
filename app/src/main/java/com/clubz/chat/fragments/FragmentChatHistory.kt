package com.clubz.chat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.AllChatActivity
import com.clubz.chat.adapter.ChatHistoryAdapter
import com.clubz.chat.model.ActivityBean
import com.clubz.chat.model.AdBean
import com.clubz.chat.model.ChatHistoryBean
import com.clubz.chat.model.FeedBean
import com.clubz.chat.util.ChatUtil
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_fragment_chat_history.*
/*import okhttp3.internal.Util*/
import java.util.*
import kotlin.Comparator

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentChatHistory : Fragment(), ChatHistoryAdapter.OnItemClick {

    private val ARG_CHATFOR = "chatFor"
    private val ARG_CLUB_ID = "clubId"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mContext: Context? = null
    private var progressBar: ProgressBar? = null
    private var chatHistoryRecycler: RecyclerView? = null
    private var nodataLay: CardView? = null
    private var chatHistoryBeanList = ArrayList<ChatHistoryBean>()
    private var chatHistoryAdapter: ChatHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_chat_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressBar = view.findViewById<ProgressBar>(R.id.progressbar)
        nodataLay = view.findViewById<CardView>(R.id.nodataLay)
        chatHistoryRecycler = view.findViewById<RecyclerView>(R.id.chatHistoryRecycler)
        chatHistoryAdapter = ChatHistoryAdapter(mContext, chatHistoryBeanList, this)
        chatHistoryRecycler?.adapter = chatHistoryAdapter
        getChatHistory()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    fun getChatHistory() {
        chatHistoryBeanList.clear()

        FirebaseDatabase.getInstance().reference.child(ChatUtil.ARG_CHAT_HISTORY).child(ClubZ.currentUser?.id).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot?, s: String?) {
                progressBar?.setVisibility(View.GONE)
                val dataSnapshots = dataSnapshot?.children?.iterator()
                val historyBean = dataSnapshot?.getValue<ChatHistoryBean>(ChatHistoryBean::class.java)
                nodataLay?.setVisibility(View.GONE)
                var isfind = false
                if (chatHistoryBeanList.size > 0) {
                    for (i in chatHistoryBeanList.indices) {
                        if (chatHistoryBeanList[i].chatHistoryRoom.equals(historyBean!!.chatHistoryRoom)) {
                            chatHistoryBeanList[i] = historyBean
                            isfind = true
                            break
                        }
                    }
                    if (!isfind) chatHistoryBeanList.add(historyBean!!)
                } else {
                    chatHistoryBeanList.add(historyBean!!)
                }
                shortList()
                // chatHistoryAdapter?.notifyDataSetChanged()
                chatHistoryRecycler?.visibility = View.VISIBLE

                    when (historyBean?.chatType) {
                        ChatUtil.ARG_NEWS_FEED -> {
                            getFeedsImage(historyBean)
                        }
                        ChatUtil.ARG_ACTIVITIES -> {
                            getActivityImage(historyBean)
                        }
                        ChatUtil.ARG_ADS -> {
                            getAdsImage(historyBean)
                        }
                    }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, s: String?) {
                progressBar?.setVisibility(View.GONE)
                val historyBean = dataSnapshot?.getValue<ChatHistoryBean>(ChatHistoryBean::class.java)
                // chatHistoryBeanList.add(historyBean!!)
                //  getSenderProfileImage(historyBean!!)
                nodataLay?.setVisibility(View.GONE)
                var isfind = false
                if (chatHistoryBeanList.size > 0) {
                    for (i in chatHistoryBeanList.indices) {
                        if (chatHistoryBeanList[i].chatHistoryRoom.equals(historyBean!!.chatHistoryRoom)) {
                            chatHistoryBeanList[i] = historyBean
                            isfind = true
                            break
                        }
                    }
                    if (!isfind) chatHistoryBeanList.add(historyBean!!)
                } else {
                    chatHistoryBeanList.add(historyBean!!)
                }
                shortList()
                chatHistoryRecycler?.visibility = View.VISIBLE
                try {
                    when (historyBean?.chatType) {
                        ChatUtil.ARG_NEWS_FEED -> {
                            getFeedsImage(historyBean)
                        }
                        ChatUtil.ARG_ACTIVITIES -> {
                            getActivityImage(historyBean)
                        }
                        ChatUtil.ARG_ADS -> {
                            getAdsImage(historyBean)
                        }
                    }
                } catch (e: Exception) {

                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
                ///     progressBar.setVisibility(View.GONE)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, s: String?) {
                //     progressBar.setVisibility(View.GONE)
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                //     progressBar.setVisibility(View.GONE)
            }
        })
        if (chatHistoryBeanList.size == 0) {
            progressBar?.setVisibility(View.GONE)
            nodataLay?.setVisibility(View.VISIBLE)
        }
    }

    private fun getAdsImage(historyBean: ChatHistoryBean) {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_ADS)
                .child(historyBean.clubId)
                .child(historyBean.historyId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            val adBean = dataSnapshot?.getValue(AdBean::class.java)
                            for (data in chatHistoryBeanList) {
                                if (data.chatType?.equals(ChatUtil.ARG_ADS)!!) {
                                    if (data.historyId.equals(adBean?.adId)) {
                                        data.profilePic = adBean?.adImage
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }
                        chatHistoryAdapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

                })
    }

    private fun getActivityImage(historyBean: ChatHistoryBean) {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_ACTIVITIES)
                .child(historyBean.clubId)
                .child(historyBean.historyId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            val activityBean = dataSnapshot?.getValue(ActivityBean::class.java)
                            for (data in chatHistoryBeanList) {
                                if (data.chatType?.equals(ChatUtil.ARG_ACTIVITIES)!!) {
                                    if (data.historyId.equals(activityBean?.activityId)) {
                                        data.profilePic = activityBean?.activityImage
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }
                        chatHistoryAdapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

                })
    }

    private fun getFeedsImage(historyBean: ChatHistoryBean) {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_NEWS_FEED)
                .child(historyBean.clubId)
                .child(historyBean.historyId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            val feedBean = dataSnapshot?.getValue(FeedBean::class.java)
                            for (data in chatHistoryBeanList) {

                                if (data.chatType?.equals(ChatUtil.ARG_NEWS_FEED)!!) {
                                    if (data.historyId.equals(feedBean?.feedId)) {
                                        data.profilePic = feedBean?.feedImage
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }
                        chatHistoryAdapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

                })
    }

    private fun shortList() {
        Collections.sort<ChatHistoryBean>(chatHistoryBeanList) { a1, a2 ->
            if (a1.timestamp == null || a2.timestamp == null)
                -1
            else {
                val long1 = a1.timestamp.toString().toLong()
                val long2 = a2.timestamp.toString().toLong()
                long2.compareTo(long1)
            }
        }
        chatHistoryAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(historyBean: ChatHistoryBean?) {
        startActivity(Intent(mContext, AllChatActivity::class.java)
                .putExtra(ARG_CHATFOR, historyBean?.chatType)
                .putExtra(ARG_CLUB_ID, historyBean?.clubId)
                .putExtra(ARG_HISTORY_ID, historyBean?.historyId)
                .putExtra(ARG_HISTORY_NAME, historyBean?.historyName)
        )

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentChatHistory().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}
