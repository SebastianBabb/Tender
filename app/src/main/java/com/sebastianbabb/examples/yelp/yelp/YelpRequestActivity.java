package com.sebastianbabb.examples.yelp.yelp;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.sebastianbabb.examples.yelp.ListItemAdapter;
import com.sebastianbabb.examples.yelp.maps.MapActivity;
import com.sebastianbabb.examples.yelp.R;
import com.sebastianbabb.examples.yelp.Restaurant;

/**
 * The YelpRequestActivity is an example activity demonstrating the use of the yelp API
 * within an android application.  It creates a simple UI consisting of three inputs - search
 * location, search term, and the number of search results to return - and a button to execute the
 * search.  The API query is executed in a background service and the output field in the UI is
 * updated when the background service has been completed.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/17/2015
 */
public class YelpRequestActivity extends ListActivity {
    // Log TAG for monitoring this activity in the system logs.
    public static final String TAG = "YelpRequestActivity";

    // Static constant hashmap key for passing values to intent.
    public static final String SEARCH_PARAMETER_HASH = "PARAM_HASH";

    // Static constant keys for hashmap values.
    public static final String HASH_KEY_SEARCH_LOCATION = "SEARCH LOCATION";
    public static final String HASH_KEY_SEARCH_TERM = "SEARCH TERM";
    public static final String HASH_KEY_SEARCH_LIMIT = "SEARCH LIMIT";

    // The number of search results to return.
    private String SEARCH_LIMIT = "20";

    // Location layout.
    private LinearLayout mLocationLayout;

    // View widgets.
    private DrawerLayout mNavDrawerLayout;  // Navigation drawer.
    private ListView mNavDrawerListView;    // Navigation drawer list view.
    private EditText mEditTextLocation;     // Search location input.
    private EditText mEditTextTerm;         // Search term input.
    private TextView mTextViewEmpty;        // Default results display (needed for updating).
    private ImageView mToggleLocationLayoutButton;  // Button to toggle location setting view.
    private ImageView mOpenNavDrawerButton;         // Button to open navigation drawer.
    private ImageView mGetCurrentLocationButton;    // Button to set location to current location.

    // View animations.
    private Animation mOpenLocationAnimation;
    private Animation mCloseLocationAnimation;
    private Animation mRotateLocationButton;

    // A broadcast receiver for receiving the intent containing the search results from the service.
    private YelpBroadcastReceiver mBroadcastReceiver;

    // Holds the restaurant results from yelp.
    private Restaurant[] restaurants;

    // Location layout animation flags.
    private boolean isRunning = false;
    private boolean isLocationOpen = false;

    // Current location.
    private String mCurrentSearchLocation;

    // The onCreate method is called when the application is opened. This is were everything happens.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         *All overridden callbacks must call their parent method.  This will
         * come into use later when we want to manage the activity lifecycle
         * properly by saving states and information.
         */
        super.onCreate(savedInstanceState);

        // Log the location.
        Log.i(TAG, "in onCreate(Bundle savedInstanceState)");

        // Set the content view to our xml layout (res/layout/activity_yelp_request.xml).
        setContentView(R.layout.activity_yelp_request);

        final Context context = this;   // TEMP FOR NOW.

        // Wire up the view widgets.
        mLocationLayout = (LinearLayout) findViewById(R.id.location_setting_view);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawerListView = (ListView) findViewById(R.id.navigation_drawer_listview);
        mEditTextLocation = (EditText) findViewById(R.id.et_location);
        mEditTextTerm = (EditText) findViewById(R.id.search_term);
        mTextViewEmpty = (TextView) getListView().getEmptyView();
        mToggleLocationLayoutButton = (ImageView) findViewById(R.id.bt_toggle_location_view);
        mOpenNavDrawerButton = (ImageView) findViewById(R.id.bt_open_nav_drawer);
        mGetCurrentLocationButton = (ImageView) findViewById(R.id.bt_get_current_location);

        /*
         * Set the items to display in the navigation drawer (settings, history, ect...)
         */
        String[] navItems = {"Settings", "About US"}; /* MOVE TO STRINGS XML FILE */
        mNavDrawerListView.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_drawer_list_item, navItems));

        /*
         * Load animation files.
         */
        mOpenLocationAnimation = AnimationUtils.loadAnimation(this, R.anim.open_set_location_view);
        mCloseLocationAnimation = AnimationUtils.loadAnimation(this, R.anim.close_set_location_view);
        mRotateLocationButton = AnimationUtils.loadAnimation(this, R.anim.rotate_location_button);

        /*
         * Set an animation listener to determine when opening the location settings view is done.
         */
        mOpenLocationAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Set the animation running flag to true.
                isRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Set the animation running flag to false.
                isRunning = false;
                // Hide the navigation drawer button so that it cant be clicked.
                mOpenNavDrawerButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing.
            }
        });

        mCloseLocationAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Set the animation running flag to true.
                isRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Set the animation running flag to false.
                isRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing.
            }
        });

        /*
         * Set the onclick listener for the open location layout button (top right button).
         */
        mToggleLocationLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * Check that the animation is not running and that the location layout is not
                 * already open, then open it.  Otherwise, close it.
                 */
                if (!isRunning && !isLocationOpen) {
                    openLocationSettings();
                } else {
                    closeLocationSettings();
                }
            }
        });

        /*
         * Queries location services and sets the location field to the current location.
         */
        mGetCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Aquiring Current Location", Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * Set the action listener for the search term input field.  This will be called when the
         * user hits the search button on the virtual keyboard.
         */
        mEditTextTerm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Validate the user input.
                // Start the loading dialog.
                // Perform your search.
                searchYelp();

                return false;
            }
        });

        /*
         * Set the action listener for the location input field. This will be executed when the user
         * presses the carriage return button on the virtual keyboard.
         */
        mEditTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(context, mEditTextLocation.getText().toString(), Toast.LENGTH_LONG).show();

                // Dismiss the virtual keyboard.
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mEditTextLocation.getWindowToken(), 0);

                // Close the location settings view.
                closeLocationSettings();

                // Set the open navigation drawer button back to visible.
                mOpenNavDrawerButton.setVisibility(View.VISIBLE);

                return false;
            }
        });

        /*
         * Set the onclick listener for the open navigation drawer button (top left button).
         */
        mOpenNavDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        /*
         * Set the broadcast receiver to listen for updates from YelpRequestService.  There is no
         * builtin YelpBroadcastReceiver class, so we have to construct our own.  It is implemented
         * at the end of the activity as an inner class that extends the BroadcastReceiver class.
         */
        mBroadcastReceiver = new YelpBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(YelpRequestService.ACTION_YELP_REQUEST_SERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
        localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
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
        // Unregister the YelpBroadcastReceiver.
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Animates the opening of the search location setting view storing the current search location
     * for comparison when the view is closed to determine if a new search location has been set.
     */
    private void openLocationSettings() {
        // Store the current location.
        mCurrentSearchLocation = mEditTextLocation.getText().toString();
        // Make the location layout visible.
        mLocationLayout.setVisibility(View.VISIBLE);
        // Slide the location layout out from the right.
        mLocationLayout.startAnimation(mOpenLocationAnimation);
        // Rotate the toggle location button in sync with the opening of the location layout.
        mToggleLocationLayoutButton.startAnimation(mRotateLocationButton);
        // Set the location layout is open flag to true.
        isLocationOpen = true;
    }

    /**
     * Animates the closing of the search location setting view checking if a new search location has
     * been entered.  If a new seach location has been entered, it stores the location and executes
     * a new search with the existing search term and the updated location.
     */
    private void closeLocationSettings() {
        // Slide the location layout in from the left.
        mLocationLayout.startAnimation(mCloseLocationAnimation);
        // Rotate the toggle location button in sync with the closing of the location layout.
        mToggleLocationLayoutButton.startAnimation(mRotateLocationButton);
        // Set the location layout open flag to false.
        isLocationOpen = false;
        // Remove the layout to avoid bleed through click events.
        mLocationLayout.setVisibility(View.GONE);
        // Set the open navigation drawer button back to visible.
        mOpenNavDrawerButton.setVisibility(View.VISIBLE);
        /*
         * Check if a new location was set.  If so, store it and update the search.
         */
        if(!mCurrentSearchLocation.equalsIgnoreCase(mEditTextLocation.getText().toString())) {
            Log.i(TAG,"New location set: " + mEditTextLocation.getText().toString());
            // Update the current search location
            mCurrentSearchLocation = mEditTextLocation.getText().toString();
            // Execute a yelp search with the new location.
            searchYelp();
        }

    }

    /**
     * Searches yelp using the parameters provided in the search term and location fields by the user.
     */
    public void searchYelp() {
        // Log location.
        Log.i(TAG, "calling searchYelp() Term: " + mEditTextTerm.getText().toString() + "Location: " + mEditTextLocation.getText().toString());

        // Dismiss the virtual keyboard.
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEditTextTerm.getWindowToken(), 0);

        /*
         * Retrieve the user inputs from the widgets and store them in a hashmap to stored in an
         * intent.  As the Hashmap class is a subclass of serializable, it can be passed in an
         * intent as so Intent.putExtra(string_tag, Serializable).  The hashmap keys are static
         * constants so that they can be accessed by the YelpRequestService without object reference.
         */
        HashMap<String, String> hash = new HashMap<>();
        hash.put(HASH_KEY_SEARCH_LOCATION, mEditTextLocation.getText().toString());
        hash.put(HASH_KEY_SEARCH_TERM, mEditTextTerm.getText().toString());
        hash.put(HASH_KEY_SEARCH_LIMIT, SEARCH_LIMIT);

        /*
         * Log the values being inserted in to the hashmap.
         */
        Log.i(TAG, "Building the hashmap.");
        Log.i(TAG, HASH_KEY_SEARCH_LOCATION + ":" + hash.get(HASH_KEY_SEARCH_LOCATION));
        Log.i(TAG, HASH_KEY_SEARCH_TERM + ":" + hash.get(HASH_KEY_SEARCH_TERM));
        Log.i(TAG, HASH_KEY_SEARCH_LIMIT + ":" + SEARCH_LIMIT);

        /*
         * Build the service request intent for calling the YelpRequestService class and store the
         * hashmap of search parameters in it.  Then start the service.  A hashmap can stored in
         * an intent as an extra because it extends serializable.  This is where the call to the
         * yelp api starts.
         */
        Log.i(TAG, "Calling YelpRequestService.");
        Intent serviceRequestIntent = new Intent(this, YelpRequestService.class);
        serviceRequestIntent.putExtra(SEARCH_PARAMETER_HASH, hash);
        startService(serviceRequestIntent);
    }

    /**
     * Displays the name, address, and mobile url of the restaurant that is clicked.
     *
     * @param lv       The ListView object the item belongs to.
     * @param v        The view of the clicked object.
     * @param position The position in the list of the clicked item (starting at 0).
     * @param id       The object id of the clicked item.
     */
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        Toast.makeText(this, restaurants[position].getName() + "\n" +
                restaurants[position].getLocation().getAddress() + ", " +
                restaurants[position].getMobileUrl(), Toast.LENGTH_SHORT).show();

        // Start the map activity.
        startMapActivity(position);
    }

    /**
     * Opens the map activity.
     *
     * @param position The index of the restaurant that was touched in the list.
     */
    private void startMapActivity(int position) {
        // Create a new intent to call the map activity.
        Intent mapIntent = new Intent(this, MapActivity.class);
        // Store the restaurants array and which restaurant index was touched.
        mapIntent.putExtra(MapActivity.RESTAURANTS_ARRAY_KEY, restaurants);
        mapIntent.putExtra(MapActivity.SELECTED_RESTAURANT_INDEX, position);
        // Start the activity.
        startActivity(mapIntent);
    }

    /**
     * A class that extends BroadcastReceiver for listening for updates from a background service.
     * This is the method that will be executed when the service has completed its background task.
     * That is, this is where the UI gets updated with the search results.
     *
     * @author Sebastian Babb
     * @version 1.0
     *          Date: 06/17/2015
     */
    private class YelpBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the search results from the intent and store them in an outer class field
             * so that they can used by the addItems method.
             */
            restaurants = (Restaurant[]) intent.getSerializableExtra(YelpRequestService.RESULT_KEY);

            /*
             * Check that the restaurant array is not null and then create am array adapter
             * using the array of restaurants and call setListAdapter to populate the list view.
             */
            if (restaurants != null) {
                ListItemAdapter restaurantListArrayAdapter = new ListItemAdapter(context, restaurants);
                setListAdapter(restaurantListArrayAdapter);
            } else {
                /*
                 * Check if the list has been displayed and clear it.  Then update the default
                 * text view to say that no results were found.
                 */
                if (getListView() != null)
                    getListView().setAdapter(null);
                mTextViewEmpty.setText("No Results Found...");
            }
        }
    }
}