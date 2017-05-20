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

import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.domain.Card;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Transaction;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBContract;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBHelper;

public class TopUpActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;
    Boolean validationErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        Intent intent = getIntent();
        final Customer curCustomer = (Customer) intent.getSerializableExtra("curCustomer");
        final Card card = (Card) intent.getSerializableExtra("card");

        final EditText amountEntry = (EditText) findViewById(R.id.editAmount);
        final EditText cardEntry = (EditText) findViewById(R.id.editText3);
        final EditText cardNameEntry = (EditText) findViewById(R.id.editText4);
        final EditText expiryEntry = (EditText) findViewById(R.id.editText5);
        final EditText ccvEntry = (EditText) findViewById(R.id.editText7);
        final Button submit = (Button) findViewById(R.id.submit);
        amountEntry.requestFocus();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationErrors = false;

                inputNotEmpty(cardEntry);
                inputNotEmpty(cardNameEntry);
                inputNotEmpty(expiryEntry);
                inputNotEmpty(ccvEntry);


                if (validationErrors == true) {
                    return;
                } else {
                    card.setBalance((card.getBalance() + Double.parseDouble(amountEntry.getText().toString())));
                    new FetchDBTask().execute(card);
                }
            }
        });
    }

    public void inputNotEmpty(EditText inputView) {
        if (inputView.getText().toString().length() == 0) {
            inputView.setError(inputView.getHint() + " can't be empty");
            validationErrors = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    class FetchDBTask extends AsyncTask<Card, Void, Card> {

        @Override
        protected Card doInBackground(Card... params) {
            customerDBHelper = new CustomerDBHelper(getApplicationContext());
            db = customerDBHelper.getReadableDatabase();
//            customerDBHelper.onUpgrade(db, 2, 3); // Clears the DB
            return params[0];
        }

        @Override
        protected void onPostExecute(final Card card) {

            // New value for one column
            ContentValues values = new ContentValues();
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE, card.getBalance());

            // Which row to update, based on the title
            String selection = CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " LIKE ?";
            String[] selectionArgs = {card.getCardNumber()};

            int count = db.update(
                    CustomerDBContract.FeedEntry.CARD_TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            String[] projection = {
                    CustomerDBContract.FeedEntry.ROWID,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER,
                    CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD
            };

            // Filter results - WHERE id is the current customer's ID
            selection = CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " = ?";
            selectionArgs = new String[]{card.getCardNumber()};

            Cursor cursor = db.query(
                    CustomerDBContract.FeedEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            final Customer curCustomer = new Customer();
            // Retrieve column values from the retrieved cursor row(s)
            if (cursor.moveToNext()) {
                curCustomer.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
                curCustomer.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
                curCustomer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL)));
                curCustomer.setCardNumber(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER)));
                curCustomer.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD)));
                curCustomer.setId(cursor.getLong(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.ROWID)));
            }
            cursor.close();
            Log.d("The customer is:", curCustomer.toString());

            AlertDialog alertDialog = new AlertDialog.Builder(TopUpActivity.this).create();
            alertDialog.setTitle("Top Up Successful");
            alertDialog.setMessage("Thanks for topping up! Click OK to return to the main screen.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goHomeWithCustomerIntent = new Intent(TopUpActivity.this, HomeScreenActivity.class);
                                goHomeWithCustomerIntent.putExtra("curCustomer", curCustomer);
                                goHomeWithCustomerIntent.putExtra("card", card);
                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
            alertDialog.show();
        }
    }
}
