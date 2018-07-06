package com.clubz.chat.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by chiranjib on 5/7/18.
 */
@IgnoreExtraProperties
class ChatBean {
    var deleteby: String? = null
    var chatType:String?=null
    var image: Int = 0
    var imageUrl: String? = null
    var message: String? = null
    var senderId: String? = null
    var senderName: String? = null
    var timestamp: Any? = null
}