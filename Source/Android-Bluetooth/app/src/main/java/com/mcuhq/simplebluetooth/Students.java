package com.mcuhq.simplebluetooth;

/**
 * Created by chuon on 10/9/2017.
 */

public class Students{

    private int id;
    private String mssv;
    private String name;
    private String mac;

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

    public Students(String mssv, String name, String mac)
    {
        this.mssv=mssv;
        this.name=name;
        this.mac = mac;
        this.active=true;
    }

    public Students(int id, String mssv, String name, String mac)
    {
        this.id = id;
        this.mssv=mssv;
        this.name=name;
        this.mac = mac;
        this.active=true;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
