package com.clubz.data.model

class AllFavContact {

    companion object {
        val TAG = AllFavContact::class.java.simpleName
        val TABLE = "AllFavContacts"

        // Labels Table Columns names
        val KEY_UserId = "userId"
        val KEY_DeviceToken = "deviceToken"
        val KEY_ClubId = "clubId"
        val KEY_ClubName = "clubName"
        val KEY_Name = "name"
        val KEY_ProfileImage = "profileImage"


    }
    var userId: String? = null
    var device_token: String? = null
    var clubId: String? = null
    var club_name: String? = null
    var name: String? = null
    var profile_image: String? = null

    override fun toString(): String {
        return name.toString()
    }
}