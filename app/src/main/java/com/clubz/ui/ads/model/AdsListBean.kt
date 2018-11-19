package com.clubz.ui.ads.model

import android.os.Parcel
import android.os.Parcelable
import java.text.ParseException
import java.text.SimpleDateFormat

class AdsListBean {
    /**
     * status : success
     * message : ok
     * data : [{"adId":"2","title":"Testing ads","fee":"100","is_renew":"1","description":"This is testing ads","club_id":"4","user_id":"5","user_role":"Manager","crd":"2018-08-19 09:36:29","image":"","club_name":"Friend Zone","full_name":"Chiru Ganguly","isFav":"0","currentDatetime":"2018-08-22 10:08:54","is_my_ads":"1","is_New":"0"},{"adId":"1","title":"My First Ad","fee":"25.8","is_renew":"1","description":"Hello friends ........","club_id":"4","user_id":"5","user_role":"Manager","crd":"2018-08-21 08:44:54","image":"http://clubz.co/dev/uploads/ad_image/6e3f49e0e8d68a491759184c6206cbeb.jpg","club_name":"Friend Zone","full_name":"Chiru Ganguly","isFav":"0","currentDatetime":"2018-08-22 10:08:54","is_my_ads":"1","is_New":"1"}]
     */

     var status: String? = null
     var message: String? = null
     var data: List<DataBean>? = null


    class DataBean() :Parcelable{
        /**
         * adId : 2
         * title : Testing ads
         * fee : 100
         * is_renew : 1
         * description : This is testing ads
         * club_id : 4
         * user_id : 5
         * creator_phone : 8959547514
         * user_role : Manager
         * crd : 2018-08-19 09:36:29
         * image :
         * club_name : Friend Zone
         * full_name : Chiru Ganguly
         * isFav : 0
         * currentDatetime : 2018-08-22 10:08:54
         * is_my_ads : 1
         * is_New : 0
         */

        var adId: String? = null
        var title: String? = null
        var fee: String? = null
        var is_renew: String? = null
        var description: String? = null
        var club_id: String? = null
        var user_id: String? = null
        var creator_phone: String? = null
        var contact_no_visibility: String? = null
        var user_role: String? = null
        var crd: String? = null
        var image: String? = null
        var profile_image: String? = null
        var club_name: String? = null
        var full_name: String? = null
        var isFav: String? = null
        var currentDatetime: String? = null
        var is_my_ads: String? = null
        var is_New: String? = null
        var expire_ads: String? = null
        var total_likes: String? = null
        var visible = false
        var isgoogleAdd = false

        constructor(parcel: Parcel) : this() {
            adId = parcel.readString()
            title = parcel.readString()
            fee = parcel.readString()
            is_renew = parcel.readString()
            description = parcel.readString()
            club_id = parcel.readString()
            user_id = parcel.readString()
            creator_phone = parcel.readString()
            contact_no_visibility = parcel.readString()
            user_role = parcel.readString()
            crd = parcel.readString()
            image = parcel.readString()
            profile_image = parcel.readString()
            club_name = parcel.readString()
            full_name = parcel.readString()
            isFav = parcel.readString()
            currentDatetime = parcel.readString()
            is_my_ads = parcel.readString()
            is_New = parcel.readString()
            expire_ads = parcel.readString()
            total_likes = parcel.readString()
            visible = parcel.readByte() != 0.toByte()
            isgoogleAdd = parcel.readByte() != 0.toByte()
        }


        fun getDayDifference(): String {
            val isgrater = false
            var returnDay = ""
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            try {

                val startDate = simpleDateFormat.parse(crd)
                val endDate = simpleDateFormat.parse(currentDatetime)

                //milliseconds
                var different = Math.abs(endDate.time - startDate.time)

                println("startDate : $startDate")
                println("endDate : $endDate")
                println("different : $different")

                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = different / daysInMilli
                different = different % daysInMilli

                val elapsedHours = different / hoursInMilli
                different = different % hoursInMilli

                val elapsedMinutes = different / minutesInMilli
                different = different % minutesInMilli

                val elapsedSeconds = different / secondsInMilli

                if (elapsedDays == 0L) {
                    if (elapsedHours == 0L) {
                        if (elapsedMinutes == 0L) {
                            returnDay = /*elapsedSeconds +*/ " Just now"
                        } else {
                            returnDay = elapsedMinutes.toString() + " minutes ago"
                        }
                    } else if (elapsedHours == 1L) {
                        returnDay = elapsedHours.toString() + " hour ago"
                    } else {
                        returnDay = elapsedHours.toString() + " hours ago"
                    }
                } else if (elapsedDays == 1L) {
                    returnDay =/* elapsedDays + */ " Yesterday"
                } else {
                    returnDay = elapsedDays.toString() + " days ago"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return returnDay
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(adId)
            parcel.writeString(title)
            parcel.writeString(fee)
            parcel.writeString(is_renew)
            parcel.writeString(description)
            parcel.writeString(club_id)
            parcel.writeString(user_id)
            parcel.writeString(creator_phone)
            parcel.writeString(contact_no_visibility)
            parcel.writeString(user_role)
            parcel.writeString(crd)
            parcel.writeString(image)
            parcel.writeString(profile_image)
            parcel.writeString(club_name)
            parcel.writeString(full_name)
            parcel.writeString(isFav)
            parcel.writeString(currentDatetime)
            parcel.writeString(is_my_ads)
            parcel.writeString(is_New)
            parcel.writeString(expire_ads)
            parcel.writeString(total_likes)
            parcel.writeByte(if (visible) 1 else 0)
            parcel.writeByte(if (isgoogleAdd) 1 else 0)
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