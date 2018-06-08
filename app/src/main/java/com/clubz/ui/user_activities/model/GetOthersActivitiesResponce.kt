package com.clubz.ui.user_activities.model

import com.clubz.ui.user_activities.expandable_recycler_view.ParentListItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by chiranjib on 4/6/18.
 */
class GetOthersActivitiesResponce {
    /**
     * status : success
     * message : ok
     * data : {"today":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}],"tomorrow":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}],"soon":[{"image":"http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg","activityName":"Diwaly Festivals ","activityId":"9","club_name":"Mindiii Sport","is_like":"0","events":[{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]}]}
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
        var today: List<TodayBean>? = null
        var tomorrow: List<TomorrowBean>? = null
        var soon: List<SoonBean>? = null
        var others: List<OthersBean>? = null

        fun getTodayList(): List<TodayBean>? {
            return today
        }

        class TodayBean : ParentListItem {
            override fun getChildItemList(): List<EventsBean>? {
                return events
            }

            override fun isInitiallyExpanded(): Boolean {
               return false
            }

            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            var image: String? = null
            var activityName: String? = null
            var activityId: String? = null
            var club_name: String? = null
            var is_like: String? = null
            var is_Confirm: Boolean? = false
            var events: List<EventsBean>? = null

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
                var parentIndex: Int?=null
                var childIndex: Int?=null

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

        class TomorrowBean : ParentListItem{
            override fun getChildItemList(): List<EventsBeanX>? {
               return events
            }

            override fun isInitiallyExpanded(): Boolean {
                return false
            }

            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            var image: String? = null
            var activityName: String? = null
            var activityId: String? = null
            var club_name: String? = null
            var is_like: String? = null
            var is_Confirm: Boolean? = false
            var events: List<EventsBeanX>? = null

            class EventsBeanX {
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
                var parentIndex: Int?=null
                var childIndex: Int?=null

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

        class SoonBean: ParentListItem {
            override fun isInitiallyExpanded(): Boolean {
         return false
            }

            override fun getChildItemList(): List<EventsBeanXX>? {
              return events
            }

            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            var image: String? = null
            var activityName: String? = null
            var activityId: String? = null
            var club_name: String? = null
            var is_like: String? = null
            var is_Confirm: Boolean? = false
            var events: List<EventsBeanXX>? = null

            class EventsBeanXX {
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
                var parentIndex: Int?=null
                var childIndex: Int?=null

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

        class OthersBean: ParentListItem {
            override fun isInitiallyExpanded(): Boolean {
         return false
            }

            override fun getChildItemList(): List<EventsBeanXXX>? {
              return events
            }

            /**
             * image : http://clubz.co/dev/uploads/activity_image/thumb/6d3f751f2200fd4f5998f4240bea2f15.jpg
             * activityName : Diwaly Festivals
             * activityId : 9
             * club_name : Mindiii Sport
             * is_like : 0
             * events : [{"activityEventId":"4","event_title":"diwali celebration","event_date":"2018-05-31","event_time":"17:00:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"1"},{"activityEventId":"5","event_title":"crackers ","event_date":"2018-06-01","event_time":"10:07:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"},{"activityEventId":"6","event_title":"next event of mine ","event_date":"2018-06-05","event_time":"19:13:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"5","is_confirm":"0","total_users":"1","joined_users":"0"}]
             */

            var image: String? = null
            var activityName: String? = null
            var activityId: String? = null
            var club_name: String? = null
            var is_like: String? = null
            var is_Confirm: Boolean? = false
            var events: List<EventsBeanXXX>? = null

            class EventsBeanXXX {
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
                var parentIndex: Int?=null
                var childIndex: Int?=null

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
}