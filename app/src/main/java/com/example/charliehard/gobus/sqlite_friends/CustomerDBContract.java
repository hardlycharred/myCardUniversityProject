package com.example.charliehard.gobus.sqlite_friends;

import android.provider.BaseColumns;

/**
 * Created by Charlie Hard on 1/05/2017.
 */

public final class CustomerDBContract {

    public CustomerDBContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer_details";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_CARD_NUMBER = "card_number";
        public static final String COLUMN_NAME_PASSWORD = "password";


    }

}
