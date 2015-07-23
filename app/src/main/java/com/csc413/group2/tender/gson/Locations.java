package com.csc413.group2.tender.gson;

import com.csc413.group2.tender.gson.Coordinates;

import java.io.Serializable;

/**
 * Created by Elaine on 7/9/2015.
 */
public class Locations implements Serializable {
    private String cross_streets;
    private String city;
    private String[] display_address;
    private String geo_accuracy;
    private String[] neighborhoods;
    private String postal_code;
    private String country_code;
    private String[] address;
    private Coordinates coordinate;
    private String state_code;

    //getters
    public String getCrossStreets() {
        return cross_streets;
    }

    public String getCity() {
        return city;
    }

    public String[] getDisplayAddress() {
        return display_address;
    }

    public String getGeoAccuracy() {
        return geo_accuracy;
    }

    public String[] getNeighboorhoods() {
        return neighborhoods;
    }

    public String getPostalCode() {
        return postal_code;
    }

    public String getCountryCode() {
        return country_code;
    }

    public String[] getAddress() {
        return address;
    }

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public String getStateCode() {
        return state_code;
    }


    //setters
    public void setCrossStreets(String cross_streets) {
        this.cross_streets = cross_streets;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDisplayAddress(String[]display_address) {
        this.display_address = display_address;
    }

    public void setGeoAccuracy(String geo_accuracy) {
        this.geo_accuracy = geo_accuracy;
    }

    public void setNeighboorhoods(String[] neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    public void setPostalCode(String postal_code) {
        this.postal_code = postal_code;
    }

    public void setCountryCode(String country_code) {
        this.country_code = country_code;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }

    public void setStateCode(String state_code) {
        this.state_code = state_code;
    }
}
