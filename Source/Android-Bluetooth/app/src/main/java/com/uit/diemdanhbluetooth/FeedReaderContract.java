package com.uit.diemdanhbluetooth;

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
        //Table student
        public static final String TABLE_STUDENT = "STUDENTS";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_MSSV = "MSSV";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_CLASS = "CLASS";
        public static final String COLUMN_MAC1 = "MAC1";
        public static final String COLUMN_MAC2 = "MAC2";

        //Table diem danh
        public static final String TABLE_DIEM_DANH = "DIEMDANH";
        public static final String COLUMN_ID_DD = "ID";
        public static final String COLUMN_ID_SV_DD = "IDSV";
        public static final String COLUMN_DAY_DD = "NGAY";
        public static final String COLUMN_LAN_DD = "LAN";
        public static final String COLUMN_GHI_CHU = "GHICHU";

        //Table ngay diem danh
        public static final String TABLE_NGAY_DIEM_DANH = "NGAYDIEMDANH";
        public static final String COLUMN_ID_NDD = "ID";
        public static final String COLUMN_DAY = "DAY";
        public static final String COLUMN_LAN = "LAN";
        public static final String COLUMN_DAY_CLASS = "CLASS";
    }

    public static class ClassRoom implements BaseColumns {
        public static final String TABLE_NAME = "CLASSROOM";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_CLASSNAME = "ClassName";
        public static final String COLUMN_CLASSSTATUS = "Status";//trạng thái được điểm danh

    }
}
