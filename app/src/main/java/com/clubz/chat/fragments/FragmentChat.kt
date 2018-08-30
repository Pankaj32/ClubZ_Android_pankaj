package com.clubz.chat.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.clubz.BuildConfig
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.chat.adapter.ChatRecyclerAdapter
import com.clubz.chat.model.ChatBean
import com.clubz.chat.model.FeedBean
import com.clubz.chat.model.MemberBean
import com.clubz.chat.util.ChatUtil
import com.clubz.utils.Constants
import com.clubz.utils.cropper.CropImage
import com.clubz.utils.cropper.CropImageView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mvc.imagepicker.ImagePicker
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.io.IOException

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
    private var isCameraSelected: Boolean = false
    private var imageUri: Uri? = null
    private var noDataTxt: TextView? = null
    private var silentTxt: TextView? = null
    private var progressbar: ProgressBar? = null
    private var txtMsg: EmojiconEditText? = null
    private var chatRecycler: RecyclerView? = null


    private var mListener: OnFragmentInteractionListener? = null

    private var emojIcon: EmojIconActions? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_chat, container, false)
        noDataTxt = view.findViewById<EditText>(R.id.noDataTxt)
        silentTxt = view.findViewById<EditText>(R.id.silentTxt)
        progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
        txtMsg = view.findViewById<EmojiconEditText>(R.id.txtMsg)
        chatRecycler = view.findViewById<RecyclerView>(R.id.chatRecycler)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        app = FirebaseApp.getInstance()
        mstorage = FirebaseStorage.getInstance(app!!)
        if (arguments != null) {
            chatFor = arguments!!.getString(ARG_CHATFOR)
            when (chatFor) {
                "feeds" -> {
                    clubId = arguments!!.getString(ARG_CLUB_ID)
                    feedsId = arguments!!.getString(ARG_FEED_ID)
                    chatRoom = clubId + "_" + feedsId + "_" + chatFor
                    //getFeedStatus()
                    getUserStatus()
                    // getMessageFromFirebaseUser()
                }
                "activities" -> {
                    activityId = arguments!!.getString(ARG_ACTIVITYID)
                    /*userId = arguments!!.getString(ARG_USERID)
                    userName = arguments!!.getString(ARG_USERNAME)
                    userProfileImg = arguments!!.getString(ARG_USERPROFILEIMG)*/
                    silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Activity Chat is On Development Mode"
                }
                "ads" -> {
                    activityId = arguments!!.getString(ARG_AD_ID)
                    /*userId = arguments!!.getString(ARG_USERID)
                    userName = arguments!!.getString(ARG_USERNAME)
                    userProfileImg = arguments!!.getString(ARG_USERPROFILEIMG)*/
                    silentTxt?.visibility = View.VISIBLE
                    silentTxt?.isClickable = true
                    silentTxt?.text="Ads Chat is On Development Mode"
                }
            }
        }
        sentButton.setOnClickListener(this)
        sendPicBtn.setOnClickListener(this)
        emojIcon?.setUseSystemEmoji(false)
        emojIcon = EmojIconActions(mContext, rootView, txtMsg, emoji)
        emojIcon?.ShowEmojIcon()
        emojIcon?.setIconsIds(R.drawable.keyboard_ico, R.drawable.ic_smilely_ico)
        emojIcon?.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {

            }

            override fun onKeyboardClose() {

            }
        })
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
                if (txtMsg?.text.toString().isNotEmpty()) {
                    sendMessage(txtMsg?.text.toString(), "text", chatFor)
                } else {
                    Toast.makeText(mContext, R.string.please_type, Toast.LENGTH_LONG).show()
                }
            }
            R.id.sendPicBtn -> {
                permissionPopUp()
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
                            if (memberBean?.silent == "1") {
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
                                    mChatRecyclerAdapter = ChatRecyclerAdapter(mContext, ArrayList<ChatBean>()/*, object : ChatAdapterClickListner() {
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

                            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                            }

                            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                            }

                            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

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
        //ads
        private val ARG_AD_ID = "adId"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentChat.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstanceActivityChat(activityId: String/*, clubId: String*/): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, "activities")
            args.putString(ARG_ACTIVITYID, activityId)
            fragment.arguments = args
            return fragment
        }
        fun newInstanceAdChat(adId: String/*, clubId: String*/): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putString(ARG_CHATFOR, "ads")
            args.putString(ARG_AD_ID, adId)
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

    fun permissionPopUp() {
        val wrapper = ContextThemeWrapper(mContext, R.style.popstyle)
        val popupMenu = PopupMenu(wrapper, sendPicBtn, Gravity.CENTER)
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
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
        popupMenu.show()
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
                ImagePicker.pickImage(this)
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
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(mContext!!, this)
                } else {
                    Toast.makeText(activity, R.string.swr, Toast.LENGTH_SHORT).show()
                }
            }
            if (requestCode == Constants.REQUEST_CAMERA) {
                // val imageUri :Uri= com.tulia.Picker.ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(300, 200).setMaxCropResultSize(4000, 4000).setAspectRatio(300, 200).start(mContext!!, this)
                } else {
                    Toast.makeText(mContext, R.string.swr, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(mContext, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    Log.e("TAG", "onProgress: $progress")
                }
    }
}// Required empty public constructor