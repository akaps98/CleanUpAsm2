package com.example.assignment2.model;

public class User {
    private Integer type; // 0 = admin, 1 = user
    private String username, password;
    private Site site; // if the user is owner

    public User(){}

    public User(Integer type, String username, String password, Site site) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.site = site;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
