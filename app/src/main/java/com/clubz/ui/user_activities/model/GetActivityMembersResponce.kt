package com.clubz.ui.user_activities.model

import com.clubz.ui.user_activities.expandable_recycler_view.ParentListItem

/**
 * Created by chiranjib on 22/6/18.
 */
class GetActivityMembersResponce {
    /**
     * status : success
     * message : found
     * data : [{"full_name":"Pankaj ","userId":"111","profile_image":"http://clubz.co/dev/uploads/profile/0ec65816942ad238a1eae6d0a92d340e.jpg","affiliates":[{"userAffiliateId":"144","affiliate_name":" Nupur"},{"userAffiliateId":"142","affiliate_name":"Anil"},{"userAffiliateId":"143","affiliate_name":" Sunil"},{"userAffiliateId":"145","affiliate_name":" Dharm"}]},{"full_name":"Chiranjib Ganguly","userId":"98","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","affiliates":[{"userAffiliateId":"113","affiliate_name":"Aish"}]}]
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

    class DataBean:ParentListItem {
        override fun getChildItemList(): List<AffiliatesBean>? {
            return affiliates
        }

        override fun isInitiallyExpanded(): Boolean {
           return false
        }

        /**
         * full_name : Pankaj
         * userId : 111
         * profile_image : http://clubz.co/dev/uploads/profile/0ec65816942ad238a1eae6d0a92d340e.jpg
         * affiliates : [{"userAffiliateId":"144","affiliate_name":" Nupur"},{"userAffiliateId":"142","affiliate_name":"Anil"},{"userAffiliateId":"143","affiliate_name":" Sunil"},{"userAffiliateId":"145","affiliate_name":" Dharm"}]
         */

        var full_name: String? = null
        var userId: String? = null
        var profile_image: String? = null
        var tag_name: String? = null
        var is_leader: String? = null
        var is_owner: String? = null
        var affiliates: List<AffiliatesBean>? = null

        class AffiliatesBean {
            /**
             * userAffiliateId : 144
             * affiliate_name :  Nupur
             */

            var userAffiliateId: String? = null
            var affiliate_name: String? = null
            var position: Int? = null
            var size: Int? = null
        }
    }
}