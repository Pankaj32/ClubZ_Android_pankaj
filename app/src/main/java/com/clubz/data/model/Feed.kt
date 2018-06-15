package com.clubz.data.model

import android.content.Context
import com.clubz.utils.DateTimeUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Feed : Serializable{

    var newsFeedId : Int? = null
    var news_feed_title  = ""
    var news_feed_description  = ""
    @SerializedName("tagName")
    var tagName  = ""
    var datetime  = ""
    var club_name  = ""
    var clubId  = ""

    @SerializedName("full_name")
    var user_name  = ""

    var profile_image  = ""

    @SerializedName("userId")
    var user_id  = ""

    var likes : Int = 0
    var comments : Int = 0
    var is_comment_allow : Int = 0
    var bookmarks  = ""
    var isLiked : Int = 0
    var isBookmarked = ""
    var news_feed_attachment  = ""
    var club_image  = ""
    var club_icon  = ""
    var currentDateTime  = ""
    var crd  = ""

    fun getTimeAgo(ctx: Context) : String{
        return DateTimeUtil.getTimeAgo(stringToDate(datetime).time, ctx)
    }

    fun getDate() : String {
        val pattern = "MMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(stringToDate(datetime))
        return date
    }

    fun getFormatedDate() : String {
        val pattern = "EEEE, MMMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(stringToDate(datetime))
        return date
    }

    private fun stringToDate(string: String) : Date {
        // yyyy-mm-dd hh:mm:ss
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val myDate = simpleDateFormat.parse(string)
        return myDate
    }
}