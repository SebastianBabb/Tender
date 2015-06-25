package com.sebastianbabb.examples.yelp;

import java.io.Serializable;

/**
 * A class for storing information about restaurants.
 * This class must implement the Serializable interface because it
 * is intended to be passed through an intent in an android application,
 * which requires all objects be serializable.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/17/2015
 */
public class Restaurant implements Serializable {
    /*
     * Data Fields.
     */
    private String mName;
    private String mMobileUrl;
    private String mAddress;
    private String mNeighborhood;
    private String mCityStateZip;

    /*
     * Constructors.
     */
    public Restaurant() {
        // Default constructor.
    }

    public Restaurant(String pName) {
        this.mName = pName;
    }


    public Restaurant(String pName, String pMobileUrl) {
        this.mName = pName;
        this.mMobileUrl = pMobileUrl;
    }

    public Restaurant(String pName, String pMobileUrl, String pAddress) {
        this.mName = pName;
        this.mMobileUrl = pMobileUrl;
        this.mAddress = pAddress;
    }

    public Restaurant(String pName, String pMobileUrl, String pAddress, String pNeighborhood) {
        this.mName = pName;
        this.mMobileUrl = pMobileUrl;
        this.mAddress = pAddress;
        this.mNeighborhood = pNeighborhood;
    }

    public Restaurant(String pName, String pMobileUrl, String pAddress, String pNeighborhood, String pCityStateZip) {
        this.mName = pName;
        this.mMobileUrl = pMobileUrl;
        this.mAddress = pAddress;
        this.mCityStateZip = pCityStateZip;
        this.mNeighborhood = pNeighborhood;
    }

    /*
     * Setters.
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    public void setMobileUrl(String mMobileUrl) {
        this.mMobileUrl = mMobileUrl;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setCityStateZip(String pCityStateZip) {
        this.mCityStateZip = pCityStateZip;
    }

    public void setmNeighborhood(String mNeighborhood) {
        this.mNeighborhood = mNeighborhood;
    }

    /*
     * Getters.
     */
    public String getName() {
        return this.mName;
    }

    public String getMobileUrl() {
        return this.mMobileUrl;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getCityStateZip() {
        return this.mCityStateZip;
    }

    public String getNeighborhood() {
        return this.mNeighborhood;
    }
}

