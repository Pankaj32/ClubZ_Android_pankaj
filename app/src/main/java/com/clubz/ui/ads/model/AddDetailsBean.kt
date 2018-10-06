package com.clubz.ui.ads.model

import java.text.ParseException
import java.text.SimpleDateFormat

class AddDetailsBean {
    /**
     * status : success
     * message : ok
     * data : {"adId":"16","title":"Fkfjfkfkf","fee":"0","is_renew":"1","description":"","user_id":"5","user_role":"Advertiser","image":"http://clubz.co/dev/uploads/ad_image/7a0f994853f94caaed4ebe1e47eb0145.jpg","creator_name":"Chiru Ganguly","creator_profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png","club_name":"Test Club","clubId":"12","created":"2018-08-28 14:49:45","total_likes":"0"}
     */

     var status: String? = null
     var message: String? = null
     var data: DataBean? = null
     var dateTime: String? = null

    class DataBean {
        /**
         * adId : 16
         * title : Fkfjfkfkf
         * fee : 0
         * is_renew : 1
         * description :
         * user_id : 5
         * creator_phone : 8959547514
         * user_role : Advertiser
         * image : http://clubz.co/dev/uploads/ad_image/7a0f994853f94caaed4ebe1e47eb0145.jpg
         * creator_name : Chiru Ganguly
         * creator_profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         * club_name : Test Club
         * clubId : 12
         * created : 2018-08-28 14:49:45
         * total_likes : 0
         */

        var adId: String? = null
        var title: String? = null
        var fee: String? = null
        var is_renew: String? = null
        var description: String? = null
        var user_id: String? = null
        var creator_phone: String? = null
        var user_role: String? = null
        var image: String? = null
        var creator_name: String? = null
        var creator_profile_image: String? = null
        var club_name: String? = null
        var clubId: String? = null
        var created: String? = null
        var total_likes: String? = null

    }
}