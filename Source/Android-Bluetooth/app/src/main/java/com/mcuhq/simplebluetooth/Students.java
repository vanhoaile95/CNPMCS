package com.mcuhq.simplebluetooth;

/**
 * Created by chuong on 10/9/2017.
 */

public class Students{

    private int id;
    private String mssv;
    private String name;
    private String mac1;
    private String mac2;

    private boolean active;

    public Students(){}

    public Students(int id, String mssv, String name)
    {
        this.id=id;
        this.mssv=mssv;
        this.name=name;
    }

    public Students(String mssv, String name)
    {
        this.mssv=mssv;
        this.name=name;
    }

    public Students(int id, String mssv, String name, String mac1, String mac2)
    {
        this.id = id;
        this.mssv=mssv;
        this.name=name;
        this.mac1 = mac1;
        this.mac2 = mac2;
        this.active=false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac) {
        this.mac1 = mac;
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac) {
        this.mac2 = mac;
    }
}
