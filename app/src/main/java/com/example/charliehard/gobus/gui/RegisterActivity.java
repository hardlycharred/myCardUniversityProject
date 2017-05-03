package com.example.charliehard.gobus.gui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBContract;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBHelper;

public class RegisterActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText firstNameEntry = (EditText) findViewById(R.id.firstNameEntry);
        final EditText lastNameEntry = (EditText) findViewById(R.id.lastNameEntry);
        final EditText emailEntry = (EditText) findViewById(R.id.emailEntry);
        final EditText cardNumEntry = (EditText) findViewById(R.id.cardNumEntry);
        final Button submit = (Button) findViewById(R.id.submitDetails);
        firstNameEntry.requestFocus();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = new Customer();
                customer.setFirstName(firstNameEntry.getText().toString());
                customer.setLastName(lastNameEntry.getText().toString());
                customer.setEmail(emailEntry.getText().toString());
                customer.setCardNumber(cardNumEntry.getText().toString());
                Log.d("The customer is:", customer.toString());
                new FetchDBTask().execute(customer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    class FetchDBTask extends AsyncTask<Customer, Void, Customer> {

        @Override
        protected Customer doInBackground(Customer... params) {
            customerDBHelper = new CustomerDBHelper(getApplicationContext());
            db = customerDBHelper.getWritableDatabase();
            return params[0];
        }

        @Override
        protected void onPostExecute(Customer curCustomer) {

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME, curCustomer.getFirstName());
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME, curCustomer.getLastName());
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL, curCustomer.getEmail());
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CustomerDBContract.FeedEntry.TABLE_NAME, null, values);
            curCustomer.setId(newRowId);

            SQLiteDatabase db = customerDBHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    CustomerDBContract.FeedEntry._ID,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER
            };

            // Filter results - WHERE id is the current customer's ID
            String selection = CustomerDBContract.FeedEntry._ID + " = ?";
            String[] selectionArgs = {curCustomer.getId().toString()};

            Cursor cursor = db.query(
                    CustomerDBContract.FeedEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            TextView firstName = (TextView) findViewById(R.id.firstName);
            TextView lastName = (TextView) findViewById(R.id.lastName);
            TextView email = (TextView) findViewById(R.id.email);
            TextView cardNum = (TextView) findViewById(R.id.cardNum);

            TextView id = (TextView) findViewById(R.id.id);


            // Retrieve column values from the retrieved cursor row(s)
            if(cursor.moveToNext()) {
                firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
                lastName.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
                email.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL)));
                cardNum.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER)));
                id.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry._ID)));
            }
            cursor.close();
        }


    }
}
