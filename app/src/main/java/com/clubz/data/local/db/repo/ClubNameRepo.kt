package com.clubz.data.local.db.repo
import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.ClubName

class ClubNameRepo{

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + ClubName.TABLE + "("
                    + ClubName.KEY_ClubID + "   PRIMARY KEY,"
                    + ClubName.KEY_ClubName + " TEXT )")
        }
    }

    fun insert(club: ClubName): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(ClubName.KEY_ClubID, club.clubId)
        values.put(ClubName.KEY_ClubName, club.club_name)

        // Inserting Row
        courseId = db?.insert(ClubName.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllClubs() : ArrayList<ClubName> {
        val clubList : ArrayList<ClubName> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + ClubName.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val club = ClubName()
                club.clubId = cursor.getInt(cursor.getColumnIndex(ClubName.KEY_ClubID))
                club.club_name = cursor.getString(cursor.getColumnIndex(ClubName.KEY_ClubName))
                clubList.add(club)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return clubList
    }

    fun deleteClub(club: ClubName){

    }

    fun insertAll(clubs:List<ClubName>){

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(ClubName.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}