package com.clubz.data.model

class MembershipPlan {


    /**
     * status : success
     * message : found
     * planDetails : {"membershipPlanId":"1","plan_name":"Basic","plan_price":"0","plan_type":"Free","club_join":"1","club_create":"0","news_read":"1","news_create":"0","activity_join":"1","activity_create":"0","chat_read":"1","chat_create":"1","ads_read":"1","ads_create":"1","status":"0","crd":"2018-11-20 08:25:37"}
     */

    var status: String? = null
    var message: String? = null
    var planDetails: PlanDetailsBean? = null

    class PlanDetailsBean {

        var membershipPlanId: String? = null
        var plan_name: String? = null
        var plan_price: String? = null
        var plan_type: String? = null
        var club_join: String? = null
        var club_create: String? = null
        var news_read: String? = null
        var news_create: String? = null
        var activity_join: String? = null
        var activity_create: String? = null
        var chat_read: String? = null
        var chat_create: String? = null
        var ads_read: String? = null
        var ads_create: String? = null
        var status: String? = null
        var crd: String? = null
    }
}
