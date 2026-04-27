package com.bsm.tradenest.dto;

public class RegisterWorkerRequest {
    public String name;
    public String skill;
    // Use boxed Double (nullable) so Jackson does not crash on null values
    public Double lat;
    public Double lng;
    public double radiusKm;
    public double ratePerHour;
    public int experienceYears;
    public String bio;
    public boolean available = true;
}
