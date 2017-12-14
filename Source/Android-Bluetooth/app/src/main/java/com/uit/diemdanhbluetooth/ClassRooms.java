package com.uit.diemdanhbluetooth;

/**
 * Created by lenovo on 21/11/2017.
 */

public class ClassRooms {

    private int id;
    private String name;
    private String status;//trạng thái lớp có đang điểm danh hay không bằng "0" hoặc "1"

    public ClassRooms(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
    public ClassRooms()
    {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
