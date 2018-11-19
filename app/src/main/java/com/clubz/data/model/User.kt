package com.clubz.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by mindiii on 2/8/18.
 */
class User : Serializable{

   @SerializedName("userId")
   var id              = ""

   var full_name       = ""
   var first_name      = ""
   var last_name       = ""
   var social_id       = ""
   var social_type     = ""
   var email           = ""
   var country_code    = ""
   var contact_no      = ""
   var landline_no     = ""
   var profile_image   = ""
   var is_verified     = ""
   var auth_token      = ""
   var device_type     = ""
   var device_token    = ""

   var about_me= ""
   var about_me_visibility= ""
   var dob= ""
   var dob_visibility= ""
   var contact_no_visibility = ""
   var landline_no_visibility = ""
   var email_visibility = ""
   var affiliates_visibility = ""
   var interest_visibility = ""
   var skills_visibility = ""
   var news_notifications = ""
   var activities_notifications = ""
   var ads_notifications = ""
   var show_profile = ""
   var allow_anyone = ""
   var hasAffiliates = ""

}