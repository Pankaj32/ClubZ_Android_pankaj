package com.clubz.data.model

class ClubName {

    companion object {
        val TAG = ClubName::class.java.simpleName
        val TABLE = "MyClubs"

        // Labels Table Columns names
        val KEY_ClubID = "clubId"
        val KEY_ClubName = "clubName"

    }

    var clubId: Int? = null
    var club_name: String? = null
    var club_image =""
    var isSelected = false

    override fun toString(): String {
        return club_name.toString()
    }
}