package com.clubz.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by mindiii on 2/8/18.
 */
class User : Serializable{

   @SerializedName("userId")
   public var id              = ""

   public var full_name       = ""
   public var first_name      = ""
   public var last_name       = ""
   public var social_id       = ""
   public var social_type     = ""
   public var email           = ""
   public var country_code    = ""
   public var contact_no      = ""
   public var profile_image   = ""
   public var is_verified     = ""
   public var auth_token      = ""
   public var device_type     = ""
   public var device_token    = ""

}