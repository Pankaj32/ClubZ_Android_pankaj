package com.clubz.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by mindiii on 3/12/18.
 */

class Clubs :Serializable {
    var clubId = "";
    var user_id = ""
    var clubUserId = ""
    var club_name = ""
    var club_description = ""
    var club_image = ""
    var club_foundation_date = ""
    var club_email = ""
    var club_contact_no = ""
    var club_country_code = ""
    var club_website = ""
    var club_city = ""
    var club_location = ""
    var club_address = ""
    var club_latitude = ""
    var club_longitude = ""
    var club_type = ""
    var club_category_id = ""
    var terms_conditions = ""
    var comment_count = ""
    var status = ""
    var crd = ""
    var upd = ""
    var distance = ""
    var club_category_name = ""
    var full_name = ""
    var club_user_status = ""
    var members : Int = 0

    @SerializedName("profile_image")
    var profile_image = ""

    var user_role = ""
    var club_icon = ""

    /*
    * is_allow_feeds 1 = ON and 0 = OFF notifications
    */
    var is_allow_feeds = ""
    var user_image = ""
}
