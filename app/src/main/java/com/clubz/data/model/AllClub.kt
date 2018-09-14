package com.clubz.data.model

class AllClub {

    companion object {
        val TAG = AllClub::class.java.simpleName
        val TABLE = "AllClubs"

        // Labels Table Columns names
        val KEY_ClubID = "clubId"
        val KEY_ClubName = "clubName"
        val KEY_ClubSilent = "clubSilent"

    }

    var clubId: Int? = null
    var club_name: String? = null
    var notSilent: String? = null

    override fun toString(): String {
        return club_name.toString()
    }
}