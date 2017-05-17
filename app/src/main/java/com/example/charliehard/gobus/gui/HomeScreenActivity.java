package com.example.charliehard.gobus.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.charliehard.gobus.R;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Card;

public class HomeScreenActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Customer curCustomer;
    private Card card;

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
        Customer curCustomer = (Customer) intent.getSerializableExtra("curCustomer");
        Card card = (Card) intent.getSerializableExtra("card");

        mTextMessage = (TextView) findViewById(R.id.message);
        if (curCustomer != null && mTextMessage != null) {
            mTextMessage.setText("Hello, " + curCustomer.getFirstName() + " Your balance is $" + card.getBalance());
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
