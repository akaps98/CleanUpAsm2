package com.example.assignment2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Site implements Serializable {
    private double latitude, longitude;
    private String name; // site name
    private String owner;
    private List<String> participants; // All joined volunteers
    private Integer collected; // amount of waste collected

    public Site(){}

    public Site(double latitude, double longitude, String name, String owner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.collected = 0;
        this.owner = owner;
        this.participants = new ArrayList<>();
    }
    public Site(double latitude, double longitude, String name, Integer collected) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.collected = collected;
        this.owner = "";
        this.participants = new ArrayList<>();
    }

    public Site(double latitude, double longitude, String name, String owner, ArrayList<String> participants, Integer collected) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.owner = owner;
        this.participants = participants;
        this.collected = collected;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Integer getCollected() {
        return collected;
    }

    public void setCollected(Integer collected) {
        this.collected = collected;
    }
}
