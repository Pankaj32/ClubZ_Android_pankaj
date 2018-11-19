package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllFeeds

class AllFeedsRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllFeeds.TABLE + "("
                    + AllFeeds.KEY_NewsFeedId + "   PRIMARY KEY,"
                    + AllFeeds.KEY_NewsFeedTitle + " TEXT,"
                    + AllFeeds.KEY_NewsFeedDescription + " TEXT,"
                    + AllFeeds.KEY_TagName + " TEXT,"
                    + AllFeeds.KEY_Datetime + " TEXT,"
                    + AllFeeds.KEY_ClubName + " TEXT,"
                    + AllFeeds.KEY_ClubId + " TEXT,"
                    + AllFeeds.KEY_UserName + " TEXT,"
                    + AllFeeds.KEY_CreatorPhone + " TEXT,"
                    + AllFeeds.KEY_ContactNoVisibility + " TEXT,"
                    + AllFeeds.KEY_ProfileImage + " TEXT,"
                    + AllFeeds.KEY_UserId + " TEXT,"
                    + AllFeeds.KEY_Likes + " TEXT,"
                    + AllFeeds.KEY_Comments + " TEXT,"
                    + AllFeeds.KEY_IsCommentAllow + " TEXT,"
                    + AllFeeds.KEY_Bookmarks + " TEXT,"
                    + AllFeeds.KEY_IsLiked + " TEXT,"
                    + AllFeeds.KEY_IsBookmarked + " TEXT,"
                    + AllFeeds.KEY_NewsFeedAttachment + " TEXT,"
                    + AllFeeds.KEY_ClubImage + " TEXT,"
                    + AllFeeds.KEY_ClubIcon + " TEXT,"
                    + AllFeeds.KEY_CurrentDateTime + " TEXT,"
                    + AllFeeds.KEY_Crd + " TEXT,"
                    + AllFeeds.KEY_ClubUserId + " TEXT )")
        }
    }

    fun insert(feeds: AllFeeds): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllFeeds.KEY_NewsFeedId, feeds.newsFeedId)
        values.put(AllFeeds.KEY_NewsFeedTitle, feeds.news_feed_title)
        values.put(AllFeeds.KEY_NewsFeedDescription, feeds.news_feed_description)
        values.put(AllFeeds.KEY_TagName, feeds.tagName)
        values.put(AllFeeds.KEY_Datetime, feeds.datetime)
        values.put(AllFeeds.KEY_ClubName, feeds.club_name)
        values.put(AllFeeds.KEY_ClubId, feeds.clubId)
        values.put(AllFeeds.KEY_UserName, feeds.user_name)
        values.put(AllFeeds.KEY_CreatorPhone, feeds.creator_phone)
        values.put(AllFeeds.KEY_ContactNoVisibility, feeds.contact_no_visibility)
        values.put(AllFeeds.KEY_ProfileImage, feeds.profile_image)
        values.put(AllFeeds.KEY_UserId, feeds.user_id)
        values.put(AllFeeds.KEY_Likes, feeds.likes)
        values.put(AllFeeds.KEY_Comments, feeds.comments)
        values.put(AllFeeds.KEY_IsCommentAllow, feeds.is_comment_allow)
        values.put(AllFeeds.KEY_Bookmarks, feeds.bookmarks)
        values.put(AllFeeds.KEY_IsLiked, feeds.isLiked)
        values.put(AllFeeds.KEY_IsBookmarked, feeds.isBookmarked)
        values.put(AllFeeds.KEY_NewsFeedAttachment, feeds.news_feed_attachment)
        values.put(AllFeeds.KEY_ClubImage, feeds.club_image)
        values.put(AllFeeds.KEY_ClubIcon, feeds.club_icon)
        values.put(AllFeeds.KEY_CurrentDateTime, feeds.currentDateTime)
        values.put(AllFeeds.KEY_Crd, feeds.crd)
        values.put(AllFeeds.KEY_ClubUserId, feeds.clubUserId)

        // Inserting Row
        courseId = db?.insert(AllFeeds.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllFeeds(): ArrayList<AllFeeds> {
        val adlist: ArrayList<AllFeeds> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllFeeds.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val feeds = AllFeeds()
                feeds.newsFeedId = cursor.getInt(cursor.getColumnIndex(AllFeeds.KEY_NewsFeedId))
                feeds.news_feed_title = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_NewsFeedTitle))
                feeds.news_feed_description = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_NewsFeedDescription))
                feeds.tagName = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_TagName))
                feeds.datetime = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_Datetime))
                feeds.club_name = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ClubName))
                feeds.clubId = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ClubId))
                feeds.user_name = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_UserName))
                feeds.creator_phone = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_CreatorPhone))
                feeds.contact_no_visibility = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ContactNoVisibility))
                feeds.profile_image = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ProfileImage))
                feeds.user_id = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_UserId))
                feeds.likes = cursor.getInt(cursor.getColumnIndex(AllFeeds.KEY_Likes))
                feeds.comments = cursor.getInt(cursor.getColumnIndex(AllFeeds.KEY_Comments))
                feeds.is_comment_allow = cursor.getInt(cursor.getColumnIndex(AllFeeds.KEY_IsCommentAllow))
                feeds.bookmarks = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_Bookmarks))
                feeds.isLiked = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_IsLiked))
                feeds.isBookmarked = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_IsBookmarked))
                feeds.news_feed_attachment = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_NewsFeedAttachment))
                feeds.club_image = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ClubImage))
                feeds.club_icon = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ClubIcon))
                feeds.currentDateTime = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_CurrentDateTime))
                feeds.crd = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_Crd))
                feeds.clubUserId = cursor.getString(cursor.getColumnIndex(AllFeeds.KEY_ClubUserId))
                adlist.add(feeds)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return adlist
    }

    fun deleteClub(feeds: AllFeeds) {

    }


    fun insertAll(feeds: List<AllFeeds>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllFeeds.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllFeeds.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}