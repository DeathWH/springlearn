package com.wang.springlearn.Entity;

public class User {

    public static final String STATE_ACCOUNTEXPIRED = "STATE_ACCOUNTEXPIRED";
    public static final String STATE_LOCK = "STATE_LOCK";
    public static final String STATE_TOKENEXPIRED = "STATE_TOKENEXPIRED";
    public static final String STATE_NORMAL = "STATE_NORMAL";

    private int id;
    private String username;
    private String password;
    private String state;
    private String name;
    private String gender;
    private String birth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
