package com.clubz.data.local.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clubz.ClubZ;
import com.clubz.data.local.db.repo.ClubNameRepo;
import com.clubz.data.model.ClubName;

public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION =8;
    // Database Name
    private static final String DATABASE_NAME = "ClubZ.db";
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper( ) {
        super(ClubZ.instance.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(ClubNameRepo.Companion.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + ClubName.Companion.getTABLE());
        onCreate(db);
    }
}
