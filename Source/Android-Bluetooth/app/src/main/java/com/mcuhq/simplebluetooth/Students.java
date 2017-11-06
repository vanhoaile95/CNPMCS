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
    private String lan1;
    private String lan2;
    private String lan3;
    private String lan4;
    private String lan5;
    private String lan6;

    private boolean active;

    public Students(){}

    public Students(int id, String mssv, String name, String mac1, String mac2)
    {
        this.id = id;
        this.mssv=mssv;
        this.name=name;
        this.mac1 = mac1;
        this.mac2 = mac2;
        this.active=false;
        this.lan1 = "";
        this.lan2 = "";
        this.lan3 = "";
        this.lan4 = "";
        this.lan5 = "";
        this.lan6 = "";
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

    public String getLan1() {
        return lan1;
    }

    public void setLan1(String lan) {
        this.lan1 = lan;
    }

    public String getLan2() {
        return lan2;
    }

    public void setLan2(String lan) {
        this.lan2 = lan;
    }

    public String getLan3() {
        return lan3;
    }

    public void setLan3(String lan) {
        this.lan3 = lan;
    }

    public String getLan4() {
        return lan4;
    }

    public void setLan4(String lan) {
        this.lan4 = lan;
    }

    public String getLan5() {
        return lan5;
    }

    public void setLan5(String lan) {
        this.lan5 = lan;
    }

    public String getLan6() {
        return lan6;
    }

    public void setLan6(String lan) {
        this.lan6 = lan;
    }
}
