package com.example.charliehard.gobus.gui;

import android.app.AlertDialog;
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
import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.dao.GoBusDAO;
import com.example.charliehard.gobus.domain.Card;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Transaction;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBContract;
import com.example.charliehard.gobus.sqlite_friends.CustomerDBHelper;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase db;
    CustomerDBHelper customerDBHelper;
    Boolean validationErrors;
    ArrayList<Transaction> allTransactions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailEntry = (EditText) findViewById(R.id.emailEntry);
        final EditText passwordEntry = (EditText) findViewById(R.id.passwordEntry);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationErrors = false;

                inputNotEmpty(emailEntry);
                inputNotEmpty(passwordEntry);

                if (validationErrors == true) {
                    return;
                } else {
                    Customer customer = new Customer();
                    customer.setEmail(emailEntry.getText().toString());
                    customer.setPassword(passwordEntry.getText().toString());
                    new FetchDBTask().execute(customer);
                }
            }
        });


        final Button signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
            db = customerDBHelper.getReadableDatabase();
//            customerDBHelper.onUpgrade(db, 2, 3); // Clears the DB
            return params[0];
        }

        @Override
        protected void onPostExecute(final Customer curCustomer) {

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
            String selection = CustomerDBContract.FeedEntry.COLUMN_NAME_EMAIL + " = ? AND " + CustomerDBContract.FeedEntry.COLUMN_NAME_PASSWORD + " = ?";
            String[] selectionArgs = {curCustomer.getEmail().toString(), curCustomer.getPassword().toString()};

            Cursor cursor = db.query(
                    CustomerDBContract.FeedEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            // Retrieve column values from the retrieved cursor row(s)
            if (cursor.moveToNext()) {
                curCustomer.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
                curCustomer.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
                curCustomer.setCardNumber(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER)));
            }
            cursor.close();

            if (curCustomer.getCardNumber() != null) {
                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                projection = new String[]{
                        CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER,
                        CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE
                };
                // Filter results - WHERE id is the current customer's ID
                selection = CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER + " = ?";
                selectionArgs = new String[]{curCustomer.getCardNumber()};

                cursor = db.query(
                        CustomerDBContract.FeedEntry.CARD_TABLE_NAME,                     // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );

                final Card card = new Card();
                // Retrieve column values from the retrieved cursor row(s)
                if (cursor.moveToNext()) {
                    card.setCardNumber(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_CARD_NUMBER)));
                    card.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_BALANCE)));
                }
                cursor.close();

                projection = new String[]{
                        CustomerDBContract.FeedEntry.COLUMN_NAME_DATE,
                        CustomerDBContract.FeedEntry.COLUMN_NAME_TIME,
                        CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT,
                        CustomerDBContract.FeedEntry.COLUMN_NAME_TRANS_ID,
                };

                cursor = db.query(
                        CustomerDBContract.FeedEntry.TRANS_TABLE_NAME,                     // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );


                while (cursor.moveToNext()) { //Charlie
                    Transaction t = new Transaction();
                    t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_TRANS_ID)));
                    t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_DATE)));
                    t.setTime(cursor.getString(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_TIME)));
                    t.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(CustomerDBContract.FeedEntry.COLUMN_NAME_AMOUNT)));
                    allTransactions.add(t);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Logged In");
                alertDialog.setMessage("Thanks for logging in, " + curCustomer.getFirstName() + ". Click OK to return to the main screen.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goHomeWithCustomerIntent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                                GoBusDAO goBusDAO = new GoBusDAO();
                                goBusDAO.setCustomer(curCustomer);
                                goBusDAO.setCard(card);
                                goBusDAO.setTransactions(allTransactions);
//                                goHomeWithCustomerIntent.putExtra("curCustomer", curCustomer);
//                                goHomeWithCustomerIntent.putExtra("card", card);
//                                goHomeWithCustomerIntent.putExtra("allTransactions", allTransactions);
                                startActivity(goHomeWithCustomerIntent);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("User not found");
                alertDialog.setMessage("We couldn't find that user. Please try again or register.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }
}
