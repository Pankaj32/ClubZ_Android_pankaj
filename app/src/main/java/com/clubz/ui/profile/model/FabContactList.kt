package com.clubz.ui.profile.model

class FabContactList {
    /**
     * status : success
     * message : found
     * userList : [{"userId":"22","device_token":"dmW22MMc5OM:APA91bGemne7UVvi4UQCtKzxoKhV_4VMku4JQoS0FY5wyMgPTdtxk4jjEzVbVmw9t96cIcSaSdpFporLUWnRJ95jk7JjLgae17UsQs5Bn3kMyvh4aUhib1a-_l0DnRc5slESLe00V1JJ","clubId":"23","club_name":"Lions Club","name":"Chiranjib Ganguly","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png"}]
     */

    private var status: String? = null
    private var message: String? = null
    private var userList: List<UserListBean>? = null


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

    fun getUserList(): List<UserListBean>? {
        return userList
    }

    fun setUserList(userList: List<UserListBean>) {
        this.userList = userList
    }


    class UserListBean {
        /**
         * userId : 22
         * device_token : dmW22MMc5OM:APA91bGemne7UVvi4UQCtKzxoKhV_4VMku4JQoS0FY5wyMgPTdtxk4jjEzVbVmw9t96cIcSaSdpFporLUWnRJ95jk7JjLgae17UsQs5Bn3kMyvh4aUhib1a-_l0DnRc5slESLe00V1JJ
         * clubId : 23
         * club_name : Lions Club
         * name : Chiranjib Ganguly
         * profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         */

        var userId: String? = null
        var device_token: String? = null
        var clubId: String? = null
        var club_name: String? = null
        var name: String? = null
        var profile_image: String? = null
    }
}