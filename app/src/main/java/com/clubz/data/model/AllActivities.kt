package com.clubz.data.model

class AllActivities {

    companion object {
        val TAG = AllActivities::class.java.simpleName
        val TABLE = "AllActivities"

        // Labels Table Columns names

        val KEY_ActivityEventId = "activityEventId"
        val KEY_EventDate ="eventDate"
        val KEY_IsMyActivity = "isMyActivity"
        val KEY_Image = "image"
        val KEY_ActivityName = "activityName"
        val KEY_Is_hide = "isHide"
        val KEY_ActivityId = "activityId"
        val KEY_Name = "name"
        val KEY_leaderId = "leaderId"
        val KEY_ClubId = "clubId"
        val KEY_CreatorId = "creatorId"
        val KEY_Location = "location"
        val KEY_Latitude = "latitude"
        val KEY_Longitude = "longitude"
        val KEY_FeeType = "feeType"
        val KEY_Fee = "fee"
        val KEY_MinUsers = "minUsers"
        val KEY_MaxUsers = "maxUsers"
        val KEY_UserRole = "userRole"
        val KEY_Description = "description"
        val KEY_TermsConditions = "termsConditions"
        val KEY_IsCancel = "isCancel"
        val KEY_Status = "status"
        val KEY_Crd = "crd"
        val KEY_Upd = "upd"
        val KEY_ClubName = "clubName"
        val KEY_IsLike = "is_like"
        val KEY_UserId = "userId"
        val KEY_FullName = "FullName"
        val KEY_DeviceToken = "deviceToken"
        val KEY_ProfileImage = "profileImage"
        val KEY_ListType = "listType"

        val KEY_CreatorPhone = "creatorPhone"
        val KEY_ContactNoVisibility = "contactNoVisibility"
        val KEY_LeaderName = "leaderName"
        val KEY_LeaderPrflimage = "leaderPrflimage"
        val KEY_LeaderPhno = "leaderPhno"
        val KEY_LeaderContactNoVisibility = "leaderContactNoVisibility"
        val KEY_TotalUser = "totalUser"

    }
    var activityEventId: String? = null
    var event_date: String? = null
    var is_my_activity: String? = null
    var image: String? = null
    var activityName: String? = null
    var is_hide: String? = null
    var activityId: String? = null
    var name: String? = null
    var leader_id: String? = null
    var club_id: String? = null
    var creator_id: String? = null
    var location: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var fee_type: String? = null
    var fee: String? = null
    var min_users: String? = null
    var max_users: String? = null
    var user_role: String? = null
    var description: String? = null
    var terms_conditions: String? = null
    var is_cancel: String? = null
    var status: String? = null
    var crd: String? = null
    var upd: String? = null
    var club_name: String? = null
    var clubId: String? = null
    var is_like: String? = null
    var userId: String? = null
    var full_name: String? = null
    var device_token: String? = null
    var profile_image: String? = null
    var listType: String? = null

    var creator_phone: String? = null
    var contact_no_visibility: String? = null
    var leader_name: String? = null
    var leader_prflimage: String? = null
    var leader_phno: String? = null
    var leader_contact_no_visibility: String? = null
    var totalUser: String? = null
    var visible = false

    override fun toString(): String {
        return activityName.toString()
    }
}