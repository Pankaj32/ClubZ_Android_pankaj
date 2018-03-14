package com.clubz.helper

/**
 * Created by mindiii on 2/10/18.
 */
class WebService {
    companion object {
        private val base_Url= "http://clubz.co/dev/"

        val Login           = base_Url+"service/login"
        val Chek_Social     = base_Url+"service/checkSocialRegister"
        //http://clubz.co/dev/service/generateOtp
        val Generate_Otp    = base_Url+"service/generateOtp"
        val login_Otp       = base_Url+"service/loginOtp"
        val Registraion     = base_Url+"service/registration"
        //http://clubz.co/dev/service/registration
        val update_user     = base_Url+"service/user/updateUserMeta"
        val clubs_list      = base_Url+"service/club/nearByClubsList"//dev/service/club/nearByClubsList


    }


    //http://clubz.co/dev/service/login
}