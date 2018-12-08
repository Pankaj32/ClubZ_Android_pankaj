package com.clubz.ui.user_activities.model

/**
 * Created by chiranjib on 4/7/18.
 */
class ActivityDetailsResponce {

    /**
     * status : success
     * message : Activity created successfully
     * details : {"activityId":"36","name":"Kid Best Performer","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee_type":"Dynamic","fee":"10","min_users":"3","max_users":"7","user_role":"Club Manager","description":"Kids are the only reason the first one for a new message was sent using Tapatalk","terms_conditions":"Terms and condition and a good idea of 68","image":"http://clubz.co/dev/uploads/activity_image/64c37950b2c749ef3a93a2dbdf0c28a3.jpg","creator_name":"Dharmraj","leader_name":"","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","club_name":"Kidzee"}
     */

    private var status: String? = null
    private var message: String? = null
    private var details: DetailsBean? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getDetails(): DetailsBean? {
        return details
    }

    fun setDetails(details: DetailsBean) {
        this.details = details
    }

    class DetailsBean {
        /**
         * activityId : 36
         * name : Kid Best Performer
         * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
         * latitude : 22.705138200000004
         * longitude : 75.9090618
         * fee_type : Dynamic
         * fee : 10
         * min_users : 3
         * max_users : 7
         * user_role : Club Manager
         * description : Kids are the only reason the first one for a new message was sent using Tapatalk
         * terms_conditions : Terms and condition and a good idea of 68
         * image : http://clubz.co/dev/uploads/activity_image/64c37950b2c749ef3a93a2dbdf0c28a3.jpg
         * creator_name : Dharmraj
         * leader_name :
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * club_name : Kidzee
         * clubId : 103
         */

        var activityId: String? = null
        var name: String? = null
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
        var image: String? = null
        var creator_name: String? = null
        var leader_name: String? = null
        var creator_profile_image: String? = null
        var club_name: String? = null
        var clubId: String? = null
        var clubLeaderId: String? = null
    }
}