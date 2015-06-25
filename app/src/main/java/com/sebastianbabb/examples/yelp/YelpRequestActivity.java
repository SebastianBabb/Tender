package com.sebastianbabb.examples.yelp;

import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;

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
public class YelpRequestActivity extends Activity implements OnClickListener {
    // Log TAG for monitoring this activity in the system logs.
    public static final String TAG = "YelpRequestActivity";

    // Static constant hashmap key for passing values to intent.
    public static final String SEARCH_PARAMETER_HASH = "PARAM_HASH";

    // Static constant keys for hashmap values.
    public static final String HASH_KEY_SEARCH_LOCATION = "SEARCH LOCATION";
    public static final String HASH_KEY_SEARCH_TERM = "SEARCH TERM";
    public static final String HASH_KEY_SEARCH_LIMIT = "SEARCH LIMIT";

    private EditText mEditTextLocation;   // Search location input.
    private EditText mEditTextTerm;       // Search term input.
    private Spinner mSpinnerLimit;        // Search results limit.
    private TextView mTextViewResults;    // Search results display.
    private Button mButtonSearch;         // Search button.

    // A broadcast receiver for receiving the intent containing the search results from the service.
    private YelpBroadcastReceiver mBroadcastReceiver;

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
        Log.i(TAG, "In YelpRequestActivity constructor.");

        // Set the content view to our xml layout (res/layout/activity_yelp_request.xml).
        setContentView(R.layout.activity_yelp_request);

        // Wire up the view widgets.
        mEditTextLocation = (EditText) findViewById(R.id.et_location);
        mEditTextTerm = (EditText) findViewById(R.id.et_term);
        mSpinnerLimit = (Spinner) findViewById(R.id.spinner_limit);
        mTextViewResults = (TextView) findViewById(R.id.tv_results);
        mButtonSearch = (Button) findViewById(R.id.bt_search);

        // Make the textview that displays the results scrollable.
        mTextViewResults.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Create an ArrayAdapter using the string array declared in res/values/strings.xml
         * and a default spinner layout.
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_limit, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerLimit.setAdapter(adapter);

        // Set the onclick listener for the search button.
        mButtonSearch.setOnClickListener(this);

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

    // When the app is destroyed, unregister the broadcast receiver.
    @Override
    public void onDestroy() {
        // Dont forget to call the parent function.
        super.onDestroy();

        // Unregister the YelpBroadcastReceiver.
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * The onClick method retrieves the user inputs from the view widgets, stores their values into
     * a hashmap, creates an intent and stores that hashmap in the intent as an extra, then calls
     * the YelpRequestService class, a subclass of ServiceIntent, passing it that intent.
     *
     * @param v The view object, widget, which was clicked (mButtonSearch in this case).
     */
    @Override
    public void onClick(View v) {
        // Log location.
        Log.i(TAG, "In  onClick() method. Calling object: " + getResources().getResourceName(v.getId()));

        /*
         * Retrieve the user inputs from the widgets and store them in a hashmap to stored in an
         * intent.  As the Hashmap class is a subclass of serializable, it can be passed in an
         * intent as so Intent.putExtra(string_tag, Serializable).  The hashmap keys are static
         * constants so that they can be accessed by the YelpRequestService without object reference.
         */
        HashMap<String, String> hash = new HashMap<>();
        hash.put(HASH_KEY_SEARCH_LOCATION, mEditTextLocation.getText().toString());
        hash.put(HASH_KEY_SEARCH_TERM, mEditTextTerm.getText().toString());
        hash.put(HASH_KEY_SEARCH_LIMIT, mSpinnerLimit.getSelectedItem().toString());

        /*
         * Log the values being inserted in to the hashmap.
         */
        Log.i(TAG, "Building the hashmap.");
        Log.i(TAG, HASH_KEY_SEARCH_LOCATION + ":" + hash.get(HASH_KEY_SEARCH_LOCATION));
        Log.i(TAG, HASH_KEY_SEARCH_TERM + ":" + hash.get(HASH_KEY_SEARCH_TERM));
        Log.i(TAG, HASH_KEY_SEARCH_LIMIT + ":" + hash.get(HASH_KEY_SEARCH_LIMIT));

        /*
         * Build the service request intent for calling the YelpRequestService class and store the
         * hashmap of search parameters in it.  Then start the service.  A hashmap can stored in
         * an intent as an extra because it extends serializable.
         */
        Log.i(TAG, "Calling YelpRequestService.");
        Intent serviceRequestIntent = new Intent(this, YelpRequestService.class);
        serviceRequestIntent.putExtra(SEARCH_PARAMETER_HASH, hash);
        startService(serviceRequestIntent);
    }

    /**
     * A class that extends BroadcastReceiver for listening for updates from a background service.
     * This is the method that will be executed when the service has completed its background task.
     *
     * @author Sebastian Babb
     * @version 1.0
     *          Date: 06/17/2015
     */
    private class YelpBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieve the search results from the intent - an array of restaurants objects.
            Restaurant[] restaurants = (Restaurant[])intent.getSerializableExtra(YelpRequestService.RESULT_KEY);

            // Clear the textview of its default message.
            mTextViewResults.setText("");
            /*
             * Check that the restaurant array is not null and then loop through the results and
             * append the names, url, address to the edittext object.
             */
            if(restaurants != null) {
                for (int i = 0; i < restaurants.length; i++) {
                    mTextViewResults.append(restaurants[i].getName() + "\n");
                    mTextViewResults.append(restaurants[i].getMobileUrl() + "\n");
                    mTextViewResults.append(restaurants[i].getAddress() + ", " + restaurants[i].getNeighborhood() + ", ");
                    mTextViewResults.append(restaurants[i].getCityStateZip() + "\n\n");
                }
            } else {
                mTextViewResults.setText("No Matches Found.");
            }
        }
    }
}