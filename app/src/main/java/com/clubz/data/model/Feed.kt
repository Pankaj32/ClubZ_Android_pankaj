package com.clubz.data.model

import java.text.SimpleDateFormat
import java.util.*

class Feed {

    var newsFeedId : Int? = null
    var news_feed_title  = ""
    var news_feed_description  = ""
    var datetime  = ""
    var club_name  = ""
    var user_name  = ""
    var likes  = ""
    var comments  = ""
    var bookmarks  = ""
    var isLiked = ""
    var isBookmarked = ""
    var news_feed_attachment  = ""
    var club_image  = ""
    var club_icon  = ""
    var currentDateTime  = ""
    var crd  = ""


    fun getDate() : String {
        val pattern = "MMM dd, yyyy"
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