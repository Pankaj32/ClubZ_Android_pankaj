package com.clubz.data.model

class AllEvents {

    companion object {
        val TAG = AllEvents::class.java.simpleName
        val TABLE = "AllEvents"

        // Labels Table Columns names
        val KEY_ActivityId = "activityId"
        val KEY_ActivityEventId ="activityEventId"
        val KEY_EventTitle ="eventTitle"
        val KEY_EventDate = "eventDate"
        val KEY_EventTime ="eventTime"
        val KEY_Description ="description"
        val KEY_Location ="location"
        val KEY_Latitude ="latitude"
        val KEY_Longitude ="longitude"
        val KEY_Fee ="fee"
        val KEY_FeeType ="feeType"
        val KEY_MinUsers ="minUsers"
        val KEY_MaxUsers ="maxUsers"
        val KEY_TotalUsers ="totalUsers"
        val KEY_JoinedUsers ="joinedUsers"
        val KEY_ConfirmUsers ="confirmUsers"
        val KEY_ConfirmUserlist ="confirmUserlist"
        val KEY_IsConfirm ="isConfirm"
        val KEY_HasJoined ="hasJoined"
        val KEY_HasAffiliatesJoined ="hasAffiliatesJoined"
        val KEY_IsCancel ="isCancel"
        val KEY_ListType ="listType"
    }
    var activityId: String? = null
    var activityEventId: String? = null
    var event_title: String? = null
    var event_date: String? = null
    var event_time: String? = null
    var description: String? = null
    var location: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var fee: String? = null
    var fee_type: String? = null
    var min_users: String? = null
    var max_users: String? = null
    var total_users: String? = null
    var joined_users: String? = null
    var confirm_users: String? = null
    var confirm_userlist: String? = null
    var is_confirm: String? = null
    var hasJoined: String? = null
    var hasAffiliatesJoined: String? = null
    var is_cancel: String? = null
    var list_type: String? = null

    override fun toString(): String {
        return event_title.toString()
    }
}