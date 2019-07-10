package com.wang.springlearn.Entity;

public class Role {
    private int id;
    //深坑，name要加上ROLE_前缀
    private String name;

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
}
