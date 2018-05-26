package com.clubz.ui.user_activities.model

/**
 * Created by chiranjib on 25/5/18.
 */
class GetMyClubResponce {

    /**
     * status : success
     * message : found
     * data : [{"clubId":"72","club_name":"Lions Club"}]
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

    class DataBean {
        /**
         * clubId : 72
         * club_name : Lions Club
         */

        var clubId: String? = null
        var club_name: String? = null
        override fun toString(): String {
            return club_name!!
        }
    }
}