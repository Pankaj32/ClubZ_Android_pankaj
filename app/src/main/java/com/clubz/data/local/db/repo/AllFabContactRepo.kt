package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllFavContact

class AllFabContactRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllFavContact.TABLE + "("
                    + AllFavContact.KEY_UserId + "   TEXT,"
                    + AllFavContact.KEY_DeviceToken + " TEXT,"
                    + AllFavContact.KEY_Name + " TEXT,"
                    + AllFavContact.KEY_ClubId + " TEXT,"
                    + AllFavContact.KEY_ClubName + " TEXT,"
                    + AllFavContact.KEY_ProfileImage + " TEXT )")
        }
    }
    fun insert(favContact: AllFavContact): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllFavContact.KEY_UserId, favContact.userId)
        values.put(AllFavContact.KEY_Name, favContact.name)
        values.put(AllFavContact.KEY_DeviceToken, favContact.device_token)
        values.put(AllFavContact.KEY_ClubId, favContact.clubId)
        values.put(AllFavContact.KEY_ClubName, favContact.club_name)
        values.put(AllFavContact.KEY_ProfileImage, favContact.profile_image)
        // Inserting Row
        courseId = db?.insert(AllFavContact.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllFavContats(): ArrayList<AllFavContact> {
        val favContactlist: ArrayList<AllFavContact> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllFavContact.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val ads = AllFavContact()
                ads.userId = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_UserId))
                ads.name = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_Name))
                ads.device_token = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_DeviceToken))
                ads.clubId = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_ClubId))
                ads.club_name = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_ClubName))
                ads.profile_image = cursor.getString(cursor.getColumnIndex(AllFavContact.KEY_ProfileImage))
                favContactlist.add(ads)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return favContactlist
    }

    fun deleteClub(ads: AllFavContact) {

    }


    fun insertAll(ads: List<AllFavContact>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllFavContact.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllFavContact.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}