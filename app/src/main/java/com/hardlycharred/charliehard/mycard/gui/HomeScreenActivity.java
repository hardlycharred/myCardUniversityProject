package com.example.charliehard.mycard.gui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.charliehard.mycard.R;
import com.example.charliehard.mycard.dao.MyCardDAO;
import com.example.charliehard.mycard.domain.Card;
import com.example.charliehard.mycard.domain.Customer;
import com.example.charliehard.mycard.domain.Transaction;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Customer curCustomer;
    private Card card;
    private ArrayList<Transaction> allTransactions;

    // Sets actions called in response to navigation item selection
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_map:
                    startActivity(new Intent(HomeScreenActivity.this, MapsActivity.class));
                    return true;
                case R.id.navigation_register:
                    startActivity(new Intent(HomeScreenActivity.this, RegisterActivity.class));
                    return true;
                case R.id.navigation_login:
                    startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        curCustomer = new MyCardDAO().getCustomer();
        card = new MyCardDAO().getCard();
        allTransactions = new MyCardDAO().getTransactions();

        //Will format the balance as we are adding decimals
        DecimalFormat fmt = new DecimalFormat("####.00");

        if (curCustomer != null) {
            setContentView(R.layout.activity_home_screen_logged_in);
            mTextMessage = (TextView) findViewById(R.id.textPersonalHello);
            mTextMessage.setText("Hello, " + curCustomer.getFirstName() );
            mTextMessage = (TextView) findViewById(R.id.textBalance);
            String roundedBal = fmt.format(card.getBalance());
            mTextMessage.setText("$" + roundedBal);
            mTextMessage = (TextView) findViewById(R.id.textTransactions);
            String tabledTransactions = "";
            if (allTransactions != null) {
                for (Transaction t : allTransactions) {
                    tabledTransactions += t.getDate() + "\t" + "\t" + "\t" + "\t" + "\t" + t.getTime() + "\t" + "\t" + "\t" + "\t" + "\t" + "$" + t.getAmount() + "\n";
                }
                mTextMessage.setText(tabledTransactions);
            }
            Button topup = (Button) findViewById(R.id.btnTopup);
            topup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent topUpIntent = new Intent(HomeScreenActivity.this, TopUpActivity.class);
                    topUpIntent.putExtra("card", card);
                    startActivity(topUpIntent);
                }
            });
            //mTextMessage.setText("Transaction1" + trans1.getDate() + " " + trans1.getTime() + " " + trans1.getAmount());
            //mTextMessage.setText("Transaction2" + trans2.getDate() + " " + trans2.getTime() + " " + trans2.getAmount());
            //mTextMessage.setText("Transaction3" + trans3.getDate() + " " + trans3.getTime() + " " + trans3.getAmount());
            //setContentView(R.layout.activity_home_screen_logged_in); uncomment to display alternative home page when logged in
        }

        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.enableAnimation(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
    }

}
