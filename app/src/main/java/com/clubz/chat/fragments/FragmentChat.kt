package com.clubz.chat.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
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
import com.clubz.utils.cropper.CropImageView
import com.clubz.utils.picker.ImageRotator
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mvc.imagepicker.ImagePicker
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.z_user_profile_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class FragmentChat : Fragment(), View.OnClickListener, ChatRecyclerAdapter.onClick {
    override fun onItemClick() {
        KeyboardUtil.hideKeyboard(activity)
    }

    private var mContext: Context? = null
    private var chatFor = ""

    private var clubId = ""
    private var clubOwnerId = ""
    private var userId = ""
    private var userName = ""
    private var userProfileImg = ""
    //for activity chat
    private var activityId = ""
    private var activityName = ""

    //for feed chat
    private var feedId = ""
    private var feedName = ""
    //for ads chat
    private var adId = ""
    private var adName = ""
    //for all chat
    private var historyId = ""
    private var historyName = ""

    private var mstorage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null
    private var app: FirebaseApp? = null
    private var chatRoom = ""
    private var chatHistoryRoom = ""
    private var databaseReference = FirebaseDatabase.getInstance().reference
    private var mChatRecyclerAdapter: ChatRecyclerAdapter? = null
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var noDataTxt: TextView? = null
    private var silentTxt: TextView? = null
    private var progressbar: ProgressBar? = null
    private var topLay: RelativeLayout? = null
    //    private var txtMsg: EmojiconEditText? = null
    //  private var chatRecycler: RecyclerView? = null
    private var memberList = ArrayList<MemberBean>()
    //private var joinedActivityUsers = ArrayList<String>()
    var activityJoiendListner: ValueEventListener? = null
    var statusEventListner: ValueEventListener? = null


    private var txtMsg: EmojiEditText? = null
    private var emoji: ImageView? = null
    internal var emojiPopup: EmojiPopup? = null
    private var isText = false
    private var membersTokenList = ArrayList<MembersFBTokenBean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        noDataTxt = view.findViewById<EditText>(R.id.noDataTxt)
        silentTxt = view.findViewById<TextView>(R.id.silentTxt)
        progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
        txtMsg = view.findViewById(R.id.txtMsg)
        emoji = view.findViewById<ImageView>(R.id.emoji)
        topLay = view.findViewById<RelativeLayout>(R.id.topLay)
        // chatRecycler = view.findViewById<RecyclerView>(R.id.chatRecycler)
        topLay!!.setOnClickListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        app = FirebaseApp.getInstance()
        mstorage = FirebaseStorage.getInstance(app!!)
        if (arguments != null) {
            chatFor = arguments!!.getString(ARG_CHATFOR)
            when (chatFor) {
                ChatUtil.ARG_NEWS_FEED -> {
                    clubId = arguments!!.getString(ARG_CLUB_ID)
                    feedId = arguments!!.getString(ARG_FEED_ID)
                    feedName = arguments!!.getString(ARG_FEED_NAME)
                    chatRoom = clubId + "_" + feedId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + feedId + "_" + chatFor
                    historyId = feedId
                    historyName = feedName
                    //getFeedStatus()
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    // getMessageFromFirebaseUser()
                }
                ChatUtil.ARG_ACTIVITIES -> {
                    clubId = arguments!!.getString(ARG_CLUB_ID)
                    activityId = arguments!!.getString(ARG_ACTIVITY_ID)
                    activityName = arguments!!.getString(ARG_ACTIVITY_NAME)
                    chatRoom = clubId + "_" + activityId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + activityId + "_" + chatFor
                    historyId = activityId
                    historyName = activityName
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    getActivitiesJoined()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Activity Chat is On Development Mode"*/
                }
                ChatUtil.ARG_ADS -> {
                    clubId = arguments!!.getString(ARG_CLUB_ID)
                    adId = arguments!!.getString(ARG_AD_ID)
                    adName = arguments!!.getString(ARG_AD_NAME)
                    chatRoom = clubId + "_" + adId + "_" + chatFor
                    chatHistoryRoom = clubId + "_" + adId + "_" + chatFor
                    historyId = adId
                    historyName = adName
                    getUserStatus()
                    getClubMembers()
                    getClubOwner()
                    /*silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Ads Chat is On Development Mode"*/
                }
            }
        }
        sentButton.setOnClickListener(this)
        //    sendPicBtn.setOnClickListener(this)
        /*emojIcon?.setUseSystemEmoji(false)
        emojIcon = EmojIconActions(mContext, rootView, txtMsg, emoji)
        emojIcon?.ShowEmojIcon()
        emojIcon?.setIconsIds(R.drawable.keyboard_ico, R.drawable.ic_smilely_ico)
        emojIcon?.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {

            }

            override fun onKeyboardClose() {

            }
        })*/

        // newEmoji
        emoji?.setColorFilter(ContextCompat.getColor(mContext!!, R.color.emoji_icons), PorterDuff.Mode.SRC_IN)
        emoji?.setOnClickListener({ ignore -> emojiPopup?.toggle() })
        setUpEmojiPopup()
        txtMsg?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    isText = false
                    sentButton.setImageResource(R.drawable.ic_attach_file_black_24dp)
                    sentButton.setColorFilter(ContextCompat.getColor(mContext!!, R.color.nav_gray))
                } else {
                    isText = true
                    if (popupMenu != null) popupMenu!!.dismiss()
                    sentButton.setImageResource(R.drawable.ic_send_chat_24dp)
                    sentButton.setColorFilter(ContextCompat.getColor(mContext!!, R.color.primaryColor))
                }
            }
        })
        KeyboardUtil.hideKeyboard(activity)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context

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
                permissionPopUp()
            }*/
            R.id.topLay -> {
                KeyboardUtil.hideKeyboard(activity)
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
                    for (member in membersTokenList) {
                        sendPushNotificationToReceiver(historyName,
                                msg, "chat", chatFor, clubId, historyId, historyName, member.deviceToken!!)
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
        statusEventListner = databaseReference
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
                                            mChatRecyclerAdapter = ChatRecyclerAdapter(mContext, chatbeans, this@FragmentChat/*, object : ChatAdapterClickListner() {
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
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(ClubZ.currentUser?.id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.hasChild(chatHistoryRoom)) {
                    databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(ClubZ.currentUser?.id!!).child(chatHistoryRoom).child("read").setValue(1)
                }
            }
        })
    }


    var popupMenu: PopupMenu? = null
    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(mContext, R.style.popstyle)
        popupMenu = PopupMenu(wrapper, sentButton, Gravity.CENTER)
        popupMenu!!.getMenuInflater().inflate(R.menu.popupmenu, popupMenu!!.getMenu())
        popupMenu!!.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                isCameraSelected = true
                when (item.getItemId()) {
                    R.id.pop1 -> if (Build.VERSION.SDK_INT >= 23) {
                        if (mContext?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTCAMERA)
                        } else if (mContext?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            callIntent(Constants.INTENTREQUESTREAD)
                        } else {
                            callIntent(Constants.INTENTCAMERA)
                        }
                    } else {
                        callIntent(Constants.INTENTCAMERA)
                    }
                    R.id.pop2 -> if (Build.VERSION.SDK_INT >= 23) {
                        isCameraSelected = false
                        if (mContext?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            FileProvider.getUriForFile(mContext!!, BuildConfig.APPLICATION_ID + ".provider", file)
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
            Constants.INTENTREQUESTCAMERA -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA)
            Constants.INTENTREQUESTREAD -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            Constants.INTENTREQUESTWRITE -> {
            }

            Constants.INTENTREQUESTNET -> {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.INTERNET),
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
                    Toast.makeText(mContext, R.string.a_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraSelected) callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(mContext, R.string.a_camera_denied, Toast.LENGTH_LONG).show()
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isCameraSelected) callIntent(Constants.INTENTGALLERY) else callIntent(Constants.INTENTCAMERA)
            } else {
                Toast.makeText(mContext, R.string.a_permission_read, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            if (requestCode == Constants.SELECT_FILE) {
                imageUri = com.clubz.utils.picker.ImagePicker.getImageURIFromResult(mContext, requestCode, resultCode, data);
                /* if (imageUri != null) {
                     CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(this)
                 } else {
                     Toast.makeText(this@AllChatActivity, R.string.swr, Toast.LENGTH_SHORT).show()
                 }*/
                var bm: Bitmap? = null
                bm = com.clubz.utils.picker.ImagePicker.getImageResized(mContext, imageUri)
                val rotation = ImageRotator.getRotation(mContext, imageUri, true)
                bm = ImageRotator.rotate(bm, rotation)

                val file = File(mContext!!.getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg")
                val imageUri = FileProvider.getUriForFile(mContext!!, mContext!!.packageName + ".provider", file)


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
                bm = com.clubz.utils.picker.ImagePicker.getImageResized(mContext, imageUri)
                val rotation = ImageRotator.getRotation(mContext, imageUri, true)
                bm = ImageRotator.rotate(bm, rotation)

                val file = File(mContext!!.getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg")
                val imageUri = FileProvider.getUriForFile(mContext!!, mContext!!.packageName + ".provider", file)


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
                    Toast.makeText(mContext, "Failed " + e.message, Toast.LENGTH_SHORT).show()
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
                        //  Log.e("clubMember Status Changed : ", memberBean?.clubId)
                    }

                    override fun onChildAdded(dataSnapShot: DataSnapshot, p1: String?) {
                        val memberBean = dataSnapShot?.getValue(MemberBean::class.java)
                        if (memberBean?.joind == 1){
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
                        memberTokenBean.deviceToken = user?.firebaseToken
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
        if (member.userId.equals(ClubZ.currentUser?.id)) chatHistory.read = 1
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = ""
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(member.userId!!).child(chatHistoryRoom).setValue(chatHistory)
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
        if (clubOwnerId.equals(ClubZ.currentUser?.id)) chatHistory.read = 1
        chatHistory.image = chatBean.image
        chatHistory.imageUrl = chatBean.imageUrl
        chatHistory.profilePic = ""
        chatHistory.lastMessangerId = ClubZ.currentUser?.id
        chatHistory.lastMessanger = ClubZ.currentUser?.full_name
        chatHistory.message = chatBean.message
        chatHistory.timestamp = chatBean.timestamp
        databaseReference.child(ChatUtil.ARG_CHAT_HISTORY).ref.child(clubOwnerId).child(chatHistoryRoom).setValue(chatHistory)
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
                            if (club?.ownerId.isNullOrEmpty()) clubOwnerId = club?.ownerId!!
                        } catch (e: java.lang.Exception) {

                        }
                    }
                })
    }

    private fun getActivitiesJoined() {
        activityJoiendListner = databaseReference
                .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                .child(activityId).addValueEventListener(object : ValueEventListener {


                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (mContext != null) {
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
                    }
                })

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

    /*override fun onBackPressed() {
        if (emojiPopup != null && emojiPopup!!.isShowing()) {
            emojiPopup!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }*/

    override fun onStop() {
        if (emojiPopup != null) {
            emojiPopup!!.dismiss()
        }

        super.onStop()
    }

    override fun onImageClick(imgUrl: String?) {
        val dialog = ZoomDialog(mContext!!, imgUrl!!)
        dialog.show()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

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

        fun newInstanceActivityChat(activityId: String, clubId: String, activityName: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, ChatUtil.ARG_ACTIVITIES)
            args.putString(ARG_CLUB_ID, clubId)
            args.putString(ARG_ACTIVITY_ID, activityId)
            args.putString(ARG_ACTIVITY_NAME, activityName)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceAdChat(adId: String, clubId: String, adName: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, ChatUtil.ARG_ADS)
            args.putString(ARG_CLUB_ID, clubId)
            args.putString(ARG_AD_ID, adId)
            args.putString(ARG_AD_NAME, adName)
            fragment.arguments = args
            return fragment
        }

        // TODO: Rename and change types and number of parameters
        fun newInstanceFeedsChat(feedsId: String, clubId: String, feedName: String): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, ChatUtil.ARG_NEWS_FEED)
            args.putString(ARG_CLUB_ID, clubId)
            args.putString(ARG_FEED_ID, feedsId)
            args.putString(ARG_FEED_NAME, feedName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (databaseReference != null) {
            if (activityJoiendListner != null) databaseReference
                    .child(ChatUtil.ARG_ACTIVITY_JOIND_USER)
                    .child(activityId).removeEventListener(activityJoiendListner!!)
            if (statusEventListner != null) databaseReference
                    .child(ChatUtil.ARG_CLUB_MEMBER)
                    .child(clubId)
                    .child(ClubZ.currentUser!!.id).removeEventListener(statusEventListner!!)
        }
    }
}// Required empty public constructor