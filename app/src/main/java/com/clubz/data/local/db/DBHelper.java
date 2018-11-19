package com.clubz.data.local.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clubz.ClubZ;
import com.clubz.data.local.db.repo.AllActivitiesRepo;
import com.clubz.data.local.db.repo.AllAdsRepo;
import com.clubz.data.local.db.repo.AllClubRepo;
import com.clubz.data.local.db.repo.AllEventsRepo;
import com.clubz.data.local.db.repo.AllFabContactRepo;
import com.clubz.data.local.db.repo.AllFeedsRepo;
import com.clubz.data.local.db.repo.ClubNameRepo;
import com.clubz.data.model.AllActivities;
import com.clubz.data.model.AllAds;
import com.clubz.data.model.AllClub;
import com.clubz.data.model.AllEvents;
import com.clubz.data.model.AllFavContact;
import com.clubz.data.model.AllFeeds;
import com.clubz.data.model.ClubName;

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 8;
    // Database Name
    private static final String DATABASE_NAME = "ClubZ.db";
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper() {
        super(ClubZ.instance.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(ClubNameRepo.Companion.createTable());
        db.execSQL(AllClubRepo.Companion.createTable());
        db.execSQL(AllAdsRepo.Companion.createTable());
        db.execSQL(AllFeedsRepo.Companion.createTable());
        db.execSQL(AllActivitiesRepo.Companion.createTable());
        db.execSQL(AllEventsRepo.Companion.createTable());
        db.execSQL(AllFabContactRepo.Companion.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + ClubName.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllClub.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllAds.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllFeeds.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllActivities.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllEvents.Companion.getTABLE());
        db.execSQL("DROP TABLE IF EXISTS " + AllFavContact.Companion.getTABLE());
        onCreate(db);
    }
}
