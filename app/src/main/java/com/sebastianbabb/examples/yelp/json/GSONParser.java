package com.sebastianbabb.examples.yelp.json;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sebastianbabb.examples.yelp.Restaurant;
import com.sebastianbabb.examples.yelp.Results;

/**
 * A class for parsing JSON strings.
 * Created by sebastian on 6/18/15. Updated on 07/17/15 by elaine.
 *
 * @author Sebastian Babb
 * @version 1.1
 *          Date: 07/17/2015
 */
public class GSONParser {
    // Log TAG for monitoring in the system logs.
    private static final String TAG = "GSONParser";

    // Holds a list of restaurant names.
    private Restaurant[] restaurantList;

    public GSONParser(String json) throws JsonParseException{
        // Log the location.
        Log.i(TAG, "In constructor, argument passed: " + json);

        /*
         * Store the JSON string as a member and use it to build a JSONObject.
         */
        Gson gson = new Gson();
        Results restaurants = gson.fromJson(json, Results.class);
        restaurantList = restaurants.getBusinesses();
    }

    public Restaurant[] getRestaurantList() {
        return this.restaurantList;
    }
}
