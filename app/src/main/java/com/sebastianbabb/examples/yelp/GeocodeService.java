package com.sebastianbabb.examples.yelp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * This service uses the google Geocoder class to convert a street address to
 * geographic coordinates, store them in a Location object, and then return them to
 * the calling activity.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/30/2015
 */
public class GeocodeService extends IntentService {
    private static final String TAG = "GeocodeService";

    // Static constants for passing information back to the calling activity.
    public static final String ACTION_GEOCODE_REQUEST_SERVICE = "com.sebastianbabb.examples.yelp.LOCATION";
    public static final String ADDRESS_PARAMETER_KEY = "ADDRESS_PARAMETER_KEY";
    public static final String LOCATION_RESULT_KEY = "LOCATION_RESULT_KEY";

    public String address;

    /**
     * A subclass of IntentService must always call the parent constructor.  The string parameter
     * passed to the parent constructor will be used to name the IntentService's background thread.
     * In this instance, that background thread will be named "GeocodeServiceThread".
     */
    public GeocodeService() {
        super("GeocodeServiceThread");
        Log.i(TAG, "In GeocodeService constructor.");
    }

    /**
     * Retrieves the address from intent that was passed in by the activity and
     * converts it to a location object containing latitude and longitude.  Performed on
     * a background thread.
     *
     * @param intent The intent passed in by the calling activity which contains the street
     *               address to convert to a location object.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "in onHandleIntent(Intent intent)");
        // Check that the argument is not a null reference.
        if (intent != null) {
            // Store the address string from the intent and log it.
            address = intent.getStringExtra(ADDRESS_PARAMETER_KEY);
            Log.i(TAG, "Address: " + address);

            // Create a geocoder object for converting the address.
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            /*
             * The geocoder method getFromLocationName takes two arguments, the address to convert
             * and the number of results to return.  The return object is a list of those results.
             * We are only interested in one set of coordinates, so the second argument is 1.
             */
            try {
                // Convert the address to coordinates.
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                // Check that the returned list is not empty.
                if (!addresses.isEmpty()) {
                    // Create a location object to store the latitude and longitude.
                    Location location = new Location("empty");

                    // There should be only one object in the list with index 0.
                    location.setLatitude(addresses.get(0).getLatitude());
                    location.setLongitude(addresses.get(0).getLongitude());

                    // Log the latitude and longitude.
                    Log.i(TAG, "Latitude: " + location.getLatitude());
                    Log.i(TAG, "Longitude: " + location.getLongitude());

                    // Build an intent to broadcast the results to the calling activity.
                    Intent resultIntent = new Intent();
                    resultIntent.setAction(ACTION_GEOCODE_REQUEST_SERVICE);
                    resultIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    resultIntent.putExtra(LOCATION_RESULT_KEY, location);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
                } else {
                    Log.d(TAG, "NO GEOCODE FOUND.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
