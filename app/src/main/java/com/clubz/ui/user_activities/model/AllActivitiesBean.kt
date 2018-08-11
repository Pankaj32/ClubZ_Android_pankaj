package com.clubz.ui.user_activities.model

class AllActivitiesBean {
    private var data: DataBean? = null

    class DataBean {

        var image: String? = null
        var activityName: String? = null
        var activityId: String? = null
        var club_name: String? = null
        var userId: String? = null
        var full_name: String? = null
        var device_token: String? = null
        var profile_image: String? = null
        var is_like: String? = null
        var is_Confirm: Boolean? = false
        var visible = false
        var events=ArrayList<EventsBean>()
        var type: String? = null
        var is_hide: String? = null

        class EventsBean {

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
            var max_users: String? = null
            var is_confirm: String? = null
            var total_users: String? = null
            var joined_users: String? = null
            var hasJoined: String? = null
            var hasAffiliatesJoined: String? = null
        }
    }
}