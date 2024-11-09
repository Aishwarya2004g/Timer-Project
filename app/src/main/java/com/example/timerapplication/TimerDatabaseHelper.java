package com.example.timerapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timers.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TIMERS = "timers";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_END_TIME = "end_time";

    public TimerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TIMERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DURATION + " TEXT, " +
                COLUMN_END_TIME + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMERS);
        onCreate(db);
    }

    public void saveTimerHistory(String duration, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_END_TIME, endTime);
        db.insert(TABLE_TIMERS, null, values);
    }

    public Cursor getAllTimers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TIMERS, null, null, null, null, null, COLUMN_ID + " DESC");
    }
}
