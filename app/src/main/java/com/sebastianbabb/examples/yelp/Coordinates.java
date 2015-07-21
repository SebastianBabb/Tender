package com.sebastianbabb.examples.yelp;

import java.io.Serializable;

/**
 * Created by Elaine on 7/9/2015.
 */
public class Coordinates implements Serializable {
    private double latitude;
    private double longitude;
    private double latitude_delta;
    private double longitude_delta;


    //getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitudeDelta() {
        return latitude_delta;
    }

    public double getLongitudeDelta() {
        return longitude_delta;
    }


    //setters
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitudeDelta(double latitude_delta) {
        this.latitude = latitude_delta;
    }

    public void setLongitudeDelta(double longitude_delta) {
        this.longitude = longitude_delta;
    }
}
