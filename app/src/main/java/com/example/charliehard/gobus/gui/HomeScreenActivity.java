package com.example.charliehard.gobus.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Card;
import com.example.charliehard.gobus.domain.Transaction;

public class HomeScreenActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Customer curCustomer;
    private Card card;
    private Transaction trans1;
    private Transaction trans2;
    private Transaction trans3;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
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
        Intent intent = getIntent();
        final Customer curCustomer = (Customer) intent.getSerializableExtra("curCustomer");
        final Card card = (Card) intent.getSerializableExtra("card");
        Transaction trans1 = (Transaction) intent.getSerializableExtra("trans1");
        Transaction trans2 = (Transaction) intent.getSerializableExtra("trans2");
        Transaction trans3 = (Transaction) intent.getSerializableExtra("trans3");


        mTextMessage = (TextView) findViewById(R.id.message);
        if (curCustomer != null && mTextMessage != null) {
            setContentView(R.layout.activity_home_screen_logged_in);
            mTextMessage = (TextView) findViewById(R.id.textView3);
            mTextMessage.setText("Hello, " + curCustomer.getFirstName() );
            mTextMessage = (TextView) findViewById(R.id.textView5);
            mTextMessage.setText("$" + card.getBalance() + " ");
            mTextMessage = (TextView) findViewById(R.id.transactions);
            mTextMessage.setText(trans1.getDate() + "\t"+ "\t" + "\t"+ "\t"+ "\t" +trans1.getTime() + "\t"+ "\t" + "\t"+ "\t"+ "\t" +trans1.getCardNumber() + "\t"+ "\t"+ "\t"+ "\t"+ "\t" + "$" + trans1.getAmount()+  "\n"
                    + trans2.getDate() + "\t"+ "\t" + "\t"+ "\t"+ "\t"  + trans2.getTime() + "\t"+ "\t" + "\t"+ "\t"+ "\t" + trans2.getCardNumber()+ "\t"+ "\t" + "\t"+ "\t"+ "\t" + "$" + trans2.getAmount() + "\n"
                   +trans3.getDate() + "\t"+ "\t" + "\t"+ "\t"+ "\t"  + trans3.getTime() + "\t"+ "\t" + "\t"+ "\t"+ "\t" + trans3.getCardNumber() +"\t"+ "\t" + "\t"+ "\t"+ "\t"  + "$" +trans3.getAmount());
            Button topup = (Button) findViewById(R.id.button2);
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
