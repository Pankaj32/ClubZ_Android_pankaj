package com.clubz.data.remote

/**
 * Created by mindiii on 2/10/18.
 */
class WebService {
    companion object {
        val base_Url= "http://clubz.co/dev/"

        val Login           = base_Url +"service/login"
        val Chek_Social     = base_Url +"service/checkSocialRegister"
        //http://clubz.co/dev/service/generateOtp
        val Generate_Otp    = base_Url +"service/generateOtp"
        val login_Otp       = base_Url +"service/loginOtp"
        val Registraion     = base_Url +"service/registration"
        //http://clubz.co/dev/service/registration
        val update_user     = base_Url +"service/user/updateUserMeta"
        val crate_club      = base_Url +"service/club/addClub"//dev/service/club/nearByClubsList
        val club_category   = base_Url +"service/club/getAllClubCategory?limit=10&offset=0";
        val club_search     = base_Url +"service/club/nearByClubsList"; // will Get
        val club_detail     = base_Url +"service/club/clubDetail";  //will get
        val nearclub_names  = base_Url +"service/club/nearByClubsName"; // Potential search will GEt
        val create_feed     = base_Url +"service/club/createNewsFeed"; // Potential search will GEt

    }


    //http://clubz.co/dev/service/login
}