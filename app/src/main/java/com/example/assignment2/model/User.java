package com.example.assignment2.model;

public class User {
    private String type;
    private String username;
    private String password;
    private boolean isOwner;
    private Site site; // if the user is owner

    public User(){}

    public User(String type, String username, String password, boolean isOwner, Site site) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.isOwner = isOwner;
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
