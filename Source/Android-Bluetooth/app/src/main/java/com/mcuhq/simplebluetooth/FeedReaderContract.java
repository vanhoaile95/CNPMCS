package com.mcuhq.simplebluetooth;

import android.provider.BaseColumns;

/**
 * Created by chuon on 10/9/2017.
 */

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "STUDENTS";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_MSSV = "Mssv";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_MAC = "Mac";
    }
}
