package com.clubz.data.model
import java.io.Serializable
import java.lang.Long.valueOf
import java.text.SimpleDateFormat
import java.util.*

class ClubMember : Serializable {

    var tag_name = ""
    val userId = ""
    val clubUserId = ""
    val full_name = ""
    var member_status = ""
    val profile_image = ""

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
}