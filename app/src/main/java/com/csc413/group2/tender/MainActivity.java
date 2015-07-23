package com.csc413.group2.tender;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;

import com.csc413.group2.tender.restaurantlist.RestaurantListItemAdapter;
import com.csc413.group2.tender.database.SearchRecordContract.*;
import com.csc413.group2.tender.preferences.AppPreferences;
import com.csc413.group2.tender.yelp.YelpRequestService;

/**
 * The Main Activity for Tender.
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/17/2015
 */
public class MainActivity extends ListActivity {
    // Log TAG for monitoring this activity in the system logs.
    public static final String TAG = "MainActivity";

    // Static constant hashmap key for passing values to intent.
    public static final String SEARCH_PARAMETER_HASH = "PARAM_HASH";

    // Static constant keys for hashmap values.
    public static final String HASH_KEY_SEARCH_LOCATION = "SEARCH LOCATION";
    public static final String HASH_KEY_SEARCH_TERM = "SEARCH TERM";
    public static final String HASH_KEY_SEARCH_LIMIT = "SEARCH LIMIT";

    // The number of search results to return.
    private String SEARCH_LIMIT = "20";

    // View widgets.
    private LinearLayout mLoadingLayout;
    private LinearLayout mLocationLayout;
    private DrawerLayout mNavDrawerLayout;  // Navigation drawer.
    private ListView mNavDrawerListView;    // Navigation drawer list view.
    private EditText mEditTextLocation;     // Search location input.
    private EditText mEditTextTerm;         // Search term input.
    private TextView mTextViewEmpty;        // Default results display (needed for updating).
    private ImageView mToggleLocationLayoutButton;  // Button to toggle location setting view.
    private ImageView mOpenNavDrawerButton;         // Button to open navigation drawer.
    private ImageView mGetCurrentLocationButton;    // Button to set location to current location.
    private ImageView mLoadingImage;

    // View animations.
    private Animation mOpenLocationAnimation;
    private Animation mCloseLocationAnimation;
    private Animation mRotateLocationButtonAnimation;
    private Animation mLoadingAnimation;

    private SearchRecordDbHelper mDbHelper;

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
        mLoadingLayout= (LinearLayout) findViewById(R.id.loading_layout);
        mLocationLayout = (LinearLayout) findViewById(R.id.location_setting_view);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawerListView = (ListView) findViewById(R.id.navigation_drawer_listview);
        mEditTextLocation = (EditText) findViewById(R.id.et_location);
        mEditTextTerm = (EditText) findViewById(R.id.search_term);
        mTextViewEmpty = (TextView) getListView().getEmptyView();
        mToggleLocationLayoutButton = (ImageView) findViewById(R.id.bt_toggle_location_view);
        mOpenNavDrawerButton = (ImageView) findViewById(R.id.bt_open_nav_drawer);
        mGetCurrentLocationButton = (ImageView) findViewById(R.id.bt_get_current_location);
        mLoadingImage = (ImageView) findViewById(R.id.loading_image);

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
        mRotateLocationButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_location_button);
        mLoadingAnimation = AnimationUtils.loadAnimation(this, R.anim.loading_animation);
        /*
         *Set onItemClick Listener for the nav drawer items.
         */

        /*
         * Navigation drawer item click listeners.
         */
        mNavDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent i = new Intent(MainActivity.this, AppPreferences.class);
                        i.putExtra("FROM_ACTIVITY","request");
                        startActivity(i);
                        break;
                    case 1:
                        //Add intent to the About Us page here
                        break;
                    //and so on
                    default:
                        break;
                }
            }
        });

        /*
         * Set n animation listeners to determine when opening/closing the location settings view is done.
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
                // NEEDS TO BE IMPLEMENTED.
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
                // Return false for all action besides down to avoid duplicate calls.
                if(textView.getImeActionId() != KeyEvent.ACTION_DOWN)
                    return false;

                // Validate the user input.
                // Start the loading dialog.
                // Perform your search.
                searchYelp();

                return true;
            }
        });

        /*
         * Set the action listener for the location input field. This will be executed when the user
         * presses the carriage return button on the virtual keyboard.
         */
        mEditTextLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
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
    } // End onCreate()

    /*
     * Remaining Lifecycle Callbacks.
     */
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
        mToggleLocationLayoutButton.startAnimation(mRotateLocationButtonAnimation);
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
        mToggleLocationLayoutButton.startAnimation(mRotateLocationButtonAnimation);
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
        // Display loading animation.
        startLoadingDialog();

        /*
         * Log the query to the database.  This should be put in a service intent or asynctask.
         */

        // Connect to the db.
        mDbHelper = new SearchRecordDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        /*
         * Create a entry to insert into the db.  Just the search term and the location for now.
         * The time stamp and record number are auto-generated.
         */
        ContentValues values = new ContentValues();
        values.put(SearchRecordEntry.COLUMN_NAME_QUERY_TERM, mEditTextTerm.getText().toString());
        values.put(SearchRecordEntry.COLUMN_NAME_QUERY_LOCATION, mEditTextLocation.getText().toString());

        // Insert the new search record into the db and store the row id for display.
        long newRowId;
        newRowId = db.insert(SearchRecordEntry.TABLE_NAME, null, values);

        // Display the row id.
        Toast.makeText(this, "Database Record Inserted: " + newRowId, Toast.LENGTH_LONG).show();

        /*
         * End of database operation.
         */

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
                restaurants[position].getLocation().getAddress()[0] + ", " +
                restaurants[position].getMobileUrl(), Toast.LENGTH_SHORT).show();

        // Start the venue activity.
        startVenueActivity(position);
    }

    /**
     * Opens the venue activity.
     *
     * @param position The index of the restaurant that was touched in the list.
     */
    private void startVenueActivity(int position) {
        // Create a new intent to call the map activity.
        Intent venueIntent = new Intent(this, VenueActivity.class);
        // Store the restaurants array and which restaurant index was touched.
        venueIntent.putExtra(VenueActivity.VENUE_KEY, restaurants[position]);
//        venueIntent.putExtra(VenueActivity.SELECTED_RESTAURANT_INDEX, position);
        // Start the activity.
        startActivity(venueIntent);
    }

    /**
     * Displays the loading dialog during searches.
     */
    private void startLoadingDialog(){
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingImage.startAnimation(mLoadingAnimation);
    }

    /**
     * Hides the loading dialog.
     */
    private void stopLoadingDialog() {
        mLoadingImage.clearAnimation();
        mLoadingLayout.setVisibility(View.GONE);
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
                RestaurantListItemAdapter restaurantListArrayAdapter = new RestaurantListItemAdapter(context, restaurants);
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

            // Stop loading animation.
            stopLoadingDialog();
        }
    }
}