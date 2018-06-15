package com.clubz.data.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Profile : Serializable{

    var userId  = ""
    var full_name = ""
    var email = ""
    var country_code  = ""
    var contact_no  = ""
    var profile_image = ""
    var about_me = ""

    var dob = ""
    var skills = ""
    var interests = ""
    var affiliates = ""

    public fun getStringToList():List<String>?{
        return affiliates.split(",").map { it.trim() }
    }

    fun getListToString(str : List<String>): String {
        return str.joinToString()
    }

    fun getContactNo():String {
        return "("+country_code+") "+contact_no
    }

    fun getFormatedDOB() : String {  // 1989, November 13
        val pattern = "yyyy, MMMM dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(stringToDate(dob))
        if (date.isNullOrEmpty()) return "----/--/--"
        return date
    }

    private fun stringToDate(string: String) : Date {
        // yyyy-mm-dd hh:mm:ss
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.parse(string)
    }
}