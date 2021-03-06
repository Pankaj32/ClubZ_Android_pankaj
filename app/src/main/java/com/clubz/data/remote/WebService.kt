package com.clubz.data.remote

/**
 * Created by mindiii on 2/10/18.
 */
class WebService {
    companion object {
        private const val   base_Url= "http://clubz.co/dev/"

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
        val club_join       = base_Url +"service/club/joinClub";  //will get
        val club_leave      = base_Url +"service/club/leaveClub";  //leave club get
        val club_applicant_list = base_Url +"service/club/getClubApplicants"
        val club_member_list = base_Url +"service/club/getClubMembers" // ?clubId=66&offset=0&limit=10
        val club_my_clubs = base_Url +"service/club/myClubs"
        val club_updateMemberStatus = base_Url +"service/club/updateClubMemberStatus"
        val club_silence = base_Url +"service/club/updateAllowFeedStatus"
        val club_member_action = base_Url +"service/club/answerClubRequest"
        val club_add_member_Tag = base_Url +"service/club/addUserTag"
        val club_delete_member_Tag = base_Url +"service/club/deleteUserTags"
        val club_search_clubs = base_Url +"service/club/searchClub"
        val update_nickName = base_Url +"service/club/updateNickName"
        val feed_filter_tag = base_Url +"service/club/allNewsFilterTags"

        val nearclub_names  = base_Url +"service/club/nearByClubsName" // Potential search will GEt

        /*feed related api*/
        val feed_getNewsFeedLsit = base_Url + "service/club/getNewsFeedsList"
        val create_feed          = base_Url +"service/club/createNewsFeed" // Potential search will GEt
        val feed_like            = base_Url +"service/club/newsFeedsLike"
        val feed_details            = base_Url +"service/club/newsFeedDetail"
        val feed_comment_count            = base_Url +"service/club/commentCount"


        val get_leaders= base_Url+"service/activity/activityLeaderList?clubId="
        val get_my_club= base_Url+"service/club/myCreatedClubsName"
        val create_activity= base_Url+"service/activity/createActivity"
        val updateActivity= base_Url+"service/activity/updateActivity"
        val get_my_activity_list= base_Url+"service/activity/myActivityList"
        val get_all_activity_list= base_Url+"service/activity/activityListNew"
        val get_activity_list= base_Url+"service/activity/activityList"
        val deleteMyActivity= base_Url+"service/activity/deleteActivity"
        val hideMyActivity= base_Url+"service/activity/hideActivity"

        val create_newsFeed      = base_Url +"service/club/createNewsFeed"
        val update_newsFeed      = base_Url +"service/club/updateNewsFeed"
        val delete_newsFeed      = base_Url +"service/club/deleteNewsFeed"
        val get_profile          = base_Url +"service/user/userProfile"
        val update_profile          = base_Url +"service/user/updateProfile"

        val addEvents            = base_Url +"service/activity/addActivityEvent"
        val joinActivity         = base_Url +"service/activity/joinActivity"
        val cancelActivityDate         = base_Url +"service/activity/cancelActivityDate"
        val confirmActivity      = base_Url +"service/activity/confirmActivity"
        val confirmMyActivity      = base_Url +"service/activity/confirmMyActivity"
        val getuserJoinAffiliates= base_Url +"service/activity/userJoinAffiliates"
        val getuserConfirmAffiliates= base_Url +"service/activity/userConfirmAffiliates"
        val getActivityDetails= base_Url +"service/activity/activityDetail"
        val getActivitymembers= base_Url +"service/activity/activityMembersList"
        val createAd= base_Url +"service/ads/createAd"
        val updateAd= base_Url +"service/ads/editAds"
        val getAdsList= base_Url +"service/ads/adsListNew"
        val getAdsDetails= base_Url +"service/ads/adsDetail"
        val adsFab= base_Url +"service/ads/favoriteAd"
        val adsDelete= base_Url +"service/ads/deleteAds"
        val renewAds= base_Url +"service/ads/renewAds"
        val updateContact= base_Url +"service/user/updateContact"
        val addFavoriteUser= base_Url +"service/user/addFavoriteUser"
        val contactList= base_Url +"service/user/contactList"
        val favoriteUserList= base_Url +"service/user/favoriteUserList"
        val getMembershipPlanList= base_Url +"service/user/getCurrentPlanDetails"


        val getUserAllowAds= base_Url +"service/ads/checkAdsLimit"
        val manageNotification= base_Url +"service/user/notificationManage"
        val updateLocation= base_Url +"service/user/updateMyAddress"

    }

    //http://clubz.co/dev/service/login
}