package com.clubz.data.model
import java.io.Serializable
import java.lang.Long.valueOf
import java.text.SimpleDateFormat
import java.util.*

class ClubMember : Serializable {

    var tag_name = ""
    var user_nickname = ""
    var userId = ""
    var clubUserId = ""
    var isLiked = 0
    var full_name = ""
    var member_status = ""
    var profile_image = ""
    var country_code = "+91"
    var contact_no = "9977141811"

    val requestDateTime = ""
    val distance : String = ""

    fun addTag(tag : String){
        if(tag_name.isEmpty())
           tag_name = tag
        else tag_name = tag_name+","+tag;
    }

    fun distance() : String{
        if(distance.isNullOrEmpty())
            return ""
        return String.format("%.2f", distance.toFloat())
    }


    fun getDate() : String {
        val pattern = "MMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(stringToDate(requestDateTime))
        return date
    }

    private fun stringToDate(string: String) : Date {
        // yyyy-mm-dd hh:mm:ss
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val myDate = simpleDateFormat.parse(string)
        return myDate
    }

    fun getNickname():String {
        if(user_nickname.isNotEmpty()) return user_nickname else return full_name;
    }

    fun getFirstTagName() : String{
        if(tag_name.isNotEmpty()){
            val index = tag_name.indexOf(",")
            if(index==-1) return tag_name
            else return tag_name.substring(0, index )
        } else return full_name
    }
}