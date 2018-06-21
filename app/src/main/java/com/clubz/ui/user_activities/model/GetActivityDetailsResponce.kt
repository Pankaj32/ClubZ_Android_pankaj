package com.clubz.ui.user_activities.model

/**
 * Created by chiranjib on 21/6/18.
 */
class GetActivityDetailsResponce {

    /**
     * status : success
     * message : ok
     * data : {"activityId":"20","name":"Diwali Festival","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee_type":"Voluntary","fee":"25","min_users":"2","max_users":"10","user_role":"admin","description":"Diwali festival and we will get you on Friday","terms_conditions":"Terms and conditions and a good weekend and then delete it and you it","image":"http://clubz.co/dev/uploads/activity_image/523bd8f1619144b713495338aa882fd7.jpg","is_like":"0","leader_name":"","creator_name":"Chiranjib Ganguly","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","next_event":{"activityEventId":"15","event_title":"edfuyyy","event_date":"2018-06-27","event_time":"00:51:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary"}}
     */

    private var status: String? = null
    private var message: String? = null
    private var data: DataBean? = null

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

    fun getData(): DataBean? {
        return data
    }

    fun setData(data: DataBean) {
        this.data = data
    }

    class DataBean {
        /**
         * activityId : 20
         * name : Diwali Festival
         * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
         * latitude : 22.705138200000004
         * longitude : 75.9090618
         * fee_type : Voluntary
         * fee : 25
         * min_users : 2
         * max_users : 10
         * user_role : admin
         * description : Diwali festival and we will get you on Friday
         * terms_conditions : Terms and conditions and a good weekend and then delete it and you it
         * image : http://clubz.co/dev/uploads/activity_image/523bd8f1619144b713495338aa882fd7.jpg
         * is_like : 0
         * leader_name :
         * creator_name : Chiranjib Ganguly
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * next_event : {"activityEventId":"15","event_title":"edfuyyy","event_date":"2018-06-27","event_time":"00:51:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary"}
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
        var is_like: String? = null
        var leader_name: String? = null
        var creator_name: String? = null
        var creator_profile_image: String? = null
        var club_name: String? = null
        var next_event: NextEventBean? = null

        class NextEventBean {
            /**
             * activityEventId : 15
             * event_title : edfuyyy
             * event_date : 2018-06-27
             * event_time : 00:51:00
             * description :
             * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
             * latitude : 22.705138200000004
             * longitude : 75.9090618
             * fee : 25
             * fee_type : Voluntary
             */

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
        }
    }
}