package com.clubz.data.model

class AllAds {

    companion object {
        val TAG = AllAds::class.java.simpleName
        val TABLE = "AllAds"

        // Labels Table Columns names
        val KEY_AdID = "adId"
        val KEY_AdTitle = "title"
        val KEY_AdFee = "fee"
        val KEY_IsRenew = "isRenew"
        val KEY_Description = "description"
        val KEY_ClubId = "clubId"
        val KEY_UserId = "userId"
        val KEY_CreatorPhone = "creatorPhone"
        val KEY_ContactNoVisibility = "contactNoVisibility"
        val KEY_UserRole = "userRole"
        val KEY_Crd = "crd"
        val KEY_Image = "image"
        val KEY_ProfileImage = "profileImage"
        val KEY_ClubName = "clubName"
        val KEY_FullName = "fullName"
        val KEY_IsFav = "isFav"
        val KEY_currentDatetime = "currentDatetime"
        val KEY_IsMyAds = "isMyAds"
        val KEY_IsNew = "isNew"
        val KEY_ExpireAds = "expireAds"
        val KEY_totalLikes = "totalLikes"

    }
    var adId: String? = null
    var title: String? = null
    var fee: String? = null
    var is_renew: String? = null
    var description: String? = null
    var club_id: String? = null
    var user_id: String? = null
    var creator_phone: String? = null
    var contact_no_visibility: String? = null
    var user_role: String? = null
    var crd: String? = null
    var image: String? = null
    var profile_image: String? = null
    var club_name: String? = null
    var full_name: String? = null
    var isFav: String? = null
    var currentDatetime: String? = null
    var is_my_ads: String? = null
    var is_New: String? = null
    var expire_ads: String? = null
    var total_likes: String? = null
    var visible = false

    override fun toString(): String {
        return title.toString()
    }
}