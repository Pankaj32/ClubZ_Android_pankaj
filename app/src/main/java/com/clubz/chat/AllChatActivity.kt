package com.clubz.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.adapter.ChatRecyclerAdapter
import com.clubz.chat.model.ChatBean
import com.clubz.chat.model.ChatHistoryBean
import com.clubz.chat.model.ClubBean
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.utils.Constants
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mvc.imagepicker.ImagePicker
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText
import kotlinx.android.synthetic.main.activity_all_chat.*
import java.io.File
import java.io.IOException
import java.util.ArrayList

class AllChatActivity : AppCompatActivity(), View.OnClickListener {

    private val ARG_CHATFOR = "chatFor"
    private val ARG_CLUB_ID = "clubId"
    //activities
    private val ARG_ACTIVITY_ID = "activityId"
    private val ARG_ACTIVITY_NAME = "activityName"
    private val ARG_USERID = "userId"
    private val ARG_USERNAME = "userName"
    private val ARG_USERPROFILEIMG = "userProfileImg"
    //feeds
    private val ARG_FEED_ID = "feedId"
    private val ARG_FEED_NAME = "feedName"
    //ads
    private val ARG_AD_ID = "adId"
    private val ARG_AD_NAME = "adName"
    //
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"

    private var chatFor = ""

    private var clubId = ""
    private var clubOwnerId = ""
    /*  private var userId = ""
      private var userName = ""
      private var userProfileImg = ""

      private var activityId = ""
      private var activityName = ""

      private var feedId = ""
      private var feedName = ""

      private var adId = ""
      private var adName = ""*/

    private var historyId = ""
    private var historyName = ""

    private var mstorage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var app: FirebaseApp? = null
    private var chatRoom = ""
    private var chatHistoryRoom = ""
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var mChatRecyclerAdapter: ChatRecyclerAdapter? = null
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var noDataTxt: TextView? = null
    private var silentTxt: TextView? = null
    private var progressbar: ProgressBar? = null
    private var txtMsg: EmojiconEditText? = null
    private var chatRecycler: RecyclerView? = null
    private var memberList = ArrayList<MemberBean>()

    private var emojIcon: EmojIconActions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chat)

        noDataTxt = findViewById<EditText>(R.id.noDataTxt)
        silentTxt = findViewById<TextView>(R.id.silentTxt)
        progressbar = findViewById<ProgressBar>(R.id.progressbar)
        txtMsg = findViewById<EmojiconEditText>(R.id.txtMsg)
        chatRecycler = findViewById<RecyclerView>(R.id.chatRecycler)

        app = FirebaseApp.getInstance()
        mstorage = FirebaseStorage.getInstance(app!!)

        val arguments = intent.extras
        if (arguments != null) {
            chatFor = arguments.getString(ARG_CHATFOR)
            when (chatFor) {
                ChatUtil.ARG_NEWS_FEED -> {
                    clubId = arguments.getString(ARG_CLUB_ID)
                    historyId = arguments.getString(ARG_HISTORY_ID)
                    historyName = arguments.getString(ARG_HISTORY_NAME)
                    chatRoom = clubId + "_" + historyId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor + "_" + historyName

                    //getFeedStatus()
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    // getMessageFromFirebaseUser()
                }
                ChatUtil.ARG_ACTIVITIES -> {
                    clubId = arguments.getString(ARG_CLUB_ID)
                    historyId = arguments.getString(ARG_HISTORY_ID)
                    historyName = arguments.getString(ARG_HISTORY_NAME)
                    chatRoom = clubId + "_" + historyId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor + "_" + historyName
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Activity Chat is On Development Mode"*/
                }
                ChatUtil.ARG_ADS -> {
                    clubId = arguments.getString(ARG_CLUB_ID)
                    historyId = arguments.getString(ARG_HISTORY_ID)
                    historyName = arguments.getString(ARG_HISTORY_NAME)
                    chatRoom = clubId + "_" + historyId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor + "_" + historyName
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Ads Chat is On Development Mode"*/
                }
            }
        }
        titleTxt.text = historyName
        sentButton.setOnClickListener(this)
        sendPicBtn.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        emojIcon?.setUseSystemEmoji(false)
        emojIcon = EmojIconActions(this, rootView, txtMsg, emoji)
        emojIcon?.ShowEmojIcon()
        emojIcon?.setIconsIds(R.drawable.keyboard_ico, R.drawable.ic_smilely_ico)
        emojIcon?.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {

            }

            override fun onKeyboardClose() {

            }
        })
    }

    private fun getClubOwner() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB)
                .child(clubId).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        val club = p0?.getValue(ClubBean::class.java)
                        clubOwnerId = club?.ownerId!!
                    }
                })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sentButton -> {
                if (txtMsg?.text.toString().trim().isNotEmpty()) {
                    sendMessage(txtMsg?.text.toString(), "text", chatFor)
                } else {
                 //   Toast.makeText(this, R.string.please_type, Toast.LENGTH_LONG).show()
                }
            }
            R.id.sendPicBtn -> {
                permissionPopUp()
            }
            R.id.backBtn -> {
                finish()
            }
        }
    }

    private fun sendMessage(message: String, msgType: String, chatType: String) {
        if (message != "") {
            var msg = ""
            txtMsg?.setText("")
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
                    for (member in memberList) {
                        sendToChatHistory(member, chatBean, databaseReference)
                    }
                    sendToOwnerChatHistory(chatBean, databaseReference)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //   mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                }
            })
        }
    }


    /*fun getFeedStatus() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_NEWS_FEED)
                .child(clubId)
                .child(feedsId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val feedsBean = dataSnapshot?.getValue(FeedBean::class.java)
                        if (feedsBean?.isCommentAllow.equals("1")) {
                            getMessageFromFirebaseUser()
                        } else {
                            txtMsg.setFocusable(false)
                            txtMsg.setText("Comment disable")
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }

             })
}*/

    private fun getUserStatus() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(clubId)
                .child(ClubZ.currentUser?.id).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val memberBean = dataSnapshot?.getValue(MemberBean::class.java)
                        if (memberBean != null) {
                            if (memberBean.silent == "1") {
                                silentTxt?.visibility = View.GONE
                                silentTxt?.isClickable = true
                                chatRecycler?.visibility = View.VISIBLE
                                noDataTxt?.visibility = View.GONE
                                if (mChatRecyclerAdapter == null) getMessageFromFirebaseUser()
                            } else {
                                silentTxt?.visibility = View.VISIBLE
                                chatRecycler?.visibility = View.GONE
                                noDataTxt?.visibility = View.VISIBLE
                            }
                        } else {
                            getMessageFromFirebaseUser()
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
                                    val chatbeans = ArrayList<ChatBean>()
                                    chatbeans.add(chatBean!!)
                                    mChatRecyclerAdapter = ChatRecyclerAdapter(this@AllChatActivity, chatbeans/*, object : ChatAdapterClickListner() {
                                    fun clickedItemPosition(url: String) {
                                        showZoomImage(url)
                                    }
                                }*/)
                                    chatRecycler?.adapter = mChatRecyclerAdapter
                                } else {
                                    mChatRecyclerAdapter?.add(chatBean)
                                }
                                try {
                                    chatRecycler?.scrollToPosition(mChatRecyclerAdapter!!.itemCount - 1)
                                } catch (e: Exception) {

                                }
                                noDataTxt?.visibility = View.GONE
                                progressbar?.visibility = View.GONE
                            }

                            override fun onChildChanged(dataSnapshot: DataSnapshot?, s: String?) {
                            }

                            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {

                            }

                            override fun onChildMoved(dataSnapshot: DataSnapshot?, s: String?) {

                            }

                            override fun onCancelled(databaseError: DatabaseError?) {
                                progressbar?.visibility = View.GONE
                            }
                        })
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                //  mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        })
    }

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(this@AllChatActivity, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, sendPicBtn, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (this@AllChatActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        } else if (this@AllChatActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        } else {
                            callIntent(Constants.INTENTCAMERA)
                        }
                    } else {
                        callIntent(Constants.INTENTCAMERA)
                    }
                    R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                        isCameraSelected = false
                        if (this@AllChatActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        } else {
                            callIntent(Constants.INTENTGALLERY)
                        }
                    } else {
                        callIntent(Constants.INTENTGALLERY)
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }

    fun callIntent(caseid: Int) {

        when (caseid) {
            Constants.INTENTCAMERA -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg");
                imageUri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(this@AllChatActivity, BuildConfig.APPLICATION_ID + ".provider", file)
                        } else {
                            Uri.fromFile(file)
                        }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//USE file code in_ this case
                startActivityForResult(intent, Constants.REQUEST_CAMERA)
            }
            Constants.INTENTGALLERY -> {
                ImagePicker.pickImage(this)
                // com.clubz.utils.picker.ImagePicker.pickImage(this@NewActivities)
            }
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(this@AllChatActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(this@AllChatActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(this@AllChatActivity, arrayOf(Manifest.permission.INTERNET),
                        Constants.MY_PERMISSIONS_REQUEST_INTERNET)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isCameraSelected) callIntent(Constants.INTENTGALLERY)
                } else {
                    Toast.makeText(this@AllChatActivity, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@AllChatActivity, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(this@AllChatActivity, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(this@AllChatActivity, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this)
                } else {
                    Toast.makeText(this@AllChatActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this)
                } else {
                    Toast.makeText(this@AllChatActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                try {
                    if (result != null)
                        sendFileFireBase(result.uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        isCameraSelected = false
    }

    private fun sendFileFireBase(selectedImageUri: Uri) {
        progressbar?.visibility = View.VISIBLE
        storageRef = mstorage?.getReference("chat_photos_" + getString(R.string.app_name))
        val photoRef = storageRef?.child(selectedImageUri.lastPathSegment)
        photoRef?.putFile(selectedImageUri)?.addOnSuccessListener { taskSnapshot ->
            val fireBaseUri = taskSnapshot.downloadUrl
            Log.e("TAG", "onSuccess: ")
            progressbar?.visibility = View.GONE
            sendMessage(fireBaseUri!!.toString(), "image", chatFor)
        }
                ?.addOnFailureListener { e ->
                    progressbar?.visibility = View.GONE
                    Log.e("TAG", "onFailure: " + e.message)
                    Toast.makeText(this@AllChatActivity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    Log.e("TAG", "onProgress: $progress")
                }
    }

    private fun getClubMembers() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB_MEMBER)
                .child(clubId).addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    @SuppressLint("LongLogTag")
                    override fun onChildChanged(dataSnapShot: DataSnapshot?, p1: String?) {
                        val memberBean = dataSnapShot?.getValue(MemberBean::class.java)
                        /*for (data in memberList){
                            if(memberBean!!.userId.equals(memberBean.userId)){

                            }
                        }*/
                        Log.e("clubMember Status Changed : ", memberBean?.clubId)
                    }

                    override fun onChildAdded(dataSnapShot: DataSnapshot?, p1: String?) {
                        val memberBean = dataSnapShot?.getValue(MemberBean::class.java)
                        if (memberBean?.joind == 1) memberList.add(memberBean)
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
    }

    private fun sendToChatHistory(member: MemberBean,
                                  chatBean: ChatBean,
                                  databaseReference: DatabaseReference) {
        val chatHistory = ChatHistoryBean()
        // chatHistory.deleteby = ""
        chatHistory.chatType = chatFor
        chatHistory.chatHistoryRoom = chatHistoryRoom
        chatHistory.clubId = clubId
        chatHistory.historyId = historyId
        chatHistory.historyName = historyName

        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = ""
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(member.userId).child(chatHistoryRoom).setValue(chatHistory)
    }

    private fun sendToOwnerChatHistory(chatBean: ChatBean,
                                       databaseReference: DatabaseReference) {
        val chatHistory = ChatHistoryBean()
        // chatHistory.deleteby = ""
        chatHistory.chatType = chatFor
        chatHistory.chatHistoryRoom = chatHistoryRoom
        chatHistory.clubId = clubId
        chatHistory.historyId = historyId
        chatHistory.historyName = historyName
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = ""
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(clubOwnerId).child(chatHistoryRoom).setValue(chatHistory)
    }
}
