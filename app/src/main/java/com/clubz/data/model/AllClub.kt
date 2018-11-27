package com.clubz.data.model

class AllClub {

    companion object {
        val TAG = AllClub::class.java.simpleName
        val TABLE = "AllClubs"

        // Labels Table Columns names
        val KEY_ClubID = "clubId"
        val KEY_ClubName = "clubName"
        val KEY_ClubSilent = "clubSilent"

        val KEY_ClubDescription = "clubDescription"
        val KEY_ClubIcon = "clubIcon"
        val KEY_ClubImage = "clubImage"
        val KEY_ClubFoundationDate = "clubFoundationDate"
        val KEY_ClubEmail = "clubEmail"
        val KEY_ClubWebsite = "clubWebsite"
        val KEY_ClubContactNo = "clubContactNo"
        val KEY_ClubCountryCode = "clubCountryCode"
        val KEY_ClubCity = "clubCity"
        val KEY_ClubLocation = "clubLocation"
        val KEY_ClubAddress = "clubAddress"
        val KEY_ClubLatitude = "clubLatitude"
        val KEY_ClubLongitude = "clubLongitude"
        val KEY_ClubCategoryId = "clubCategoryId"
        val KEY_TermsConditions = "termsConditions"
        val KEY_ClubCategoryName = "clubCategoryName"
        val KEY_UserId = "userId"
        val KEY_FullName = "fullName"
        val KEY_UserImage = "userImage"
        val KEY_ClubUserStatus = "clubUserStatus"
        val KEY_UserRole = "userRole"
        val KEY_ContactNo = "contactNo"
        val KEY_ContactNoVisibility = "contactNoVisibility"
        val KEY_ProfileImage = "profileImage"
        val KEY_ClubUserId = "clubUserId"
        val KEY_IsAllowFeeds = "isAllowFeeds"
        val KEY_ClubType = "clubType"
        val KEY_CommentCount = "commentCount"
        val KEY_Status = "status"
        val KEY_Crd = "crd"
        val KEY_Upd = "upd"
        val KEY_Distance = "distance"
        val KEY_Members = "members"
    }

    var clubId: Int? = null
    var club_name: String? = null
    var notSilent: String? = null

    var club_description = ""
    var club_icon = ""
    var club_image = ""
    var club_foundation_date = ""
    var club_email = ""
    var club_website = ""
    var club_contact_no = ""
    var club_country_code = ""
    var club_city = ""
    var club_location = ""
    var club_address = ""
    var club_latitude = ""
    var club_longitude = ""
    var club_category_id = ""
    var terms_conditions = ""
    var club_category_name = ""
    var user_id = ""
    var full_name = ""
    var user_image = ""
    var club_user_status = ""
    var user_role = ""
    var contact_no = ""
    var contact_no_visibility = ""
    var profile_image = ""
    var clubUserId = ""
    var is_allow_feeds = ""
    var club_type = ""
    var comment_count = ""
    var status = ""
    var crd = ""
    var upd = ""
    var distance = ""
    var members : Int = 0
    var isVisiableBody :Boolean = false

    override fun toString(): String {
        return club_name.toString()
    }
}