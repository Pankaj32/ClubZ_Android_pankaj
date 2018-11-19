package com.clubz.data.local.db.repo

import android.content.ContentValues
import com.clubz.data.local.db.DatabaseManager
import com.clubz.data.model.AllActivities

class AllActivitiesRepo {

    companion object {

        fun createTable(): String {
            return ("CREATE TABLE " + AllActivities.TABLE + "("
                  //  + AllActivities.KEY_ActivityId + "   PRIMARY KEY,"
                    + AllActivities.KEY_ActivityId + " TEXT,"
                    + AllActivities.KEY_ActivityEventId + " TEXT,"
                    + AllActivities.KEY_EventDate + " TEXT,"
                    + AllActivities.KEY_IsMyActivity + " TEXT,"
                    + AllActivities.KEY_Image + " TEXT,"
                    + AllActivities.KEY_ActivityName + " TEXT,"
                    + AllActivities.KEY_Is_hide + " TEXT,"
                    + AllActivities.KEY_Name + " TEXT,"
                    + AllActivities.KEY_leaderId + " TEXT,"
                    + AllActivities.KEY_ClubId + " TEXT,"
                    + AllActivities.KEY_CreatorId + " TEXT,"
                    + AllActivities.KEY_Location + " TEXT,"
                    + AllActivities.KEY_Latitude + " TEXT,"
                    + AllActivities.KEY_Longitude + " TEXT,"
                    + AllActivities.KEY_FeeType + " TEXT,"
                    + AllActivities.KEY_Fee + " TEXT,"
                    + AllActivities.KEY_MinUsers + " TEXT,"
                    + AllActivities.KEY_MaxUsers + " TEXT,"
                    + AllActivities.KEY_UserRole + " TEXT,"
                    + AllActivities.KEY_Description + " TEXT,"
                    + AllActivities.KEY_IsCancel + " TEXT,"
                    + AllActivities.KEY_TermsConditions + " TEXT,"
                    + AllActivities.KEY_Status + " TEXT,"
                    + AllActivities.KEY_Crd + " TEXT,"
                    + AllActivities.KEY_Upd + " TEXT,"
                    + AllActivities.KEY_ClubName + " TEXT,"
                    + AllActivities.KEY_IsLike + " TEXT,"
                    + AllActivities.KEY_UserId + " TEXT,"
                    + AllActivities.KEY_FullName + " TEXT,"
                    + AllActivities.KEY_DeviceToken + " TEXT,"
                    + AllActivities.KEY_ProfileImage + " TEXT,"
                    + AllActivities.KEY_ListType + " TEXT,"
                    + AllActivities.KEY_CreatorPhone + " TEXT,"
                    + AllActivities.KEY_ContactNoVisibility + " TEXT,"
                    + AllActivities.KEY_LeaderName + " TEXT,"
                    + AllActivities.KEY_LeaderPrflimage + " TEXT,"
                    + AllActivities.KEY_LeaderPhno + " TEXT,"
                    + AllActivities.KEY_LeaderContactNoVisibility + " TEXT,"
                    + AllActivities.KEY_TotalUser + " TEXT )")
        }
    }

    fun insert(activity: AllActivities): Int {
        val courseId: Int
        val db = DatabaseManager.getInstance()?.openDatabase()
        val values = ContentValues()
        values.put(AllActivities.KEY_ActivityId, activity.activityId)
        values.put(AllActivities.KEY_ActivityEventId, activity.activityEventId)
        values.put(AllActivities.KEY_EventDate, activity.event_date)
        values.put(AllActivities.KEY_IsMyActivity, activity.is_my_activity)
        values.put(AllActivities.KEY_Image, activity.image)
        values.put(AllActivities.KEY_ActivityName, activity.activityName)
        values.put(AllActivities.KEY_Is_hide, activity.is_hide)
        values.put(AllActivities.KEY_Name, activity.name)
        values.put(AllActivities.KEY_leaderId, activity.leader_id)
        values.put(AllActivities.KEY_ClubId, activity.clubId)
        values.put(AllActivities.KEY_CreatorId, activity.creator_id)
        values.put(AllActivities.KEY_Location, activity.location)
        values.put(AllActivities.KEY_Latitude, activity.latitude)
        values.put(AllActivities.KEY_Longitude, activity.longitude)
        values.put(AllActivities.KEY_FeeType, activity.fee_type)
        values.put(AllActivities.KEY_Fee, activity.fee)
        values.put(AllActivities.KEY_MinUsers, activity.min_users)
        values.put(AllActivities.KEY_MaxUsers, activity.max_users)
        values.put(AllActivities.KEY_UserRole, activity.user_role)
        values.put(AllActivities.KEY_Description, activity.description)
        values.put(AllActivities.KEY_IsCancel, activity.is_cancel)
        values.put(AllActivities.KEY_TermsConditions, activity.terms_conditions)
        values.put(AllActivities.KEY_Status, activity.status)
        values.put(AllActivities.KEY_Crd, activity.crd)
        values.put(AllActivities.KEY_Upd, activity.activityId)
        values.put(AllActivities.KEY_ClubName, activity.club_name)
        values.put(AllActivities.KEY_IsLike, activity.is_like)
        values.put(AllActivities.KEY_UserId, activity.userId)
        values.put(AllActivities.KEY_FullName, activity.full_name)
        values.put(AllActivities.KEY_DeviceToken, activity.device_token)
        values.put(AllActivities.KEY_ProfileImage, activity.profile_image)
        values.put(AllActivities.KEY_ListType, activity.listType)

        values.put(AllActivities.KEY_CreatorPhone, activity.creator_phone)
        values.put(AllActivities.KEY_ContactNoVisibility, activity.contact_no_visibility)
        values.put(AllActivities.KEY_LeaderName, activity.leader_name)
        values.put(AllActivities.KEY_LeaderPrflimage, activity.leader_prflimage)
        values.put(AllActivities.KEY_LeaderPhno, activity.leader_phno)
        values.put(AllActivities.KEY_LeaderContactNoVisibility, activity.leader_contact_no_visibility)
        values.put(AllActivities.KEY_TotalUser, activity.totalUser)

        // Inserting Row
        courseId = db?.insert(AllActivities.TABLE, null, values)!!.toInt()
        DatabaseManager.getInstance().closeDatabase()
        return courseId
    }

    fun getAllActivities(): ArrayList<AllActivities> {
        val activitylist: ArrayList<AllActivities> = arrayListOf()
        val db = DatabaseManager.getInstance().openDatabase()
        val selectQuery = (" SELECT * FROM " + AllActivities.TABLE)
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                val activity = AllActivities()
                activity.activityId=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ActivityId))
                activity.activityEventId=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ActivityEventId))
                activity.event_date=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_EventDate))
                activity.is_my_activity=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_IsMyActivity))
                activity.image=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Image))
                activity.activityName=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ActivityName))
                activity.is_hide=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Is_hide))
                activity.name=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Name))
                activity.leader_id=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_leaderId))
                activity.club_id=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ClubId))
                activity.creator_id=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_CreatorId))
                activity.location=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Location))
                activity.latitude=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Latitude))
                activity.longitude=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Longitude))
                activity.fee_type=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_FeeType))
                activity.fee=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Fee))
                activity.min_users=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_MinUsers))
                activity.max_users=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_MaxUsers))
                activity.user_role=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_UserRole))
                activity. description=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Description))
                activity.terms_conditions=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_TermsConditions))
                activity.is_cancel=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_IsCancel))
                activity.status=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Status))
                activity.crd=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Crd))
                activity.upd=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_Upd))
                activity.club_name=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ClubName))
                activity.clubId=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ClubId))
                activity.is_like=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_IsLike))
                activity.userId=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_UserId))
                activity.full_name=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_FullName))
                activity.device_token=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_DeviceToken))
                activity.profile_image=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ProfileImage))
                activity.listType=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ListType))

                activity.creator_phone=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_CreatorPhone))
                activity.contact_no_visibility=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_ContactNoVisibility))
                activity.leader_name=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_LeaderName))
                activity.leader_prflimage=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_LeaderPrflimage))
                activity.leader_phno=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_LeaderPhno))
                activity.leader_contact_no_visibility=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_LeaderContactNoVisibility))
                activity.totalUser=cursor.getString(cursor.getColumnIndex(AllActivities.KEY_TotalUser))
                activitylist.add(activity)
            } while (cursor.moveToNext())
        }

        cursor.close()

        DatabaseManager.getInstance().closeDatabase()
        return activitylist
    }

    fun deleteClub(activity: AllActivities) {

    }


    fun insertAll(activity: List<AllActivities>) {

    }

    fun deleteTable() {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        db.delete(AllActivities.TABLE, null, null)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }

    fun updateRow(isAllow: String = "0") {
        val db = DatabaseManager.getInstance().openDatabase()

        // delete clubTable of database
        //  db.update(AllActivities.TABLE,isAllow,)

        //reCreate Table
        //db.execSQL(ClubNameRepo.createTable())

        // Close database connection
        DatabaseManager.getInstance().closeDatabase()
    }
}