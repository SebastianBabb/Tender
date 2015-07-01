package com.sebastianbabb.examples.yelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The MapActivity displays a full screen map and marks the location of a restaurant selected from
 * a list.  The calling intent submits an array of restaurant objects and the index of the restaurant
 * to mark on the map.  The long and latitude needed to plot the address on the map is retrieved by
 * the GeocodeService from the street address obtained from the restaurant object.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/30/2015
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";

    // Intent keys for retrieving restaurant array and selected index.
    public static final String RESTAURANTS_ARRAY_KEY = "RESTAURANTS_ARRAY_KEY";
    public static final String SELECTED_RESTAURANT_INDEX = "SELECTED_RESTAURANT_INDEX";

    // Google map camera zoom.
    private final int MAP_CAMERA_ZOOM = 15;

    // View widgets.
    private MapFragment mMapFragment;

    // Location (lat/long) of the restaurant.
    private Location location;

    // The restaurants array and index of the selected one from the list.
    private Restaurant[] restaurants;
    private int restaurantIndex;

    // Local broadcast receiver for receiving updates from the geocoding service.
    private GeocodeBroadcastReceiver mGeocodeBroadcastReceiver;

    // Address of the selected restaurant.
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "in onCreate(Bundle savedInstanceState)");
        setContentView(R.layout.activity_map);

        // Wire up the map view.
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Setup the broadcast receiver for catching updates from the geocoding service.
        mGeocodeBroadcastReceiver = new GeocodeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(GeocodeService.ACTION_GEOCODE_REQUEST_SERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
        localBroadcastManager.registerReceiver(mGeocodeBroadcastReceiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "in onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "in onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "in onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "in onStop()");
    }

    /*
     * When the app is destroyed, unregister the broadcast receiver.
     */
    @Override
    public void onDestroy() {
        // Dont forget to call the parent function.
        super.onDestroy();
        Log.i(TAG, "in onDestroy()");
        try {
            unregisterReceiver(mGeocodeBroadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the map fragment is done loading.  This is where you can start making references
     * to the map without too much concern you will be referencing a null object.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Retrieve the intent from YelpRequestActivity.
        Intent mapIntent = getIntent();

        /*
         * Unpack the restaurant array and index of the selected restaurant.  The restaurant array
         * must be retrieved and cast to an object array and then copied to the restaurant array.
         * A direct cast (Restaurant[])mapIntent.getSerializableExtra(RESTAURANTS_ARRAY_KEY) will
         * not work on all versions of android - worked on 5.0.1, but failed on 4.4.2.
         */
        Object[] temp = (Object[]) mapIntent.getSerializableExtra(RESTAURANTS_ARRAY_KEY);
        // Initialize the restaurant array to the same size as temp array.
        restaurants = new Restaurant[temp.length];
        // Copy temp objects to restaurants array as restaurant objects.
        System.arraycopy(temp, 0, restaurants, 0, temp.length);
        // Unpack the index of the selected restaurant.
        restaurantIndex = mapIntent.getIntExtra(SELECTED_RESTAURANT_INDEX, 0);

        // Done with the temp array, mark as null for gc.
        temp = null;

        // Merge the street address with city, state, and zip.
        address = restaurants[restaurantIndex].getAddress() + ", " +
                  restaurants[restaurantIndex].getCityStateZip();

        // Log the address.
        Log.i(TAG, "Address: " + address);

        /*
         * Start the geocoding service.  This service will take a street address and return a
         * location object containing coordinates that can be used to plot the address on the google
         * map.
         */
        Intent serviceRequestIntent = new Intent(this, GeocodeService.class);
        serviceRequestIntent.putExtra(GeocodeService.ADDRESS_PARAMETER_KEY, address);
        startService(serviceRequestIntent);
    }

    /**
     * This is the method that will be executed when the service has completed its background task.
     * That is, this is where the map gets updated.
     *
     * @author Sebastian Babb
     * @version 1.0
     *          Date: 06/30/2015
     */
    private class GeocodeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the search results from the intent and store them in an outer class field
             * so that they can used to update the map.
             */
            location = intent.getParcelableExtra(GeocodeService.LOCATION_RESULT_KEY);

            // Update the map.
            if (location != null)
                displayLocation();
        }
    }

    /**
     * Marks the address on the map with a flag, moves the camera over it, and zooms in.
     */
    private void displayLocation() {
        // Display the results.
        LatLng restaurantLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        // Ensure that the map fragment is not a null reference.
        if (mMapFragment.getMap() != null) {
            mMapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLong, MAP_CAMERA_ZOOM));
            mMapFragment.getMap().addMarker(new MarkerOptions().title(restaurants[restaurantIndex].getName()).position(restaurantLatLong));
        }
    }
}

