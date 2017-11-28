package com.mcuhq.simplebluetooth;

/**
 * Created by lenovo on 21/11/2017.
 */

public class DiemDanh {

    private int id;
    private int idSv;
    private String ngay;
    private int lan;
    private String ghichu;

    public int getIdSv() {
        return idSv;
    }

    public void setIdSv(int id) {
        this.idSv = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public int getLan() {
        return lan;
    }

    public void setLan(int lan) {
        this.lan = lan;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public DiemDanh(int id, int idSv, String ngay, int lan, String ghichu) {
        this.id = id;
        this.idSv = idSv;
        this.ngay = ngay;
        this.lan = lan;
        this.ghichu = ghichu;
    }
    public DiemDanh()
    {

    }
}
