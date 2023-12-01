package com.example.assignment2.model;

public class Site {
    private float latitude, longitude;
    private String name, desc; // site name, short description of site
    private User owner;
    private User[] participants; // All joined volunteers
    private Integer collected; // amount of waste collected

    public Site(){}

    public Site(float latitude, float longitude, String name, String desc, User owner, User[] participants, Integer collected) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.desc = desc;
        this.owner = owner;
        this.participants = participants;
        this.collected = collected;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User[] getParticipants() {
        return participants;
    }

    public void setParticipants(User[] participants) {
        this.participants = participants;
    }

    public Integer getCollected() {
        return collected;
    }

    public void setCollected(Integer collected) {
        this.collected = collected;
    }
}
