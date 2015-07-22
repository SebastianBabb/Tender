package com.csc413.group2.tender;

import java.io.Serializable;

/**
 * Created by Elaine on 7/9/2015.
 */
public class Results implements Serializable {
    private Region region;
    private int total;
    private Restaurant[] businesses;


    //getters
    public Region getRegion() {
        return region;
    }

    public int getTotal() {
        return total;
    }

    public Restaurant[] getBusinesses()
    {
        return businesses;
    }


    //setters
    public void setRegion(Region region) {
        this.region = region;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setBusinesses(Restaurant[] businesses) {
        this.businesses = businesses;
    }
}

