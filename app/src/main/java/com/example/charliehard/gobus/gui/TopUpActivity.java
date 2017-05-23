package com.example.charliehard.gobus.gui;

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

import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.dao.GoBusDAO;
import com.example.charliehard.gobus.domain.Card;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Transaction;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBContract;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBHelper;

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
        curBalance.setText("$" + card.getBalance().toString());

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

            //3 Transactions

            // Create a new map of values, where column names are the keys
            ContentValues values3 = new ContentValues();
            values3.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values3.put(CustomerDBContract.FeedEntry.COLUMN_NAME_DATE, "2017-05-14");
            values3.put(CustomerDBContract.FeedEntry.COLUMN_NAME_TIME, "12:31");
            values3.put(CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT, 2.24);
            long newRowId2 = db.insertOrThrow(CustomerDBContract.FeedEntry.TRANS_TABLE_NAME, null, values3);
            final Transaction trans1 = new Transaction();
            trans1.setId(newRowId2);
            trans1.setCardNumber(curCustomer.getCardNumber());
            trans1.setDate("2017-05-14");
            trans1.setTime("12:31");
            trans1.setAmount(2.24);

            // Create a new map of values, where column names are the keys
            ContentValues values4 = new ContentValues();
            values4.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values4.put(CustomerDBContract.FeedEntry.COLUMN_NAME_DATE, "2017-05-15");
            values4.put(CustomerDBContract.FeedEntry.COLUMN_NAME_TIME, "07.45");
            values4.put(CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT, 1.56);
            long newRowId3 = db.insertOrThrow(CustomerDBContract.FeedEntry.TRANS_TABLE_NAME, null, values4);
            final Transaction trans2 = new Transaction();
            trans2.setId(newRowId2);
            trans2.setCardNumber(curCustomer.getCardNumber());
            trans2.setDate("2017-05-15");
            trans2.setTime("07.45");
            trans2.setAmount(1.56);

            // Create a new map of values, where column names are the keys
            ContentValues values5 = new ContentValues();
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_DATE, "2017-05-15");
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_TIME, "17.15");
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT, 2.10);
            long newRowId4 = db.insertOrThrow(CustomerDBContract.FeedEntry.TRANS_TABLE_NAME, null, values5);
            final Transaction trans3 = new Transaction();
            trans3.setId(newRowId2);
            trans3.setCardNumber(curCustomer.getCardNumber());
            trans3.setDate("2017-05-15");
            trans3.setTime("17.15");
            trans3.setAmount(2.10);

            AlertDialog alertDialog = new AlertDialog.Builder(TopUpActivity.this).create();
            alertDialog.setTitle("Top Up Successful");
            alertDialog.setMessage("Thanks for topping up! Click OK to return to the main screen.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goHomeWithCustomerIntent = new Intent(TopUpActivity.this, HomeScreenActivity.class);
//                                goHomeWithCustomerIntent.putExtra("curCustomer", curCustomer);
//                                goHomeWithCustomerIntent.putExtra("card", card);
//                                ArrayList<Transaction> allTransactions = new ArrayList<>();
//                                allTransactions.add(trans1);
//                                allTransactions.add(trans2);
//                                allTransactions.add(trans3);
//                                goHomeWithCustomerIntent.putExtra("allTransactions", allTransactions);
                                GoBusDAO goBusDAO = new GoBusDAO();
                                Card retCard = goBusDAO.getCard();
                                retCard.setBalance(retCard.getBalance() + Double.parseDouble(amountEntry.getText().toString()));
                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
            alertDialog.show();
        }
    }
}
