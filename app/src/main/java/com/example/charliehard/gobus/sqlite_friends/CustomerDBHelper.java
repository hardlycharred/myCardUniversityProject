package com.example.charliehard.gobus.sqlite_friends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Charlie Hard on 1/05/2017. Hi!!
 */

public class CustomerDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String SQL_CREATE_CARD_ENTRIES =
            "CREATE TABLE " + CustomerDBContract.FeedEntry.CARD_TABLE_NAME + "(" +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " TEXT PRIMARY KEY," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE + " INTEGER);";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CustomerDBContract.FeedEntry.TABLE_NAME + " (" +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " TEXT," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD + " TEXT, "+
                    "FOREIGN KEY(" + CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER +
                    ") REFERENCES " + CustomerDBContract.FeedEntry.CARD_TABLE_NAME +
                    "(" + CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + "));";

    public static final String SQL_CREATE_TRANS_ENTRIES =
            "CREATE TABLE " + CustomerDBContract.FeedEntry.TRANS_TABLE_NAME + " (" +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_TRANS_ID + " STRING PRIMARY KEY," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " TEXT," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_DATE + " DATE," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_TIME + " TIME," +
                    CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT + " INTEGER," +
                    "FOREIGN KEY(" + CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER +
                    ") REFERENCES " + CustomerDBContract.FeedEntry.CARD_TABLE_NAME +
                    "(" + CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + "));";

    public static final String SQL_CARD_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CustomerDBContract.FeedEntry.CARD_TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CustomerDBContract.FeedEntry.TABLE_NAME;

    public static final String SQL_TRANS_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CustomerDBContract.FeedEntry.TRANS_TABLE_NAME;

    public CustomerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CARD_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_TRANS_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_CARD_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_TRANS_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}