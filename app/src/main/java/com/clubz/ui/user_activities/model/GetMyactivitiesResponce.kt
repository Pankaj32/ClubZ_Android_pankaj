package com.clubz.ui.user_activities.model

import com.clubz.ui.user_activities.expandable_recycler_view.ParentListItem
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by chiranjib on 7/6/18.
 */
class GetMyactivitiesResponce {
    /**
     * status : success
     * message : ok
     * data : [{"image":"http://clubz.co/dev/uploads/activity_image/6658df64236ffec11b469522dd34233c.jpg","activityName":"Football Match","activityId":"12","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/1056b7090cdc255f2af7ea21c203a472.jpg","activityName":"My activity","activityId":"11","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/ab5330781463c058c48118eb976344d1.jpg","activityName":"Holy Festival","activityId":"10","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/a2d8260f55348603ef453101fe1c51d6.jpg","activityName":"New activity pankaj","activityId":"8","club_name":"Lions Club","events":[]},{"image":"http://clubz.co/dev/uploads/activity_image/8fcfdf8b7831c2e53689d21f54754b3e.jpg","activityName":"Annual Function","activityId":"7","club_name":"Lions Club","events":[{"activityEventId":"2","event_title":"my event","event_date":"2018-05-30","event_time":"07:10:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"1","event_title":"event fav","event_date":"2018-05-30","event_time":"12:08:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"3","event_title":"new event of activity","event_date":"2018-05-31","event_time":"13:00:00","description":"test","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"26","fee_type":"Fixed","max_users":"6","is_confirm":"0","total_users":"1","joined_users":"0"}]}]
     */

    private var status: String? = null
    private var message: String? = null
    private var data: List<DataBean>? = null

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

    fun getData(): List<DataBean>? {
        return data
    }

    fun setData(data: List<DataBean>) {
        this.data = data
    }

    class DataBean : ParentListItem {
        override fun getChildItemList(): List<EventsBean>? {
            return events
        }

        override fun isInitiallyExpanded(): Boolean {
            return false
        }

        /**
         * image : http://clubz.co/dev/uploads/activity_image/6658df64236ffec11b469522dd34233c.jpg
         * activityName : Football Match
         * activityId : 12
         * club_name : Lions Club
         * events : []
         */

        var image: String? = null
        var activityName: String? = null
        var activityId: String? = null
        var club_name: String? = null
        var is_hide: String? = null
        var visible=false
        var events: List<EventsBean>? = null
        var is_Confirm: Boolean? = false

        class EventsBean {
            /**
             * activityEventId : 4
             * event_title : diwali celebration
             * event_date : 2018-05-31
             * event_time : 17:00:00
             * description :
             * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
             * latitude : 22.705138200000004
             * longitude : 75.9090618
             * fee : 25
             * fee_type : Voluntary
             * max_users : 5
             * is_confirm : 0
             * total_users : 1
             * joined_users : 1
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
            var max_users: String? = null
            var is_confirm: String? = null
            var total_users: String? = null
            var joined_users: String? = null
            var parentIndex: Int? = null
            var childIndex: Int? = null

            fun getDate(): String {
                // String input = "2014-04-25 17:03:13";
                val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                //  val outputFormat = SimpleDateFormat("E, MMM dd, yyyy")
                val outputFormat = SimpleDateFormat("MMM dd")
                try {
                    return outputFormat.format(inputFormat.parse(event_date))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                return ""
            }

            fun getTime(): String {
                var formatedTime = ""
                val TimeList = event_time!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val hourSt = TimeList[0]
                val minute = TimeList[1]
                var hour = Integer.parseInt(hourSt)
                val format: String
                if (hour == 0) {
                    hour += 12
                    format = "A.M."
                } else if (hour == 12) {
                    format = "P.M."
                } else if (hour > 12) {
                    hour -= 12
                    format = "P.M."
                } else {
                    format = "A.M."
                }
                formatedTime = "$hour:$minute $format"
                return formatedTime
            }
        }
    }
}