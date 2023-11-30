package com.example.assignment2.model;

public class Site {
    private long latitude;
    private long longitude;
    private String name; // site name
    private String desc; // short description of site
    private User[] participants; // All joined volunteers
    private Integer collected; // amount of waste collected

    public Site(){}

    public Site(long latitude, long longitude, String name, String desc, User[] participants, Integer collected) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.desc = desc;
        this.participants = participants;
        this.collected = collected;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
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
