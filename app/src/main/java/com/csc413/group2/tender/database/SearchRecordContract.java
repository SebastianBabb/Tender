package com.csc413.group2.tender.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * A helper class for creating and accessing the search records database on Tender.
 * It creates one table, search_records, consisting of four fields - _ID, query_term,
 * query_location, and time_stamp.
 * Created by sebastian on 7/23/15.
 */
public class SearchRecordContract {

    // Make the constructor private so the class cant be instantiated. SINGLETON!
    private SearchRecordContract() {}

    // DDL for creating the database table.
    private static final String TEXT_TYPE = " TEXT";    // NOTE THE SPACE PRECEDING TEXT!
    private static final String TIMESTAMP = " DATETIME DEFAULT CURRENT_TIMESTAMP";
    private static final String COMMA_SEP = ",";

    // SQL for creating the table.
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + SearchRecordEntry.TABLE_NAME + " (" +
           SearchRecordEntry._ID + " INTEGER PRIMARY KEY, " +
           SearchRecordEntry.COLUMN_NAME_QUERY_TERM + TEXT_TYPE + COMMA_SEP +
           SearchRecordEntry.COLUMN_NAME_QUERY_LOCATION + TEXT_TYPE + COMMA_SEP +
           SearchRecordEntry.COLUMN_NAME_TIME_STAMP + TIMESTAMP + ")";

    // SQL for delete the table if it exists.
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SearchRecordEntry.TABLE_NAME;

    /*
     * Create an inner class for the table records. All it does its declare the table fields (columns).
     */
    public static abstract class SearchRecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "search_records";
        public static final String COLUMN_NAME_QUERY_TERM = "query_term";
        public static final String COLUMN_NAME_QUERY_LOCATION = "query_location";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";
        public static final String COLUMN_NAME_UPDATED = "";

    } // end inner class

    /*
     * Create an inner class extending SQLiteOpenHelper. This will be instantiated and used by
     * other class to retrieve an instance of a writable/readable database.
     */
    public static class SearchRecordDbHelper extends SQLiteOpenHelper {
        private final String TAG = "SeachRecordDbHelper";

        // If you change the db schema, increment the db version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "SearchRecord.db";

        // Constructor.
        public SearchRecordDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, SQL_CREATE_ENTRIES);
            // Create the database.
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
