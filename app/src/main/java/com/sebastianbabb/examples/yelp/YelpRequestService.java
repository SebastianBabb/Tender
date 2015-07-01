package com.sebastianbabb.examples.yelp;

import org.json.JSONException;

import java.util.HashMap;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * This service uses the yelp api to retrieve information about restaurants given a
 * set of parameters passed in by an intent - location, search term, number of results.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/17/2015
 */
public class YelpRequestService extends IntentService {
    // Log TAG for monitoring this service in the system logs.
    public static final String TAG = "YelpRequestService";

    // Static constants for passing information back to the calling activity.
    public static final String ACTION_YELP_REQUEST_SERVICE = "com.sebastianbabb.examples.yelp.RESULTS";
    public static final String RESULT_KEY = "RESULT_KEY";

    /**
     * A subclass of IntentService must always call the parent constructor.  The string parameter
     * passed to the parent constructor will be used to name the IntentService's background thread.
     * In this instance, that background thread will be named "YelpRequestServiceThread".
     */
    public YelpRequestService() {
        super("YelpRequestServiceThread");
        Log.i(TAG, "In YelpRequestService constructor.");
    }

    /*
     * THIS IS WHERE YOU NEED TO ADD THE YELP API CREDENTIALS.
     * OAuth credentials for the Yelp API.
     * Site: http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "";
    private static final String CONSUMER_SECRET = "";
    private static final String TOKEN = "";
    private static final String TOKEN_SECRET = "";

    /**
     * In an IntentService, the onHandleIntent method is run on a background thread (the
     * "YelpRequestServiceThread" from the call to the parent constructor).  While it runs, it will
     * broadcast its current status using a LocalBroadcastManager, not a BroadcastManager, as the
     * service will not be available to applications other than the one implementing it.
     *
     * @param workIntent The intent that starts the IntentService.  It contains values for search
     *                   location, term, and the number of results (JSON objects) to return.
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Retrieve the hashmap from stored in the intent extras.
        HashMap<String, String> hash = (HashMap<String, String>) workIntent.getSerializableExtra(
                YelpRequestActivity.SEARCH_PARAMETER_HASH);

        // Store the search parameters from the hashmap in strings.
        String location = hash.get(YelpRequestActivity.HASH_KEY_SEARCH_LOCATION);
        String term = hash.get(YelpRequestActivity.HASH_KEY_SEARCH_TERM);
        String limit = hash.get(YelpRequestActivity.HASH_KEY_SEARCH_LIMIT);

        /*
         * Log the search values that were unpacked from the hashmap for monitoring.
         */
        Log.i(TAG, "Location:" + location);
        Log.i(TAG, "Term:" + term);
        Log.i(TAG, "Limit:" + limit);


        /*
         *Create a YelpApi object and call it's search method passing it the search parameters.
         */
        YelpAPI yelp = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        String results = yelp.searchForBusinessesByLocation(term, location, limit);
        Restaurant[] restaurants = null;
        /*
         * Log the resulting JSON string.
         */
        Log.i(TAG, results);


        /*
         * Create a JSONParser object passing the JSON string in as a parameter.  Then call the
         * JSONParser's getRestaurants method to retrieve a list a restaurant objects.
         */
        try {
            JSONParser parser = new JSONParser(results);
            // Parse the json for restaurant objects.
            restaurants = parser.getRestaurants("businesses");
        } catch (JSONException e) {
            System.out.print(e.toString());
        }

        /*
         * Broadcast the results to YelpRequestActivity using the local broadcast manager.
         * This is where the search results get packaged in an intent and sent back to the
         * activity that called the service.
         */
        Intent resultIntent = new Intent();
        resultIntent.setAction(ACTION_YELP_REQUEST_SERVICE);
        resultIntent.addCategory(Intent.CATEGORY_DEFAULT);
        resultIntent.putExtra(RESULT_KEY, restaurants);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }
}

