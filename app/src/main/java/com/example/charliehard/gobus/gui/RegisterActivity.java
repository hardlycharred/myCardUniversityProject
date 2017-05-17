package com.example.charliehard.gobus.gui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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
import com.example.charliehard.gobus.domain.Card;
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
        final EditText passwordEntry = (EditText) findViewById(R.id.passwordEntry);
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
                customer.setPassword(passwordEntry.getText().toString());
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
            customerDBHelper.onUpgrade(db, 3, 4); // Clears the DB
            return params[0];
        }

        @Override
        protected void onPostExecute(final Customer curCustomer) {

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE, 2);

// Insert the new row, returning the primary key value of the new row
            Boolean error = false;

            try {
                db.insertOrThrow(CustomerDBContract.FeedEntry.CARD_TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                error = true;
                // Sourced from http://stackoverflow.com/a/26097696
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Card number already is associated with an account");
                alertDialog.setMessage("A user with that card number already exists. Please try another card number.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

            // Create a new map of values, where column names are the keys
            ContentValues values2 = new ContentValues();
            values2.put(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME, curCustomer.getFirstName());
            values2.put(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME, curCustomer.getLastName());
            values2.put(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL, curCustomer.getEmail());
            values2.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values2.put(CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD, curCustomer.getPassword());


// Insert the new row, returning the primary key value of the new row
            error = false;

            try {
                long newRowId = db.insertOrThrow(CustomerDBContract.FeedEntry.TABLE_NAME, null, values2);
                Log.d("new row id is: ", Long.toString(newRowId));
                curCustomer.setId(newRowId);
            } catch (SQLiteConstraintException e) {
                error = true;
                // Sourced from http://stackoverflow.com/a/26097696
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("User already exists");
                alertDialog.setMessage("A user with that email address already exists. Please try another email address.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }


            if (!error) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("User Created");
                alertDialog.setMessage("Thanks for signing up, " + curCustomer.getFirstName() + ". Click OK to return to the main screen.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Card card = new Card();
                                card.setCardNumber(curCustomer.getCardNumber());
                                card.setBalance(2);

                                Intent goHomeWithCustomerIntent = new Intent(RegisterActivity.this, HomeScreenActivity.class);
                                goHomeWithCustomerIntent.putExtra("curCustomer", curCustomer);
                                goHomeWithCustomerIntent.putExtra("card", card);
                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

//             All of the code below is for selecting data that was just placed in the DB
//             This was useful for testing and may be useful in the future, so it remains here for now.

            SQLiteDatabase db = customerDBHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    CustomerDBContract.FeedEntry.ROWID,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD
            };

            // Filter results - WHERE id is the current customer's ID
            String selection = CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL + " = ?";
            String[] selectionArgs = {curCustomer.getEmail().toString()};

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
            TextView password = (TextView) findViewById(R.id.password);


            TextView id = (TextView) findViewById(R.id.id);


            // Retrieve column values from the retrieved cursor row(s)
            if (cursor.moveToNext()) {
                firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
                lastName.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
                email.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL)));
                cardNum.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER)));
                password.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD)));
                id.setText(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.ROWID)));
            }
            cursor.close();
        }


    }
}
