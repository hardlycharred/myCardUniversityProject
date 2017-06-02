package com.hardlycharred.charliehard.mycard.sqlite_friends;

import android.provider.BaseColumns;

/**
 * Created by Charlie Hard on 1/05/2017. !!
 */

public final class CustomerDBContract {

    public CustomerDBContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer_details";
        public static final String ROWID = "rowid";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_CARD_NUMBER = "card_number";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String CARD_TABLE_NAME = "card_details";
        public static final String COLUMN_NAME_BALANCE = "balance";
        public static final String TRANS_TABLE_NAME = "transactions";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TRANS_ID = "transaction_id";


    }

}
