package com.clubz.ui.user_activities.model

import android.os.Parcel
import android.os.Parcelable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActivitiesBean {


    /**
     * status : success
     * message : ok
     * data : [{"event_date":"2018-08-30","is_my_activity":"1","image":"http://clubz.co/dev/uploads/activity_image/52072a11df8241b5f81d067b3f3285be.jpg","activityName":"Playing Competition","activityId":"23","club_name":"Kidzee","is_like":"0","userId":"4","full_name":"Chiranjib Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"23","event_title":"Kinder","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"10","fee_type":"Voluntary","max_users":"10","total_users":"0","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-31","is_my_activity":"1","image":"http://clubz.co/dev/uploads/activity_image/9bb9df510531e254efae1ecfecd4e7c4.jpg","activityName":"Hello Activity","activityId":"24","club_name":"My New Club","is_like":"0","userId":"4","full_name":"Chiranjib Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"24","event_title":"Qqqw","event_date":"2018-08-31","event_time":"06:15:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"25","fee_type":"Voluntary","max_users":"25","total_users":"3","joined_users":"3","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-31","is_my_activity":"0","image":"http://clubz.co/dev/uploads/activity_image/094c1bd05c40cecadb9ab11296e347a6.jpg","activityName":"Football Tournament ","activityId":"4","club_name":"Friend Zone","is_like":"0","userId":"5","full_name":"Chiru Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"4","event_title":"he ha","event_date":"2018-08-31","event_time":"18:32:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"10","fee_type":"Dynamic","max_users":"15","total_users":"6","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-21","is_my_activity":"0","image":"http://clubz.co/dev/uploads/activity_image/2df298341171443b76820528afc00acb.jpg","activityName":"New Test Activity","activityId":"16","club_name":"Friend Zone","is_like":"0","userId":"5","full_name":"Chiru Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"19","event_title":"Hellloo","event_date":"2018-08-21","event_time":"18:02:00","description":"","location":"Manorama Ganj, Indore, Madhya Pradesh 452001, India","latitude":"22.718937","longitude":"75.884408","fee":"0","fee_type":"Free","max_users":"0","total_users":"3","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-30","is_my_activity":"0","image":"http://clubz.co/dev/backend_asset/custom/images/clubDefault.png","activityName":"My new","activityId":"17","club_name":"Test Club","is_like":"0","userId":"5","full_name":"Chiru Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"11","event_title":"My date","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"Pipliyahana, Indore, Madhya Pradesh, India","latitude":"22.7093523","longitude":"75.9014392","fee":"0","fee_type":"Fixed","max_users":"0","total_users":"3","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"},{"activityEventId":"12","event_title":"My date","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"Pipliyahana, Indore, Madhya Pradesh, India","latitude":"22.7093523","longitude":"75.9014392","fee":"0","fee_type":"Fixed","max_users":"0","total_users":"3","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-30","is_my_activity":"0","image":"http://clubz.co/dev/backend_asset/custom/images/clubDefault.png","activityName":"My new","activityId":"17","club_name":"Test Club","is_like":"0","userId":"5","full_name":"Chiru Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"11","event_title":"My date","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"Pipliyahana, Indore, Madhya Pradesh, India","latitude":"22.7093523","longitude":"75.9014392","fee":"0","fee_type":"Fixed","max_users":"0","total_users":"3","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"},{"activityEventId":"12","event_title":"My date","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"Pipliyahana, Indore, Madhya Pradesh, India","latitude":"22.7093523","longitude":"75.9014392","fee":"0","fee_type":"Fixed","max_users":"0","total_users":"3","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-28","is_my_activity":"0","image":"http://clubz.co/dev/backend_asset/custom/images/clubDefault.png","activityName":"Fan meeting","activityId":"6","club_name":"Kim hyun joong2","is_like":"0","userId":"11","full_name":"Alka","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"8","event_title":"Meet","event_date":"2018-08-28","event_time":"21:02:00","description":"","location":"Berbah, Sleman Regency, Special Region of Yogyakarta, Indonesia","latitude":"-7.802725799999999","longitude":"110.43812539999999","fee":"500","fee_type":"Dynamic","max_users":"1000","total_users":"0","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-31","is_my_activity":"0","image":"http://clubz.co/dev/backend_asset/custom/images/clubDefault.png","activityName":"Test","activityId":"15","club_name":"Kim hyun joong2","is_like":"0","userId":"11","full_name":"Alka","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Soon","events":[{"activityEventId":"16","event_title":"EOM","event_date":"2018-08-31","event_time":"17:54:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"0","fee_type":"Fixed","max_users":"0","total_users":"0","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]},{"event_date":"2018-08-04","is_my_activity":"1","image":"http://clubz.co/dev/uploads/activity_image/f5faa71e87e7a2db9cc4bb04f3f977ae.jpg","activityName":"My 1st Activity","activityId":"1","club_name":"Kidzee","is_like":"0","userId":"4","full_name":"Chiranjib Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Others"},{"event_date":"2018-08-10","is_my_activity":"1","image":"http://clubz.co/dev/uploads/activity_image/f5faa71e87e7a2db9cc4bb04f3f977ae.jpg","activityName":"My 1st Activity","activityId":"1","club_name":"Kidzee","is_like":"0","userId":"4","full_name":"Chiranjib Ganguly","device_token":"","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","listType":"Others"}]
     */

    var status: String? = null
    var message: String? = null
    var hasAffiliates: Int? = null
    var dateTime: String? = null
    var data: List<DataBean>? = null

    class DataBean() : Parcelable{
        /**
         * event_date : 2018-08-30
         * is_my_activity : 1
         * image : http://clubz.co/dev/uploads/activity_image/52072a11df8241b5f81d067b3f3285be.jpg
         * activityName : Playing Competition
         * activityId : 23
         * club_name : Kidzee
         * is_like : 0
         * userId : 4
         * full_name : Chiranjib Ganguly
         * device_token :
         * profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * listType : Soon
         * events : [{"activityEventId":"23","event_title":"Kinder","event_date":"2018-08-30","event_time":"06:30:00","description":"","location":"502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","latitude":"22.705138200000004","longitude":"75.9090618","fee":"10","fee_type":"Voluntary","max_users":"10","total_users":"0","joined_users":"0","is_confirm":"0","hasJoined":"0","hasAffiliatesJoined":"0"}]
         */

        /*var event_date: String? = null
        var is_my_activity: String? = null
        var image: String? = null
        var activityName: String? = null
        var activityId: String? = null
        var club_name: String? = null
        var clubId: String? = null
        var is_like: String? = null
        var userId: String? = null
        var full_name: String? = null
        var device_token: String? = null
        var profile_image: String? = null
        var listType: String? = null
        var events: List<EventsBean>? = null
        var visible = false
        var is_hide: String? = null*/

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
        var events: List<EventsBean>? = null

        constructor(parcel: Parcel) : this() {
            activityEventId = parcel.readString()
            event_date = parcel.readString()
            is_my_activity = parcel.readString()
            image = parcel.readString()
            activityName = parcel.readString()
            is_hide = parcel.readString()
            activityId = parcel.readString()
            name = parcel.readString()
            leader_id = parcel.readString()
            club_id = parcel.readString()
            creator_id = parcel.readString()
            location = parcel.readString()
            latitude = parcel.readString()
            longitude = parcel.readString()
            fee_type = parcel.readString()
            fee = parcel.readString()
            min_users = parcel.readString()
            max_users = parcel.readString()
            user_role = parcel.readString()
            description = parcel.readString()
            terms_conditions = parcel.readString()
            is_cancel = parcel.readString()
            status = parcel.readString()
            crd = parcel.readString()
            upd = parcel.readString()
            club_name = parcel.readString()
            clubId = parcel.readString()
            is_like = parcel.readString()
            userId = parcel.readString()
            full_name = parcel.readString()
            device_token = parcel.readString()
            profile_image = parcel.readString()
            listType = parcel.readString()

            creator_phone = parcel.readString()
            contact_no_visibility = parcel.readString()
            leader_name = parcel.readString()
            leader_prflimage = parcel.readString()
            leader_phno = parcel.readString()
            leader_contact_no_visibility = parcel.readString()
            totalUser = parcel.readString()
            visible = parcel.readByte() != 0.toByte()
        }

        class EventsBean():Parcelable {


            /**
             * activityEventId : 23
             * event_title : Kinder
             * event_date : 2018-08-30
             * event_time : 06:30:00
             * description :
             * location : 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
             * latitude : 22.705138200000004
             * longitude : 75.9090618
             * fee : 10
             * fee_type : Voluntary
             * max_users : 10
             * total_users : 0
             * joined_users : 0
             * is_confirm : 0
             * hasJoined : 0
             * hasAffiliatesJoined : 0
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

            constructor(parcel: Parcel) : this() {
                activityEventId = parcel.readString()
                event_title = parcel.readString()
                event_date = parcel.readString()
                event_time = parcel.readString()
                description = parcel.readString()
                location = parcel.readString()
                latitude = parcel.readString()
                longitude = parcel.readString()
                fee = parcel.readString()
                fee_type = parcel.readString()
                min_users = parcel.readString()
                max_users = parcel.readString()
                total_users = parcel.readString()
                joined_users = parcel.readString()
                confirm_users = parcel.readString()
                confirm_userlist = parcel.readString()
                is_confirm = parcel.readString()
                hasJoined = parcel.readString()
                hasAffiliatesJoined = parcel.readString()
                is_cancel = parcel.readString()
                list_type = parcel.readString()
            }

            fun getDate(): String {
                // String input = "2014-04-25 17:03:13";
                val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                //  val outputFormat = SimpleDateFormat("E, MMM dd, yyyy")
                //   val outputFormat = SimpleDateFormat("MMM'.' dd")
                val outputFormat = SimpleDateFormat("MMM dd")
                try {
                    return outputFormat.format(inputFormat.parse(event_date)).toUpperCase()
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
                    format = "AM"
                } else if (hour == 12) {
                    format = "PM"
                } else if (hour > 12) {
                    hour -= 12
                    format = "PM"
                } else {
                    format = "AM"
                }
                formatedTime = "$hour:$minute $format"
                return formatedTime
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(activityEventId)
                parcel.writeString(event_title)
                parcel.writeString(event_date)
                parcel.writeString(event_time)
                parcel.writeString(description)
                parcel.writeString(location)
                parcel.writeString(latitude)
                parcel.writeString(longitude)
                parcel.writeString(fee)
                parcel.writeString(fee_type)
                parcel.writeString(min_users)
                parcel.writeString(max_users)
                parcel.writeString(total_users)
                parcel.writeString(joined_users)
                parcel.writeString(confirm_users)
                parcel.writeString(confirm_userlist)
                parcel.writeString(is_confirm)
                parcel.writeString(hasJoined)
                parcel.writeString(hasAffiliatesJoined)
                parcel.writeString(is_cancel)
                parcel.writeString(list_type)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<EventsBean> {
                override fun createFromParcel(parcel: Parcel): EventsBean {
                    return EventsBean(parcel)
                }

                override fun newArray(size: Int): Array<EventsBean?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(activityEventId)
            parcel.writeString(event_date)
            parcel.writeString(is_my_activity)
            parcel.writeString(image)
            parcel.writeString(activityName)
            parcel.writeString(is_hide)
            parcel.writeString(activityId)
            parcel.writeString(name)
            parcel.writeString(leader_id)
            parcel.writeString(club_id)
            parcel.writeString(creator_id)
            parcel.writeString(location)
            parcel.writeString(latitude)
            parcel.writeString(longitude)
            parcel.writeString(fee_type)
            parcel.writeString(fee)
            parcel.writeString(min_users)
            parcel.writeString(max_users)
            parcel.writeString(user_role)
            parcel.writeString(description)
            parcel.writeString(terms_conditions)
            parcel.writeString(is_cancel)
            parcel.writeString(status)
            parcel.writeString(crd)
            parcel.writeString(upd)
            parcel.writeString(club_name)
            parcel.writeString(clubId)
            parcel.writeString(is_like)
            parcel.writeString(userId)
            parcel.writeString(full_name)
            parcel.writeString(device_token)
            parcel.writeString(profile_image)
            parcel.writeString(listType)
            parcel.writeString(creator_phone)
            parcel.writeString(contact_no_visibility)
            parcel.writeString(leader_name)
            parcel.writeString(leader_prflimage)
            parcel.writeString(leader_phno)
            parcel.writeString(leader_contact_no_visibility)
            parcel.writeString(totalUser)
            parcel.writeByte(if (visible) 1 else 0)
        }
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<DataBean> {
            override fun createFromParcel(parcel: Parcel): DataBean {
                return DataBean(parcel)
            }

            override fun newArray(size: Int): Array<DataBean?> {
                return arrayOfNulls(size)
            }
        }
    }
}