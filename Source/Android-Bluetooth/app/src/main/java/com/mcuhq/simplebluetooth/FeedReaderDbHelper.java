package com.mcuhq.simplebluetooth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;

import com.mcuhq.simplebluetooth.FeedReaderContract.FeedEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuong on 10/9/2017.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Students.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_MSSV + " TEXT," +
                    FeedEntry.COLUMN_NAME + " TEXT," +
                    FeedEntry.COLUMN_MAC1 + " TEXT," +
                    FeedEntry.COLUMN_MAC2 + " TEXT," +
                    FeedEntry.COLUMN_LAN1 + " TEXT," +
                    FeedEntry.COLUMN_LAN2 + " TEXT," +
                    FeedEntry.COLUMN_LAN3 + " TEXT," +
                    FeedEntry.COLUMN_LAN4 + " TEXT," +
                    FeedEntry.COLUMN_LAN5 + " TEXT," +
                    FeedEntry.COLUMN_LAN6 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createDefaultStudents()  {
        int count = this.getNotesCount();
        if(count ==0 ) {
            Students cheat = new Students(0, "", "", "", "");
            Students note1 = new Students(1, "13520086", "Nguyễn Đình Chương", "18:CF:5E:A8:47:B4", "Vlxx.tv");
            Students note2 = new Students(2, "13520422", "Phan Thanh Lam", "58:00:E3:B8:7D:0A","");
            Students note3 = new Students(3, "13520290", "Lê Văn Hoài", "123456","");
            this.addStudent(cheat);
            this.addStudent(note1);
            this.addStudent(note2);
            this.addStudent(note3);
        }
    }

    public void addStudent(Students std)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ID, std.getId());
        values.put(FeedEntry.COLUMN_MSSV, std.getMssv());
        values.put(FeedEntry.COLUMN_NAME, std.getName());
        values.put(FeedEntry.COLUMN_MAC1, std.getMac1());
        values.put(FeedEntry.COLUMN_MAC2, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN1, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN2, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN3, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN4, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN5, std.getMac2());
        values.put(FeedEntry.COLUMN_LAN6, std.getMac2());
        db.insert(FeedEntry.TABLE_NAME, null, values);

        db.close();
    }

    public Students getStudent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FeedEntry.TABLE_NAME, new String[] {FeedEntry.COLUMN_ID ,FeedEntry.COLUMN_MSSV,
                        FeedEntry.COLUMN_NAME }, FeedEntry.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Students note = new Students(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return note
        return note;
    }


    public List<Students> getAllNotes() {

        List<Students> noteList = new ArrayList<Students>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Students note = new Students();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setMssv(cursor.getString(1));
                note.setName(cursor.getString(2));
                note.setMac1(cursor.getString(3));
                note.setMac2(cursor.getString(4));
                note.setLan1(cursor.getString(5));
                note.setLan2(cursor.getString(6));
                note.setLan3(cursor.getString(7));
                note.setLan4(cursor.getString(8));
                note.setLan5(cursor.getString(9));
                note.setLan6(cursor.getString(10));

                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public int getNotesCount() {

        String countQuery = "SELECT  * FROM " + FeedEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }


    public void updateStudent(Students note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_MSSV, note.getMssv());
        values.put(FeedEntry.COLUMN_NAME, note.getName());
        values.put(FeedEntry.COLUMN_MAC1, note.getMac1());
        values.put(FeedEntry.COLUMN_MAC2, note.getMac2());

        db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(note.getId())});

        db.close();
    }

    public void updateDiemDanh(int id, int lan, String value) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        switch(lan)
        {
            case 1:
                values.put(FeedEntry.COLUMN_LAN1, value);
                break;
            case 2:
                values.put(FeedEntry.COLUMN_LAN2, value);
                break;
            case 3:
                values.put(FeedEntry.COLUMN_LAN3, value);
                break;
            case 4:
                values.put(FeedEntry.COLUMN_LAN4, value);
                break;
            case 5:
                values.put(FeedEntry.COLUMN_LAN5, value);
                break;
            case 6:
                values.put(FeedEntry.COLUMN_LAN6, value);
                break;
        }

        db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    public void deleteStudent(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public Cursor getuser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + FeedEntry.TABLE_NAME + " ",
                null);
        return res;
    }
}
