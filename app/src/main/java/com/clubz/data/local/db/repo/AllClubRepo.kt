package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllClub

class AllClubRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllClub.TABLE + "("
                    + AllClub.KEY_ClubID + "   PRIMARY KEY,"
                    + AllClub.KEY_ClubName + " TEXT,"
                    + AllClub.KEY_ClubSilent + " TEXT,"
                    + AllClub.KEY_ClubDescription + " TEXT,"
                    + AllClub.KEY_ClubIcon + " TEXT,"
                    + AllClub.KEY_ClubImage + " TEXT,"
                    + AllClub.KEY_ClubFoundationDate + " TEXT,"
                    + AllClub.KEY_ClubEmail + " TEXT,"
                    + AllClub.KEY_ClubWebsite + " TEXT,"
                    + AllClub.KEY_ClubContactNo + " TEXT,"
                    + AllClub.KEY_ClubCountryCode + " TEXT,"
                    + AllClub.KEY_ClubCity + " TEXT,"
                    + AllClub.KEY_ClubLocation + " TEXT,"
                    + AllClub.KEY_ClubAddress + " TEXT,"
                    + AllClub.KEY_ClubLatitude + " TEXT,"
                    + AllClub.KEY_ClubLongitude + " TEXT,"
                    + AllClub.KEY_ClubCategoryId + " TEXT,"
                    + AllClub.KEY_TermsConditions + " TEXT,"
                    + AllClub.KEY_ClubCategoryName + " TEXT,"
                    + AllClub.KEY_UserId + " TEXT,"
                    + AllClub.KEY_FullName + " TEXT,"
                    + AllClub.KEY_UserImage + " TEXT,"
                    + AllClub.KEY_ClubUserStatus + " TEXT,"
                    + AllClub.KEY_UserRole + " TEXT,"
                    + AllClub.KEY_ContactNo + " TEXT,"
                    + AllClub.KEY_ContactNoVisibility + " TEXT,"
                    + AllClub.KEY_ProfileImage + " TEXT,"
                    + AllClub.KEY_ClubUserId + " TEXT,"
                    + AllClub.KEY_IsAllowFeeds + " TEXT,"
                    + AllClub.KEY_ClubType + " TEXT,"
                    + AllClub.KEY_CommentCount + " TEXT,"
                    + AllClub.KEY_Status + " TEXT,"
                    + AllClub.KEY_Crd + " TEXT,"
                    + AllClub.KEY_Upd + " TEXT,"
                    + AllClub.KEY_Distance + " TEXT,"
                    + AllClub.KEY_Members + " TEXT )")
        }
    }
    fun insert(club: AllClub): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllClub.KEY_ClubID, club.clubId)
        values.put(AllClub.KEY_ClubName, club.club_name)
        values.put(AllClub.KEY_ClubSilent, club.notSilent)
        values.put(AllClub.KEY_ClubDescription, club.club_description)
        values.put(AllClub.KEY_ClubIcon, club.club_icon)
        values.put(AllClub.KEY_ClubImage, club.club_image)
        values.put(AllClub.KEY_ClubFoundationDate, club.club_foundation_date)
        values.put(AllClub.KEY_ClubEmail, club.club_email)
        values.put(AllClub.KEY_ClubWebsite, club.club_website)
        values.put(AllClub.KEY_ClubContactNo, club.club_contact_no)
        values.put(AllClub.KEY_ClubCountryCode, club.club_country_code)
        values.put(AllClub.KEY_ClubCity, club.club_city)
        values.put(AllClub.KEY_ClubLocation, club.club_location)
        values.put(AllClub.KEY_ClubAddress, club.club_address)
        values.put(AllClub.KEY_ClubLatitude, club.club_latitude)
        values.put(AllClub.KEY_ClubLongitude, club.club_longitude)
        values.put(AllClub.KEY_ClubCategoryId, club.club_category_id)
        values.put(AllClub.KEY_TermsConditions, club.terms_conditions)
        values.put(AllClub.KEY_ClubCategoryName, club.club_category_name)
        values.put(AllClub.KEY_UserId, club.user_id)
        values.put(AllClub.KEY_FullName, club.full_name)
        values.put(AllClub.KEY_UserImage, club.user_image)
        values.put(AllClub.KEY_ClubUserStatus, club.club_user_status)
        values.put(AllClub.KEY_UserRole, club.user_role)
        values.put(AllClub.KEY_ContactNo, club.contact_no)
        values.put(AllClub.KEY_ContactNoVisibility, club.contact_no_visibility)
        values.put(AllClub.KEY_ProfileImage, club.profile_image)
        values.put(AllClub.KEY_ClubUserId, club.clubUserId)
        values.put(AllClub.KEY_IsAllowFeeds, club.is_allow_feeds)
        values.put(AllClub.KEY_ClubType, club.club_type)
        values.put(AllClub.KEY_CommentCount, club.comment_count)
        values.put(AllClub.KEY_Status, club.status)
        values.put(AllClub.KEY_Crd, club.crd)
        values.put(AllClub.KEY_Upd, club.upd)
        values.put(AllClub.KEY_Distance, club.distance)
        values.put(AllClub.KEY_Members, club.members)

        // Inserting Row
        courseId = db?.insert(AllClub.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllClubs(): ArrayList<AllClub> {
        val clubList: ArrayList<AllClub> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllClub.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val club = AllClub()
                club.clubId = cursor.getInt(cursor.getColumnIndex(AllClub.KEY_ClubID))
                club.club_name = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubName))
                club.notSilent = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubSilent))

                club.club_description = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubDescription))
                club.club_icon = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubIcon))
                club.club_image = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubSilent))
                club.club_foundation_date = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubFoundationDate))
                club.club_email = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubEmail))
                club.club_website = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubWebsite))
                club.club_contact_no = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubContactNo))
                club.club_country_code = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubCountryCode))
                club.club_city = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubCity))
                club.club_location = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubLocation))
                club.club_address = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubAddress))
                club.club_latitude = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubLatitude))
                club.club_longitude = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubLongitude))
                club.club_category_id = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubCategoryId))
                club.terms_conditions = cursor.getString(cursor.getColumnIndex(AllClub.KEY_TermsConditions))
                club.club_category_name = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubCategoryName))
                club.user_id = cursor.getString(cursor.getColumnIndex(AllClub.KEY_UserId))
                club.full_name = cursor.getString(cursor.getColumnIndex(AllClub.KEY_FullName))
                club.user_image = cursor.getString(cursor.getColumnIndex(AllClub.KEY_UserImage))
                club.club_user_status = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubUserStatus))
                club.user_role = cursor.getString(cursor.getColumnIndex(AllClub.KEY_UserRole))
                club.contact_no = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ContactNo))
                club.contact_no_visibility = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ContactNoVisibility))
                club.profile_image = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ProfileImage))
                club.clubUserId = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubUserId))
                club.is_allow_feeds = cursor.getString(cursor.getColumnIndex(AllClub.KEY_IsAllowFeeds))
                club.club_type = cursor.getString(cursor.getColumnIndex(AllClub.KEY_ClubType))
                club.comment_count = cursor.getString(cursor.getColumnIndex(AllClub.KEY_CommentCount))
                club.status = cursor.getString(cursor.getColumnIndex(AllClub.KEY_Status))
                club.crd = cursor.getString(cursor.getColumnIndex(AllClub.KEY_Crd))
                club.upd = cursor.getString(cursor.getColumnIndex(AllClub.KEY_Upd))
                club.distance = cursor.getString(cursor.getColumnIndex(AllClub.KEY_Distance))
                club.members = cursor.getInt(cursor.getColumnIndex(AllClub.KEY_Members))

                clubList.add(club)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return clubList
    }

    fun deleteClub(club: AllClub) {

    }


    fun insertAll(clubs: List<AllClub>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllClub.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllClub.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}