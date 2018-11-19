package com.clubz.data.model

class AllFeeds {

    companion object {
        val TAG = AllFeeds::class.java.simpleName
        val TABLE = "AllFeeds"

        // Labels Table Columns names
        val KEY_NewsFeedId = "newsFeedId"
        val KEY_NewsFeedTitle = "newsFeedTitle"
        val KEY_NewsFeedDescription = "newsFeedDescription"
        val KEY_TagName = "tagName"
        val KEY_Datetime = "datetime"
        val KEY_ClubName = "clubName"
        val KEY_ClubId = "clubId"
        val KEY_UserName = "userName"
        val KEY_CreatorPhone = "creatorPhone"
        val KEY_ContactNoVisibility = "contactNoVisibility"
        val KEY_ProfileImage = "profileImage"
        val KEY_UserId = "userId"
        val KEY_Likes = "likes"
        val KEY_Comments = "comments"
        val KEY_IsCommentAllow = "isCommentAllow"
        val KEY_Bookmarks = "bookmarks"
        val KEY_IsLiked = "isLiked"
        val KEY_IsBookmarked = "isBookmarked"
        val KEY_NewsFeedAttachment = "newsFeedAttachment"
        val KEY_ClubImage = "clubImage"
        val KEY_ClubIcon = "clubIcon"
        val KEY_CurrentDateTime = "currentDateTime"
        val KEY_Crd = "crd"
        val KEY_ClubUserId = "clubUserId"

    }
    var newsFeedId : Int? = null
    var news_feed_title  = ""
    var news_feed_description  = ""
    var tagName  = ""
    var datetime  = ""
    var club_name  = ""
    var clubId  = ""
    var user_name  = ""
    var creator_phone  = ""
    var contact_no_visibility  = ""
    var profile_image  = ""
    var user_id  = ""
    var likes : Int = 0
    var comments : Int = 0
    var is_comment_allow : Int = 0
    var bookmarks  = ""
    var isLiked  ="0"
    var isBookmarked = ""
    var news_feed_attachment  = ""
    var club_image  = ""
    var club_icon  = ""
    var currentDateTime  = ""
    var crd  = ""
    var clubUserId  = ""

    override fun toString(): String {
        return news_feed_title.toString()
    }
}

