package com.clubz.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import com.clubz.BuildConfig
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.chat.adapter.ChatRecyclerAdapter
import com.clubz.chat.model.*
import com.clubz.chat.util.ChatUtil
import com.clubz.helper.fcm.FcmNotificationBuilder
import com.clubz.ui.dialogs.ZoomDialog
import com.clubz.utils.Constants
import com.clubz.utils.KeyboardUtil
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.picker.ImagePicker.getImageResized
import com.clubz.utils.picker.ImageRotator
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.vanniktech.emoji.EmojiPopup

import kotlinx.android.synthetic.main.activity_all_chat.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AllChatActivity : AppCompatActivity(), View.OnClickListener, ChatRecyclerAdapter.onClick {
    override fun onItemClick() {
        KeyboardUtil.hideKeyboard(this)
    }

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
    private val ARG_AD_PIC = "adPic"
    //
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private val ARG_HISTORY_PIC = "historyPic"

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
    private var historyPic = ""

    private var mstorage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var app: FirebaseApp? = null
    private var chatRoom = ""
    private var chatHistoryRoom = ""
    private var mUserId = ""
    private var mUserName = ""
    private var mUserPic = ""
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var mChatRecyclerAdapter: ChatRecyclerAdapter? = null
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var noDataTxt: TextView? = null
    private var silentTxt: TextView? = null
    private var progressbar: ProgressBar? = null
    private var topLay: RelativeLayout? = null

    //newemoji
    private var emoji: ImageView? = null
    internal var emojiPopup: EmojiPopup? = null
    private var memberList = ArrayList<MemberBean>()
    private var membersTokenList = ArrayList<MembersFBTokenBean>()

    private var isText = false
    val wrapper = ContextThemeWrapper(this@AllChatActivity, R.style.popstyle)
    var popupMenu: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chat)
        noDataTxt = findViewById<EditText>(R.id.noDataTxt)
        silentTxt = findViewById<TextView>(R.id.silentTxt)
        progressbar = findViewById<ProgressBar>(R.id.progressbar)
        topLay = findViewById<RelativeLayout>(R.id.topLay)
        emoji = findViewById<ImageView>(R.id.emoji)

        mUserId = ClubZ.currentUser!!.id
        mUserName = ClubZ.currentUser!!.full_name
        mUserPic= ClubZ.currentUser!!.profile_image
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
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor

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
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    getActivitiesJoined()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Activity Chat is On Development Mode"*/
                }
                ChatUtil.ARG_ADS -> {
                    clubId = arguments.getString(ARG_CLUB_ID)
                    historyId = arguments.getString(ARG_HISTORY_ID)
                    historyName = arguments.getString(ARG_HISTORY_NAME)
                    if(arguments.getString(ARG_HISTORY_PIC)!=null)
                    historyPic = arguments.getString(ARG_HISTORY_PIC)
                    chatRoom = clubId + "_" + historyId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + historyId + "_" + chatFor
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Ads Chat is On Development Mode"*/
                }
                ChatUtil.ARG_IDIVIDUAL -> {
                    historyId = arguments.getString(ARG_HISTORY_ID)
                    historyName = arguments.getString(ARG_HISTORY_NAME)
                    if(arguments.getString(ARG_HISTORY_PIC)!=null)
                    historyPic = arguments.getString(ARG_HISTORY_PIC)
                    chatRoom = if (mUserId.toInt() > historyId.toInt()) historyId + "_" + mUserId + "_" + chatFor else mUserId + "_" + historyId + "_" + chatFor
                    chatHistoryRoom = chatRoom
                    if (mChatRecyclerAdapter == null) getMessageFromFirebaseUser()
                    getMemberDeviceToken(historyId)
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Ads Chat is On Development Mode"*/
                }
            }
        }
        titleTxt.text = historyName
        sentButton.setOnClickListener(this)
        //    sendPicBtn.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        topLay!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                KeyboardUtil.hideKeyboard(this@AllChatActivity)
                return true
            }
        })

        // newEmoji
        emoji?.setColorFilter(ContextCompat.getColor(this, R.color.emoji_icons), PorterDuff.Mode.SRC_IN)
        emoji?.setOnClickListener({ ignore -> emojiPopup?.toggle() })
        setUpEmojiPopup()

        txtMsg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    isText = false
                    sentButton.setImageResource(R.drawable.ic_attach_file_black_24dp)
                    sentButton.setColorFilter(ContextCompat.getColor(this@AllChatActivity, R.color.nav_gray))
                } else {
                    isText = true
                    if (popupMenu != null) popupMenu!!.dismiss()
                    sentButton.setImageResource(R.drawable.ic_send_chat_24dp)
                    sentButton.setColorFilter(ContextCompat.getColor(this@AllChatActivity, R.color.primaryColor))
                }
            }
        })
    }

    private fun getClubOwner() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_CLUB)
                .child(clubId).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val club = p0?.getValue(ClubBean::class.java)
                        try {
                            clubOwnerId = club?.ownerId!!
                        } catch (e: Exception) {

                        }
                    }
                })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sentButton -> {
                if (isText) {
                    if (txtMsg?.text.toString().trim().isNotEmpty()) {
                        sendMessage(txtMsg?.text.toString(), "text", chatFor)
                    } else {
                        //   Toast.makeText(this, R.string.please_type, Toast.LENGTH_LONG).show()
                    }
                } else {
                    permissionPopUp()
                }

            }
            /*R.id.sendPicBtn -> {

            }*/
            R.id.backBtn -> {
                finish()
            }
        }
    }

    private fun sendMessage(message: String, msgType: String, chatType: String) {
        if (message != "") {
            var msg = ""
            txtMsg?.setText("")
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
                    if (chatFor.equals(ChatUtil.ARG_IDIVIDUAL)) {
                        sendToMyIndividualChatHistory(chatBean, databaseReference)
                        sendToOtherIndividualChatHistory(chatBean, databaseReference)
                    } else {
                        for (member in memberList) {
                            sendToChatHistory(member, chatBean, databaseReference)
                        }
                        sendToOwnerChatHistory(chatBean, databaseReference)
                    }
                    for (member in membersTokenList) {
                        if (chatFor.equals(ChatUtil.ARG_IDIVIDUAL)) {
                            sendPushNotificationToReceiver(mUserName,
                                    msg, "chat",
                                    chatFor, clubId, mUserId, mUserName, member.deviceToken!!)
                        } else {
                            if (!member.userId!!.equals(mUserId)) sendPushNotificationToReceiver(historyName,
                                    mUserName + ": " + msg, "chat",
                                    chatFor, clubId, historyId, historyName, member.deviceToken!!)
                        }

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //   mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                }
            })
        }
    }

    private fun sendPushNotificationToReceiver(title: String,
                                               message: String,
                                               notficationType: String,
                                               chatFor: String,
                                               clubId: String,
                                               historyId: String,
                                               historyName: String,
                                               firebaseToken: String) {
        FcmNotificationBuilder.initialize()
                .title(title)
                .message(message)
                .notificationType(notficationType)
                .chatFor(chatFor)
                .clubId(clubId)
                .historyId(historyId)
                .historyName(historyName)
                .firebaseToken(firebaseToken).send()
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
                .child(ClubZ.currentUser!!.id).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
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

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }

    fun getMessageFromFirebaseUser() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child(ChatUtil.ARG_CHAT_ROOMS).ref.orderByKey()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        FirebaseDatabase.getInstance()
                                .reference
                                .child(ChatUtil.ARG_CHAT_ROOMS)
                                .child(chatRoom).addChildEventListener(object : ChildEventListener {
                                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                        val chatBean = dataSnapshot?.getValue(ChatBean::class.java)
                                        if (mChatRecyclerAdapter == null) {
                                            val chatbeans = ArrayList<ChatBean>()
                                            chatbeans.add(chatBean!!)
                                            mChatRecyclerAdapter = ChatRecyclerAdapter(this@AllChatActivity, chatbeans, this@AllChatActivity/*, object : ChatAdapterClickListner() {
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

                                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                                    }

                                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                    }

                                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        progressbar?.visibility = View.GONE
                                    }
                                })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //  mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                    }
                })
        upadteReadWriteMessage()
    }

    private fun upadteReadWriteMessage() {
        FirebaseDatabase.getInstance().reference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(ClubZ.currentUser!!.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.hasChild(chatHistoryRoom)) {
                    FirebaseDatabase.getInstance().reference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(ClubZ.currentUser!!.id).child(chatHistoryRoom).child("read").setValue(1)
                }
            }
        })
    }

    fun permissionPopUp() {
        popupMenu = PopupMenu(wrapper, sentButton, Gravity.CENTER)
        popupMenu!!.getMenuInflater().inflate(R.menu.popupmenu, popupMenu!!.getMenu())
        popupMenu!!.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
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
        popupMenu!!.show()
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
                //  ImagePicker.pickImage(this)
                val intentgallery = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentgallery, Constants.SELECT_FILE)
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
                /* if (imageUri != null) {
                     CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this)
                 } else {
                     Toast.makeText(this@AllChatActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                 }*/
                var bm: Bitmap? = null
                bm = getImageResized(this@AllChatActivity, imageUri)
                val rotation = ImageRotator.getRotation(this@AllChatActivity, imageUri, true)
                bm = ImageRotator.rotate(bm, rotation)

                val file = File(this@AllChatActivity.getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg")
                val imageUri = FileProvider.getUriForFile(this@AllChatActivity, applicationContext.packageName + ".provider", file)


                if (file != null) {
                    try {
                        var outStream: OutputStream? = null
                        outStream = FileOutputStream(file)
                        bm!!.compress(Bitmap.CompressFormat.PNG, 80, outStream)
                        outStream!!.flush()
                        outStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                sendFileFireBase(imageUri)
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                /*  if (imageUri != null) {
                      CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this)
                  } else {
                      Toast.makeText(this@AllChatActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                  }*/

                /*var bm: Bitmap? = null
                val imageFile = getTemporalFile(this@AllChatActivity)
                val photoURI = Uri.fromFile(imageFile)*/
                var bm: Bitmap? = null
                bm = getImageResized(this@AllChatActivity, imageUri)
                val rotation = ImageRotator.getRotation(this@AllChatActivity, imageUri, true)
                bm = ImageRotator.rotate(bm, rotation)

                val file = File(this@AllChatActivity.getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg")
                val imageUri = FileProvider.getUriForFile(this@AllChatActivity, applicationContext.packageName + ".provider", file)


                if (file != null) {
                    try {
                        var outStream: OutputStream? = null
                        outStream = FileOutputStream(file)
                        bm!!.compress(Bitmap.CompressFormat.PNG, 80, outStream)
                        outStream!!.flush()
                        outStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                /*Intent i = new Intent(ChatActivity.this, CropActivity.class);
                i.putExtra("Image", imageUri.toString());
                i.putExtra("FROM", "gallery");
                startActivityForResult(i, 111);*/

                sendFileFireBase(imageUri)

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
            val fireBaseUri = taskSnapshot.uploadSessionUri
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
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    @SuppressLint("LongLogTag")
                    override fun onChildChanged(dataSnapShot: DataSnapshot, p1: String?) {
                        val memberBean = dataSnapShot?.getValue(MemberBean::class.java)
                        /*for (data in memberList){
                            if(memberBean!!.userId.equals(memberBean.userId)){

                            }
                        }*/
                        Log.e("clubMember Status Changed : ", memberBean?.clubId)
                    }

                    override fun onChildAdded(dataSnapShot: DataSnapshot, p1: String?) {
                        val memberBean = dataSnapShot?.getValue(MemberBean::class.java)

                        if (memberBean?.joind == 1) {
                            memberList.add(memberBean)
                            getMemberDeviceToken(memberBean.userId)
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
    }

    private fun getMemberDeviceToken(userId: String?) {
        databaseReference
                .child(ChatUtil.ARG_USERS)
                .child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0?.getValue(UserBean::class.java)
                        val memberTokenBean = MembersFBTokenBean()
                        memberTokenBean.userId = user?.uid
                        memberTokenBean.userName = user?.name
                        memberTokenBean.deviceToken = user?.firebaseToken
                        memberTokenBean.userPic = user?.profilePic
                        membersTokenList.add(memberTokenBean)
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
        chatHistory.profilePic = historyPic
        if (member.userId.equals(ClubZ.currentUser?.id)) chatHistory.read = 1
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref
                .child(member.userId!!)
                .child(chatHistoryRoom)
                .setValue(chatHistory)
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
        chatHistory.profilePic = historyPic
        if (clubOwnerId.equals(ClubZ.currentUser?.id)) chatHistory.read = 1
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref
                .child(clubOwnerId)
                .child(chatHistoryRoom)
                .setValue(chatHistory)
    }

    private fun sendToMyIndividualChatHistory(chatBean: ChatBean,
                                              databaseReference: DatabaseReference) {
        val chatHistory = ChatHistoryBean()
        // chatHistory.deleteby = ""
        chatHistory.chatType = chatFor
        chatHistory.chatHistoryRoom = chatHistoryRoom
        chatHistory.clubId = ""
        chatHistory.historyId = historyId
        chatHistory.historyName = historyName
        chatHistory.read = 1
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = historyPic
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref
                .child(mUserId)
                .child(chatHistoryRoom)
                .setValue(chatHistory)
    }

    private fun sendToOtherIndividualChatHistory(chatBean: ChatBean,
                                                 databaseReference: DatabaseReference) {
        val chatHistory = ChatHistoryBean()
        // chatHistory.deleteby = ""
        chatHistory.chatType = chatFor
        chatHistory.chatHistoryRoom = chatHistoryRoom
        chatHistory.clubId = ""
        chatHistory.historyId = mUserId
        chatHistory.historyName = mUserName
        chatHistory.read = 0
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = mUserPic
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp

        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(historyId).child(chatHistoryRoom)
                .setValue(chatHistory)
    }

    //newEmoji
    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener { ignore -> Log.e("CHATALLACTIVITY", "Clicked on Backspace") }
                .setOnEmojiClickListener { ignore, ignore2 -> Log.e("CHATALLACTIVITY", "Clicked on emoji") }
                .setOnEmojiPopupShownListener { emoji?.setImageResource(R.drawable.ic_keyboard_ico) }
                .setOnSoftKeyboardOpenListener { ignore -> Log.d("CHATALLACTIVITY", "Opened soft keyboard") }
                .setOnEmojiPopupDismissListener { emoji?.setImageResource(R.drawable.ic_smilely_ico) }
                .setOnSoftKeyboardCloseListener { Log.d("CHATALLACTIVITY", "Closed soft keyboard") }
                .build(txtMsg!!)
    }

    override fun onBackPressed() {
        if (emojiPopup != null && emojiPopup!!.isShowing()) {
            emojiPopup!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        if (emojiPopup != null) {
            emojiPopup!!.dismiss()
        }
        super.onStop()
    }

    override fun onImageClick(imgUrl: String?) {
        val dialog = ZoomDialog(this@AllChatActivity, imgUrl!!)
        dialog.show()
    }

    private fun getActivitiesJoined() {
        FirebaseDatabase.getInstance()
                .reference
                .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                .child(historyId).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var isActivityJoind = false
                        for (values in p0.children) {
                            if (values.getValue(String::class.java).equals(ClubZ.currentUser!!.id)) {
                                isActivityJoind = true
                                break
                            }
                        }
                        if (isActivityJoind) {
                            silentTxt?.visibility = View.GONE
                            silentTxt?.isClickable = true
                            chatRecycler?.visibility = View.VISIBLE
                            noDataTxt?.visibility = View.GONE
                        } else {
                            silentTxt?.visibility = View.VISIBLE
                            silentTxt?.text = getString(R.string.first_join_this_activity)
                            chatRecycler?.visibility = View.GONE
                            noDataTxt?.visibility = View.VISIBLE
                        }

                    }
                })

    }
}
