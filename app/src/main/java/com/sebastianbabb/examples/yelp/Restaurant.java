package com.sebastianbabb.examples.yelp;

import java.io.Serializable;

/**
 * Created by Elaine on 7/9/2015.
 */
public class Restaurant implements Serializable{
    private boolean is_claimed;
    private double rating;
    private String mobile_url;
    private String rating_img_url;
    private int review_count;
    private String name;
    private String rating_img_url_small;
    private String url;
    private boolean is_closed;
    private String id;
    private long phone;
    private String snippet_text;
    private String image_url;
    private String[][] categories;
    private String display_phone;
    private String rating_img_url_large;
    private String menu_provider;
    private long menu_date_updated;
    private String snippet_image_url;
    private Locations location;


    //getters
    public boolean getIsClaimed() {
        return is_claimed;
    }

    public double getRating() {
        return rating;
    }

    public String getMobileUrl() {
        return mobile_url;
    }

    public int getReviewCount() {
        return review_count;
    }

    public String getName() {
        return name;
    }

    public String getRatingImgUrlSmall() {
        return rating_img_url_small;
    }

    public String getUrl() {
        return url;
    }

    public boolean getIsClosed() {
        return is_closed;
    }

    public String getId() {
        return id;
    }

    public long getPhoneNumber() {
        return phone;
    }

    public String getSnippetText() {
        return snippet_text;
    }

    public String getImageUrl() {
        int index = image_url.lastIndexOf('/');
        image_url = image_url.substring(0,index) + "/l.jpg";
        return image_url;
    }

    public String[][]  getCatergories() {
        return categories;
    }

    public String getDisplayPhone() {
        return display_phone;
    }

    public String getMenuProvider() {
        return menu_provider;
    }

    public long getUpdatedMenuDate() {
        return menu_date_updated;
    }

    public String getSnippetImageUrl() {
        return snippet_image_url;
    }

    public Locations getLocation() {
        return location;
    }


    //setters
    public void setIsClaimed(boolean is_claimed) {
        this.is_claimed = is_claimed;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setMobileUrl(String mobile_url) {
        this.mobile_url = mobile_url;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSmallRatingImgUrl(String rating_img_url_small) {
        this.rating_img_url_small = rating_img_url_small;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIsClosed(boolean isClosed) {
        is_closed = isClosed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhoneNumber(long phone)
    {
        this.phone = phone;
    }

    public void setSnippetText() {
        this.snippet_text = snippet_text;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public void setCategories(String[][]  categories) {
        this.categories = categories;
    }

    public void setDisplayPhone(String display_phone) {
        this.display_phone = display_phone;
    }

    public void setMenuProvider(String menu_provider) {
        this.menu_provider = menu_provider;
    }

    public void setUpdatedMenuDate(long menu_date_updated) {
        this.menu_date_updated = menu_date_updated;
    }

    public void setSnippetImageUrl(String snippet_image_url) {
        this.snippet_image_url = snippet_image_url;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }


}

