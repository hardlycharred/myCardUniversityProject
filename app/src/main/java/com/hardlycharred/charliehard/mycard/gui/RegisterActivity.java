package com.example.charliehard.mycard.gui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.charliehard.mycard.R;
import com.example.charliehard.mycard.dao.MyCardDAO;
import com.example.charliehard.mycard.domain.Card;
import com.example.charliehard.mycard.domain.Customer;
import com.example.charliehard.mycard.domain.Transaction;
import com.example.charliehard.mycard.sqlite_friends.CustomerDBContract;
import com.example.charliehard.mycard.sqlite_friends.CustomerDBHelper;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;
    Boolean validationErrors;

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

                validationErrors = false;

                // Regex sourced from here: http://regexlib.com/REDetails.aspx?regexp_id=88&AspxAutoDetectCookieSupport=1
                if (!emailEntry.getText().toString().matches("^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$")) {
                    emailEntry.setError("Email not valid");
                    validationErrors = true;
                }

                inputNotEmpty(firstNameEntry);
                inputNotEmpty(lastNameEntry);
                inputNotEmpty(emailEntry);
                inputNotEmpty(cardNumEntry);
                inputNotEmpty(passwordEntry);


                if (validationErrors == true) {
                    return;
                } else {
                    Customer customer = new Customer();
                    customer.setFirstName(firstNameEntry.getText().toString());
                    customer.setLastName(lastNameEntry.getText().toString());
                    customer.setEmail(emailEntry.getText().toString());
                    customer.setCardNumber(cardNumEntry.getText().toString());
                    customer.setPassword(passwordEntry.getText().toString());
                    new FetchDBTask().execute(customer);
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

    class FetchDBTask extends AsyncTask<Customer, Void, Customer> {

        @Override
        protected Customer doInBackground(Customer... params) {
            customerDBHelper = new CustomerDBHelper(getApplicationContext());
            db = customerDBHelper.getWritableDatabase();
//            customerDBHelper.onUpgrade(db, 2, 3); // Clears the DB
            return params[0];
        }

        @Override
        protected void onPostExecute(final Customer curCustomer) {
            //Card
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values.put(CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE, 4.32);

// Insert the new row, returning the primary key value of the new row
            Boolean error = false;

            try {
                db.insertOrThrow(CustomerDBContract.FeedEntry.CARD_TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
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
                return;
            }
//Customer
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
                return;
            }
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
            trans2.setTime("07:45");
            trans2.setAmount(1.56);

            // Create a new map of values, where column names are the keys
            ContentValues values5 = new ContentValues();
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER, curCustomer.getCardNumber());
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_DATE, "2017-05-15");
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_TIME, "17:15");
            values5.put(CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT, 2.10);
            long newRowId4 = db.insertOrThrow(CustomerDBContract.FeedEntry.TRANS_TABLE_NAME, null, values5);
            final Transaction trans3 = new Transaction();
            trans3.setId(newRowId2);
            trans3.setCardNumber(curCustomer.getCardNumber());
            trans3.setDate("2017-05-15");
            trans3.setTime("17:15");
            trans3.setAmount(2.10);

            if (!error) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("User Created");
                alertDialog.setMessage("Thanks for signing up, " + curCustomer.getFirstName() + ". Click OK to return to the main screen.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Card card = new Card();
                                card.setCardNumber(curCustomer.getCardNumber());
                                card.setBalance(4.32);
                                Intent goHomeWithCustomerIntent = new Intent(RegisterActivity.this, HomeScreenActivity.class);
//                                goHomeWithCustomerIntent.putExtra("curCustomer", curCustomer);
//                                goHomeWithCustomerIntent.putExtra("card", card);
                                MyCardDAO myCardDAO = new MyCardDAO();
                                myCardDAO.setCustomer(curCustomer);
                                myCardDAO.setCard(card);
                                ArrayList<Transaction> allTransactions = new ArrayList<>();
                                allTransactions.add(trans1);
                                allTransactions.add(trans2);
                                allTransactions.add(trans3);
//                                goHomeWithCustomerIntent.putExtra("allTransactions", allTransactions);
                                myCardDAO.setTransactions(allTransactions);

                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }
}
