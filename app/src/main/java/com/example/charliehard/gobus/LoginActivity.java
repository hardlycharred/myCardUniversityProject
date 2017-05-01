package com.example.charliehard.gobus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new FetchDBTask().execute();
    }

    class FetchDBTask extends AsyncTask<Void, Void, SQLiteDatabase> {

        @Override
        protected SQLiteDatabase doInBackground(Void... params) {
            customerDBHelper = new CustomerDBHelper(getApplicationContext());
            db = customerDBHelper.getWritableDatabase();
            return db;
        }

        @Override
        protected void onPostExecute(SQLiteDatabase sqLiteDatabase) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME, "FirstNameTest");
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME, "LastNameTest");
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL, "EmailTest");
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, "CardNumTest");

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CustomerDBContract.FeedEntry.TABLE_NAME, null, values);
            Log.d("New row num", Long.toString(newRowId));
            TextView firstName = (TextView) findViewById(R.id.firstName);


            SQLiteDatabase db = customerDBHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    CustomerDBContract.FeedEntry._ID,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME
            };

            // Filter results - WHERE first_name = "FirstNameTest"
            String selection = CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME + " = ?";
            String[] selectionArgs = { "FirstNameTest" };

            Cursor cursor = db.query(
                    CustomerDBContract.FeedEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            if(cursor.moveToNext()) {
                firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
            }
            cursor.close();
        }


    }
}
