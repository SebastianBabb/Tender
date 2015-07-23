package com.csc413.group2.tender.gson;

import com.csc413.group2.tender.gson.Coordinates;

import java.io.Serializable;

/**
 * Created by Elaine on 7/9/2015.
 */
public class Region implements Serializable {
    private Coordinates span;
    private Coordinates center;


    //getters
    public Coordinates getSpan() {
        return span;
    }

    public Coordinates getCenter() {
        return center;
    }


    //setters
    public void setSpan(Coordinates span) {
        this.span = span;
    }

    public void setCenter(Coordinates center) {
        this.center = center;
    }
}
