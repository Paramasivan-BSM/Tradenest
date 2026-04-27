package com.bsm.tradenest.model;

import org.bson.types.ObjectId;

public class WorkerModel {

    private ObjectId id;
    private String name;
    private String skill;
    private boolean available;
    private double serviceRadiusKm;
    private double startingRate; // service starting price
    private boolean negotiable; // rate negotiable?
    private int experienceYears;
    private String bio;
    private Location location;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getServiceRadiusKm() {
        return serviceRadiusKm;
    }

    public void setServiceRadiusKm(double serviceRadiusKm) {
        this.serviceRadiusKm = serviceRadiusKm;
    }

    public double getStartingRate() {
        return startingRate;
    }

    public void setStartingRate(double startingRate) {
        this.startingRate = startingRate;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
