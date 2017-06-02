package com.example.charliehard.mycard.gui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.charliehard.mycard.R;
import com.example.charliehard.mycard.dao.MyCardDAO;
import com.example.charliehard.mycard.domain.Card;
import com.example.charliehard.mycard.domain.Customer;
import com.example.charliehard.mycard.sqlite_friends.CustomerDBContract;
import com.example.charliehard.mycard.sqlite_friends.CustomerDBHelper;

import java.text.DecimalFormat;

public class TopUpActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;
    Boolean validationErrors;
    EditText amountEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        Intent intent = getIntent();
        final Card card = (Card) intent.getSerializableExtra("card");

        TextView curBalance = (TextView) findViewById(R.id.textView6);
        DecimalFormat fmt = new DecimalFormat("####.00");
        String formattedBalance = fmt.format(card.getBalance());
        curBalance.setText("$" + formattedBalance);

        amountEntry = (EditText) findViewById(R.id.editAmount);
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
                inputNotEmpty(amountEntry);
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
            db = customerDBHelper.getWritableDatabase();
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
                    CustomerDBContract.FeedEntry.TABLE_NAME,  // The table to query
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


            AlertDialog alertDialog = new AlertDialog.Builder(TopUpActivity.this).create();
            alertDialog.setTitle("Top Up Successful");
            alertDialog.setMessage("Thanks for topping up! Click OK to return to the main screen.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goHomeWithCustomerIntent = new Intent(TopUpActivity.this, HomeScreenActivity.class);
                                MyCardDAO myCardDAO = new MyCardDAO();
                                Card retCard = myCardDAO.getCard();
                                retCard.setBalance(retCard.getBalance() + Double.parseDouble(amountEntry.getText().toString()));
                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
            alertDialog.show();
        }
    }
}
