package com.mcuhq.simplebluetooth;

import android.provider.BaseColumns;

/**
 * Created by chuong on 10/9/2017.
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
        public static final String COLUMN_MAC1 = "Mac1";
        public static final String COLUMN_MAC2 = "Mac2";
        public static final String COLUMN_LAN1 = "Lan1";
        public static final String COLUMN_LAN2 = "Lan2";
        public static final String COLUMN_LAN3 = "Lan3";
        public static final String COLUMN_LAN4 = "Lan4";
        public static final String COLUMN_LAN5 = "Lan5";
        public static final String COLUMN_LAN6 = "Lan6";
    }
}
