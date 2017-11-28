package com.mcuhq.simplebluetooth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;

import com.mcuhq.simplebluetooth.FeedReaderContract.FeedEntry;
import com.mcuhq.simplebluetooth.FeedReaderContract.ClassRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuong on 10/9/2017.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE = "Database.db";

    private static final String SQL_CREATE_STUDENTS =
            "CREATE TABLE " + FeedEntry.TABLE_STUDENT + " (" +
                    FeedEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_MSSV + " TEXT," +
                    FeedEntry.COLUMN_NAME + " TEXT," +
                    FeedEntry.COLUMN_CLASS + " TEXT," +
                    FeedEntry.COLUMN_MAC1 + " TEXT," +
                    FeedEntry.COLUMN_MAC2 + " TEXT)";

    private static final String SQL_CREATE_DIEMDANH =
            "CREATE TABLE " + FeedEntry.TABLE_DIEM_DANH + " (" +
                    FeedEntry.COLUMN_ID_DD + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_ID_SV_DD + " INTEGER," +
                    FeedEntry.COLUMN_DAY_DD + " TEXT," +
                    FeedEntry.COLUMN_LAN_DD + " INTEGER," +
                    FeedEntry.COLUMN_GHI_CHU + " TEXT)";

    private static final String SQL_CREATE_NGAYDIEMDANH =
            "CREATE TABLE " + FeedEntry.TABLE_NGAY_DIEM_DANH + " (" +
                    FeedEntry.COLUMN_ID_NDD + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_DAY + " TEXT," +
                    FeedEntry.COLUMN_LAN + " INTEGER," +
                    FeedEntry.COLUMN_DAY_CLASS + " TEXT)";

    private static final String SQL_CREATE_CLASSROOM =
            "CREATE TABLE " + ClassRoom.TABLE_NAME + " (" +
                    ClassRoom.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    ClassRoom.COLUMN_CLASSNAME + " TEXT," +
                    ClassRoom.COLUMN_CLASSSTATUS + " TEXT)";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STUDENTS);
        db.execSQL(SQL_CREATE_DIEMDANH);
        db.execSQL(SQL_CREATE_NGAYDIEMDANH);
        db.execSQL(SQL_CREATE_CLASSROOM);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createDefault()  {
        int count = this.getStudentsCount();
        if(count == 0 ) {
            Students note1 = new Students(1, "13520086", "Nguyễn Đình Chương", "CNPMCS" ,"18:CF:5E:A8:47:B4", "");
            Students note2 = new Students(2, "13520422", "Phan Thanh Lam", "CNPMCS");
            Students note3 = new Students(3, "13520290", "Lê Văn Hoài", "CNPMCS");
            Students note4 = new Students(4, "13520000", "Obama", "OOAD");
            ClassRooms class1 = new ClassRooms(1, "CNPMCS", "1");
            ClassRooms class2 = new ClassRooms(2, "OOAD", "0");
            this.addStudent(note1);
            this.addStudent(note2);
            this.addStudent(note3);
            this.addStudent(note4);
            this.addClassRooms(class1);
            this.addClassRooms(class2);
        }
    }

    public void addDiemDanh(DiemDanh dd)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ID_DD, dd.getId());
        values.put(FeedEntry.COLUMN_ID_SV_DD, dd.getIdSv());
        values.put(FeedEntry.COLUMN_DAY_DD, dd.getNgay());
        values.put(FeedEntry.COLUMN_LAN_DD, dd.getLan());
        values.put(FeedEntry.COLUMN_GHI_CHU, dd.getGhichu());

        db.insert(FeedEntry.TABLE_DIEM_DANH, null, values);

        db.close();
    }

    public List<DiemDanh> getAllDiemDanh(int id) {

        List<DiemDanh> noteList = new ArrayList<DiemDanh>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_DIEM_DANH;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                DiemDanh note = new DiemDanh();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setIdSv(Integer.parseInt(cursor.getString(1)));
                note.setNgay(cursor.getString(2));
                note.setLan(Integer.parseInt(cursor.getString(3)));
                note.setGhichu(cursor.getString(4));

                if(note.getIdSv()==id) {
                    // Thêm vào danh sách.
                    noteList.add(note);
                }
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public int getDiemDanhCount() {

        String countQuery = "SELECT  * FROM " + FeedEntry.TABLE_DIEM_DANH;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public void addDay(NgayDiemDanh _day)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ID_NDD, _day.getId());
        values.put(FeedEntry.COLUMN_DAY, _day.getDay());
        values.put(FeedEntry.COLUMN_LAN, _day.getLan());
        values.put(FeedEntry.COLUMN_DAY_CLASS, _day.getLop());

        db.insert(FeedEntry.TABLE_NGAY_DIEM_DANH, null, values);

        db.close();
    }

    public void updateDay(NgayDiemDanh _day) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_LAN, _day.getLan());

        db.update(FeedEntry.TABLE_NGAY_DIEM_DANH, values, FeedEntry.COLUMN_ID_NDD + " = ?",
                new String[]{String.valueOf(_day.getId())});

        db.close();
    }

    public int getLanOfDay(NgayDiemDanh _day) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FeedEntry.TABLE_NGAY_DIEM_DANH, new String[] {FeedEntry.COLUMN_LAN}, FeedEntry.COLUMN_ID_NDD + "=?",
                new String[] { String.valueOf(_day.getId()) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        int lan = Integer.parseInt(cursor.getString(0));

        return lan;
    }

    public int getDayCount() {

        String countQuery = "SELECT  * FROM " + FeedEntry.TABLE_NGAY_DIEM_DANH;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
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
        values.put(FeedEntry.COLUMN_CLASS, std.getClassStd());
        values.put(FeedEntry.COLUMN_MAC1, std.getMac1());
        values.put(FeedEntry.COLUMN_MAC2, std.getMac2());
        db.insert(FeedEntry.TABLE_STUDENT, null, values);

        db.close();
    }

    public void addClassRooms(ClassRooms clr)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ClassRoom.COLUMN_ID, clr.getId());
        values.put(ClassRoom.COLUMN_CLASSNAME, clr.getName());
        values.put(ClassRoom.COLUMN_CLASSSTATUS, clr.getStatus());

        db.insert(ClassRoom.TABLE_NAME, null, values);

        db.close();
    }

    public Students getStudent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FeedEntry.TABLE_STUDENT, new String[] {FeedEntry.COLUMN_ID ,FeedEntry.COLUMN_MSSV,
                        FeedEntry.COLUMN_NAME, FeedEntry.COLUMN_CLASS }, FeedEntry.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Students note = new Students(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return note
        return note;
    }

    public ClassRooms getClassRoom(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ClassRoom.TABLE_NAME, new String[] {ClassRoom.COLUMN_ID ,ClassRoom.COLUMN_CLASSNAME,
                        ClassRoom.COLUMN_CLASSSTATUS }, ClassRoom.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ClassRooms note = new ClassRooms(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return note
        return note;
    }

    public ClassRooms getClassRoomON() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ClassRoom.TABLE_NAME, new String[] {ClassRoom.COLUMN_ID ,ClassRoom.COLUMN_CLASSNAME,
                        ClassRoom.COLUMN_CLASSSTATUS }, ClassRoom.COLUMN_CLASSSTATUS + "=?",
                new String[] { String.valueOf(1) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ClassRooms note = new ClassRooms(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return note
        return note;
    }

    public List<Students> getListStudents(String _class) {

        List<Students> noteList = new ArrayList<Students>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_STUDENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Students note = new Students();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setMssv(cursor.getString(1));
                note.setName(cursor.getString(2));
                note.setClassStd(cursor.getString(3));
                note.setMac1(cursor.getString(4));
                note.setMac2(cursor.getString(5));

                if(note.getClassStd().equals(_class)) {
                    // Thêm vào danh sách.
                    noteList.add(note);
                }
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public List<NgayDiemDanh> getAllDays(String _class) {

        List<NgayDiemDanh> noteList = new ArrayList<NgayDiemDanh>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_NGAY_DIEM_DANH;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                NgayDiemDanh note = new NgayDiemDanh();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setDay(cursor.getString(1));
                note.setLan(Integer.parseInt(cursor.getString(2)));
                note.setLop(cursor.getString(3));

                if(note.getLop().equals(_class))
                    noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public List<Students> getAllStudents() {

        List<Students> noteList = new ArrayList<Students>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_STUDENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Students note = new Students();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setMssv(cursor.getString(1));
                note.setName(cursor.getString(2));
                note.setClassStd(cursor.getString(3));
                note.setMac1(cursor.getString(4));
                note.setMac2(cursor.getString(5));

                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public List<ClassRooms> getAllClassRoom() {

        List<ClassRooms> noteList = new ArrayList<ClassRooms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ClassRoom.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                ClassRooms note = new ClassRooms();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setName(cursor.getString(1));
                note.setStatus(cursor.getString(2));


                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public int getStudentsCount() {

        String countQuery = "SELECT  * FROM " + FeedEntry.TABLE_STUDENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int getNotesClassRoomCount() {

        String countQuery = "SELECT  * FROM " + ClassRoom.TABLE_NAME;
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

        db.update(FeedEntry.TABLE_STUDENT, values, FeedEntry.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(note.getId())});

        db.close();
    }

    public void updateClassRoom(ClassRooms clr) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ClassRoom.COLUMN_CLASSNAME, clr.getName());
        values.put(ClassRoom.COLUMN_CLASSSTATUS, clr.getStatus());



        db.update(ClassRoom.TABLE_NAME, values, ClassRoom.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(clr.getId())});

        db.close();
    }

    public void updateClassRoomON(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ClassRoom.COLUMN_CLASSSTATUS, "0");//update lại các lớp



        db.update(ClassRoom.TABLE_NAME, values, ClassRoom.COLUMN_CLASSSTATUS+ " = ?",
                new String[]{String.valueOf(1)});

        //update

        ContentValues values2 = new ContentValues();

        values2.put(ClassRoom.COLUMN_CLASSSTATUS, "1");//update lại các lớp



        db.update(ClassRoom.TABLE_NAME, values2, ClassRoom.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(id)});



        db.close();
    }

    public void deleteStudent(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FeedEntry.TABLE_STUDENT, FeedEntry.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public void deleteClassroom(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ClassRoom.COLUMN_CLASSSTATUS, "2");//status=2~ xóa, 1~đang điểm danh, 0~binhf thường



        db.update(ClassRoom.TABLE_NAME, values, ClassRoom.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }
}
