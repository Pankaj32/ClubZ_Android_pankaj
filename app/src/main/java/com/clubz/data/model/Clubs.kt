package com.clubz.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Dharmraj Acharya on 3/12/18.
 */

class Clubs :Serializable {

    /** clib information
     *  @param clubId
     *  @param club_name
     *  @param club_description
     *  @param club_image
     *  @param club_foundation_date
     *  @param club_country_code
     *  @param club_contact_no
     *  @param club_email
     *  @param club_website
     */
    var clubId = "";
    var club_name = ""
    var club_description = ""
    var club_icon = ""
    var club_image = ""
    var club_foundation_date = ""
    var club_email = ""
    var club_website = ""
    var club_contact_no = ""
    var club_country_code = ""
    var club_city = ""
    var club_location = ""
    var club_address = ""
    var club_latitude = ""
    var club_longitude = ""
    var club_category_id = ""
    var terms_conditions = ""
    var club_category_name = ""

    var user_id = ""
    var full_name = ""
    var user_image = ""
    var club_user_status = ""
    var user_role = ""
    var contact_no = ""
    var contact_no_visibility = ""
    //var isUserLike = ""

    @SerializedName("profile_image")
    var profile_image = ""

    var clubUserId = ""

    /**
     *  @param is_allow_feeds 1 = ON and 0 = OFF notifications
     */
    var is_allow_feeds = ""

   /**
    *  @param club_type
    *  1 = public and 2 = private
    */
    var club_type = ""

    /**
     *  @param comment_count it means total no of message count of this news feed
     *
     */
    var comment_count = ""
    var status = ""
    var crd = ""
    var upd = ""
    var distance = ""
    var members : Int = 0
    var isVisiableBody :Boolean = false
}
