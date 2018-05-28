package com.clubz.ui.user_activities.model

/**
 * Created by chiranjib on 22/5/18.
 */
class GetLeaderResponce {
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
         * userTagId : 3
         * tag_name : Rj
         */

        var userTagId: String? = null
        var tag_name: String? = null
        override fun toString(): String {
            return tag_name!!
        }
    }
}