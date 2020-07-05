package com.vinh.multichoice.online;

import android.support.annotation.NonNull;

public class Exam {
    private int id, subject_id;
    private String name;
    private int time, amount;
    private int update;
    private String url;
    private boolean downloaded;

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Exam() {
    }

    public Exam(int id, int subject_id, String name, int time, int amount, String url, int update) {
        this.id = id;
        this.subject_id = subject_id;
        this.name = name;
        this.time = time;
        this.amount = amount;
        this.url = url;
        this.update = update;
    }

    @NonNull
    @Override
    public String toString() {
        return id + " " + subject_id + " " + name + " " + amount + " " + time + " " + url + " " + update;
    }
}
