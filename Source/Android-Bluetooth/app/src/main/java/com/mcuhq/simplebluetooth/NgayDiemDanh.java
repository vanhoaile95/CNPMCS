package com.mcuhq.simplebluetooth;

/**
 * Created by lenovo on 21/11/2017.
 */

public class NgayDiemDanh {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getLan() {
        return lan;
    }

    public void setLan(int lan) {
        this.lan = lan;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    private int id;
    private String day;
    private int lan;
    private String lop;

    public NgayDiemDanh(int id, String day, int lan, String lop) {
        this.id = id;
        this.day = day;
        this.lan = lan;
        this.lop = lop;
    }
    public NgayDiemDanh()
    {

    }

}
