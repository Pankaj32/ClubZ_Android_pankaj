package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllAds

class AllAdsRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllAds.TABLE + "("
                    + AllAds.KEY_AdID + "   PRIMARY KEY,"
                    + AllAds.KEY_AdTitle + " TEXT,"
                    + AllAds.KEY_AdFee + " TEXT,"
                    + AllAds.KEY_IsRenew + " TEXT,"
                    + AllAds.KEY_Description + " TEXT,"
                    + AllAds.KEY_ClubId + " TEXT,"
                    + AllAds.KEY_UserId + " TEXT,"
                    + AllAds.KEY_CreatorPhone + " TEXT,"
                    + AllAds.KEY_ContactNoVisibility + " TEXT,"
                    + AllAds.KEY_UserRole + " TEXT,"
                    + AllAds.KEY_Crd + " TEXT,"
                    + AllAds.KEY_Image + " TEXT,"
                    + AllAds.KEY_ProfileImage + " TEXT,"
                    + AllAds.KEY_ClubName + " TEXT,"
                    + AllAds.KEY_FullName + " TEXT,"
                    + AllAds.KEY_IsFav + " TEXT,"
                    + AllAds.KEY_currentDatetime + " TEXT,"
                    + AllAds.KEY_IsMyAds + " TEXT,"
                    + AllAds.KEY_IsNew + " TEXT,"
                    + AllAds.KEY_ExpireAds + " TEXT,"
                    + AllAds.KEY_totalLikes + " TEXT )")
        }
    }

    fun insert(ads: AllAds): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllAds.KEY_AdID, ads.adId)
        values.put(AllAds.KEY_AdTitle, ads.title)
        values.put(AllAds.KEY_AdFee, ads.fee)
        values.put(AllAds.KEY_IsRenew, ads.is_renew)
        values.put(AllAds.KEY_Description, ads.description)
        values.put(AllAds.KEY_ClubId, ads.club_id)
        values.put(AllAds.KEY_UserId, ads.user_id)
        values.put(AllAds.KEY_CreatorPhone, ads.creator_phone)
        values.put(AllAds.KEY_ContactNoVisibility, ads.contact_no_visibility)
        values.put(AllAds.KEY_UserRole, ads.user_role)
        values.put(AllAds.KEY_Crd, ads.crd)
        values.put(AllAds.KEY_Image, ads.image)
        values.put(AllAds.KEY_ProfileImage, ads.profile_image)
        values.put(AllAds.KEY_ClubName, ads.club_name)
        values.put(AllAds.KEY_FullName, ads.full_name)
        values.put(AllAds.KEY_IsFav, ads.isFav)
        values.put(AllAds.KEY_currentDatetime, ads.currentDatetime)
        values.put(AllAds.KEY_IsMyAds, ads.is_my_ads)
        values.put(AllAds.KEY_IsNew, ads.is_New)
        values.put(AllAds.KEY_ExpireAds, ads.expire_ads)
        values.put(AllAds.KEY_totalLikes, ads.total_likes)

        // Inserting Row
        courseId = db?.insert(AllAds.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllAds(): ArrayList<AllAds> {
        val adlist: ArrayList<AllAds> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllAds.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val ads = AllAds()
                ads.adId = cursor.getString(cursor.getColumnIndex(AllAds.KEY_AdID))
                ads.title = cursor.getString(cursor.getColumnIndex(AllAds.KEY_AdTitle))
                ads.fee = cursor.getString(cursor.getColumnIndex(AllAds.KEY_AdFee))
                ads.is_renew = cursor.getString(cursor.getColumnIndex(AllAds.KEY_IsRenew))
                ads.description = cursor.getString(cursor.getColumnIndex(AllAds.KEY_Description))
                ads.club_id = cursor.getString(cursor.getColumnIndex(AllAds.KEY_ClubId))
                ads.user_id = cursor.getString(cursor.getColumnIndex(AllAds.KEY_UserId))
                ads.creator_phone = cursor.getString(cursor.getColumnIndex(AllAds.KEY_CreatorPhone))
                ads.contact_no_visibility = cursor.getString(cursor.getColumnIndex(AllAds.KEY_ContactNoVisibility))
                ads.user_role = cursor.getString(cursor.getColumnIndex(AllAds.KEY_UserRole))
                ads.crd = cursor.getString(cursor.getColumnIndex(AllAds.KEY_Crd))
                ads.image = cursor.getString(cursor.getColumnIndex(AllAds.KEY_Image))
                ads.profile_image = cursor.getString(cursor.getColumnIndex(AllAds.KEY_ProfileImage))
                ads.club_name = cursor.getString(cursor.getColumnIndex(AllAds.KEY_ClubName))
                ads.full_name = cursor.getString(cursor.getColumnIndex(AllAds.KEY_FullName))
                ads.isFav = cursor.getString(cursor.getColumnIndex(AllAds.KEY_IsFav))
                ads.currentDatetime = cursor.getString(cursor.getColumnIndex(AllAds.KEY_currentDatetime))
                ads.is_my_ads = cursor.getString(cursor.getColumnIndex(AllAds.KEY_IsMyAds))
                ads.is_New = cursor.getString(cursor.getColumnIndex(AllAds.KEY_IsNew))
                ads.expire_ads = cursor.getString(cursor.getColumnIndex(AllAds.KEY_ExpireAds))
                ads.total_likes = cursor.getString(cursor.getColumnIndex(AllAds.KEY_totalLikes))
                adlist.add(ads)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return adlist
    }

    fun deleteClub(ads: AllAds) {

    }


    fun insertAll(ads: List<AllAds>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllAds.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllAds.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}