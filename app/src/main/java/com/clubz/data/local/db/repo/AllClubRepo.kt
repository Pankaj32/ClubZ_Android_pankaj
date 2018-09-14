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
                    + AllClub.KEY_ClubSilent + " TEXT )")
        }
    }

    fun insert(club: AllClub): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllClub.KEY_ClubID, club.clubId)
        values.put(AllClub.KEY_ClubName, club.club_name)
        values.put(AllClub.KEY_ClubSilent, club.notSilent)

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

    fun updateRow(isAllow: String="0"){
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
      //  db.update(AllClub.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}