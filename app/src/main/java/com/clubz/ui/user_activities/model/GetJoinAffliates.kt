package com.clubz.ui.user_activities.model

/**
 * Created by chiranjib on 14/6/18.
 */
class GetJoinAffliates {
    /**
     * status : success
     * message : ok
     * data : {"full_name":"Chiranjib Ganguly","userId":"98","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","isJoined":"0","affiliates":[{"userAffiliateId":"113","affiliate_name":"Aish","isJoined":"0"},{"userAffiliateId":"114","affiliate_name":" Dharam","isJoined":"0"}]}
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
         * full_name : Chiranjib Ganguly
         * userId : 98
         * profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * isJoined : 0
         * affiliates : [{"userAffiliateId":"113","affiliate_name":"Aish","isJoined":"0"},{"userAffiliateId":"114","affiliate_name":" Dharam","isJoined":"0"}]
         */

        var full_name: String? = null
        var userId: String? = null
        var profile_image: String? = null
        var isJoined: String? = null
        var affiliates: List<AffiliatesBean>? = null

        class AffiliatesBean {
            /**
             * userAffiliateId : 113
             * affiliate_name : Aish
             * isJoined : 0
             */

            var userAffiliateId: String? = null
            var affiliate_name: String? = null
            var isJoined: String? = null
            var isClicked: Boolean = false
        }
    }
}