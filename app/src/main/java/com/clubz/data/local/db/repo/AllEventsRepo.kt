package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllEvents

class AllEventsRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllEvents.TABLE + "("
                    //  + AllEvents.KEY_ActivityEventId + "   PRIMARY KEY,"
                    + AllEvents.KEY_ActivityId + " TEXT,"
                    + AllEvents.KEY_ActivityEventId + " TEXT,"
                    + AllEvents.KEY_EventTitle + " TEXT,"
                    + AllEvents.KEY_EventDate + " TEXT,"
                    + AllEvents.KEY_EventTime + " TEXT,"
                    + AllEvents.KEY_Description + " TEXT,"
                    + AllEvents.KEY_Location + " TEXT,"
                    + AllEvents.KEY_Latitude + " TEXT,"
                    + AllEvents.KEY_Longitude + " TEXT,"
                    + AllEvents.KEY_Fee + " TEXT,"
                    + AllEvents.KEY_FeeType + " TEXT,"
                    + AllEvents.KEY_MinUsers + " TEXT,"
                    + AllEvents.KEY_MaxUsers + " TEXT,"
                    + AllEvents.KEY_TotalUsers + " TEXT,"
                    + AllEvents.KEY_JoinedUsers + " TEXT,"
                    + AllEvents.KEY_ConfirmUsers + " TEXT,"
                    + AllEvents.KEY_ConfirmUserlist + " TEXT,"
                    + AllEvents.KEY_IsConfirm + " TEXT,"
                    + AllEvents.KEY_HasJoined + " TEXT,"
                    + AllEvents.KEY_HasAffiliatesJoined + " TEXT,"
                    + AllEvents.KEY_IsCancel + " TEXT,"
                    + AllEvents.KEY_ListType + " TEXT )")
        }
    }

    fun insert(event: AllEvents): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllEvents.KEY_ActivityId, event.activityId)
        values.put(AllEvents.KEY_ActivityEventId, event.activityEventId)
        values.put(AllEvents.KEY_EventTitle, event.event_title)
        values.put(AllEvents.KEY_EventDate, event.event_date)
        values.put(AllEvents.KEY_EventTime, event.event_time)
        values.put(AllEvents.KEY_Description, event.description)
        values.put(AllEvents.KEY_Location, event.location)
        values.put(AllEvents.KEY_Latitude, event.latitude)
        values.put(AllEvents.KEY_Longitude, event.longitude)
        values.put(AllEvents.KEY_Fee, event.fee)
        values.put(AllEvents.KEY_FeeType, event.fee_type)
        values.put(AllEvents.KEY_MinUsers, event.min_users)
        values.put(AllEvents.KEY_MaxUsers, event.max_users)
        values.put(AllEvents.KEY_TotalUsers, event.total_users)
        values.put(AllEvents.KEY_JoinedUsers, event.joined_users)
        values.put(AllEvents.KEY_ConfirmUsers, event.confirm_users)
        values.put(AllEvents.KEY_ConfirmUserlist, event.confirm_userlist)
        values.put(AllEvents.KEY_IsConfirm, event.is_confirm)
        values.put(AllEvents.KEY_HasJoined, event.hasJoined)
        values.put(AllEvents.KEY_HasAffiliatesJoined, event.hasAffiliatesJoined)
        values.put(AllEvents.KEY_IsCancel, event.is_cancel)
        values.put(AllEvents.KEY_ListType, event.list_type)

        // Inserting Row
        courseId = db?.insert(AllEvents.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllEvents(): ArrayList<AllEvents> {
        val eventsList: ArrayList<AllEvents> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllEvents.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val event = AllEvents()
                event.activityId = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_ActivityId))
                event.activityEventId = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_ActivityEventId))
                event.event_title = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_EventTitle))
                event.event_date = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_EventDate))
                event.event_time = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_EventTime))
                event.description = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_Description))
                event.location = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_Location))
                event.latitude = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_Latitude))
                event.longitude = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_Longitude))
                event.fee = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_Fee))
                event.fee_type = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_FeeType))
                event.min_users = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_MinUsers))
                event.max_users = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_MaxUsers))
                event.total_users = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_TotalUsers))
                event.joined_users = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_JoinedUsers))
                event.confirm_users = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_ConfirmUsers))
                event.confirm_userlist = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_ConfirmUserlist))
                event.is_confirm = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_IsConfirm))
                event.hasJoined = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_HasJoined))
                event.hasAffiliatesJoined = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_HasAffiliatesJoined))
                event.is_cancel = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_IsCancel))
                event.list_type = cursor.getString(cursor.getColumnIndex(AllEvents.KEY_ListType))
                eventsList.add(event)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return eventsList
    }

    fun deleteClub(activity: AllEvents) {

    }


    fun insertAll(activity: List<AllEvents>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllEvents.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllEvents.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}