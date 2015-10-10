package com.captor.points.gtnaozuka.entity;

public class DataItem {
    private String title;
    private String date;

    public DataItem(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
