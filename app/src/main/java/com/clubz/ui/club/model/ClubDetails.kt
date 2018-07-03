package com.clubz.ui.club.model

/**
 * Created by chiranjib on 3/7/18.
 */
class ClubDetails {
    /**
     * status : success
     * message : Club added successfully
     * clubDetail : {"clubId":"101","user_id":"112","club_name":"Kidzy","club_description":"Nice and healthy environment for kids","club_image":"http://clubz.co/dev/uploads/club_image/15e73c4b9de9557d23d32302e19e2e7f.jpg","club_icon":"http://clubz.co/dev/backend_asset/custom/images/clubDefault.png","club_foundation_date":"2014-05-16","club_email":"kidsplayclub@gmail.com","club_contact_no":"8116174365","club_country_code":"+91","club_website":"www.kidsplay.com","club_location":"MINDIII Systems Pvt. Ltd.","club_address":"MINDIII Systems Pvt. Ltd.","club_latitude":"22.705138200000004","club_longitude":"75.9090618","club_city":"indore","club_type":"1","club_category_id":"25","terms_conditions":"Bellow 15 years are allowed","comment_count":"0","user_role":"Club manager","status":"1","crd":"2018-07-03 09:05:46","upd":"2018-07-03 09:05:46","club_category_name":"Kids","members":"0","full_name":"Dharmraj","profile_image":"http://clubz.co/dev/backend_asset/custom/images/defaultUser.png"}
     */

    private var status: String? = null
    private var message: String? = null
    private var clubDetail: ClubDetailBean? = null

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

    fun getClubDetail(): ClubDetailBean? {
        return clubDetail
    }

    fun setClubDetail(clubDetail: ClubDetailBean) {
        this.clubDetail = clubDetail
    }

    class ClubDetailBean {
        /**
         * clubId : 101
         * user_id : 112
         * club_name : Kidzy
         * club_description : Nice and healthy environment for kids
         * club_image : http://clubz.co/dev/uploads/club_image/15e73c4b9de9557d23d32302e19e2e7f.jpg
         * club_icon : http://clubz.co/dev/backend_asset/custom/images/clubDefault.png
         * club_foundation_date : 2014-05-16
         * club_email : kidsplayclub@gmail.com
         * club_contact_no : 8116174365
         * club_country_code : +91
         * club_website : www.kidsplay.com
         * club_location : MINDIII Systems Pvt. Ltd.
         * club_address : MINDIII Systems Pvt. Ltd.
         * club_latitude : 22.705138200000004
         * club_longitude : 75.9090618
         * club_city : indore
         * club_type : 1
         * club_category_id : 25
         * terms_conditions : Bellow 15 years are allowed
         * comment_count : 0
         * user_role : Club manager
         * status : 1
         * crd : 2018-07-03 09:05:46
         * upd : 2018-07-03 09:05:46
         * club_category_name : Kids
         * members : 0
         * full_name : Dharmraj
         * profile_image : http://clubz.co/dev/backend_asset/custom/images/defaultUser.png
         */

        var clubId: String? = null
        var user_id: String? = null
        var club_name: String? = null
        var club_description: String? = null
        var club_image: String? = null
        var club_icon: String? = null
        var club_foundation_date: String? = null
        var club_email: String? = null
        var club_contact_no: String? = null
        var club_country_code: String? = null
        var club_website: String? = null
        var club_location: String? = null
        var club_address: String? = null
        var club_latitude: String? = null
        var club_longitude: String? = null
        var club_city: String? = null
        var club_type: String? = null
        var club_category_id: String? = null
        var terms_conditions: String? = null
        var comment_count: String? = null
        var user_role: String? = null
        var status: String? = null
        var crd: String? = null
        var upd: String? = null
        var club_category_name: String? = null
        var members: String? = null
        var full_name: String? = null
        var profile_image: String? = null
    }
}