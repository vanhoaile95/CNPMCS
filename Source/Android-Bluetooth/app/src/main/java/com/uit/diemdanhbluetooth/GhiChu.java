package com.uit.diemdanhbluetooth;

/**
 * Created by lenovo on 21/11/2017.
 */

public class GhiChu {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    private String noiDung;

    public GhiChu(int id, String noiDung) {
        this.id = id;
        this.noiDung = noiDung;
    }
    public GhiChu()
    {

    }

}
