package com.bsm.tradenest.model;

public class Location {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    private String type = "Point";   // MUST be "Point"
    private double[] coordinates;    // [longitude, latitude]

    public Location(double lng, double lat) {
        this.coordinates = new double[]{lng, lat};
    }


}
