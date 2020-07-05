package com.vinh.multichoice;

public class Type {
    private int select;
    private int id, size;
    private String name;

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public Type(int id, String name) {
        this.id = id;
        this.name = name;

    }

    public Type(String name) {
        this.name = name;
        this.id = Helper.TYPE_ERR;
    }
}
