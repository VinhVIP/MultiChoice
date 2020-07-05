package com.vinh.multichoice.online;

public class Subject {
    private int id;
    private String name;
    private String iconPath;
    private int update;

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public Subject() {
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject(int id, String name, String iconPath, int update) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.update = update;
    }

}
