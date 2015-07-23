package com.csc413.group2.tender;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.csc413.group2.tender.database.SearchRecordContract.*;
import com.csc413.group2.tender.maps.MapActivity;

/**
 * Created by sebastian on 7/23/15.
 */
public class VenueActivity extends Activity implements OnClickListener {
    // TAG for logging.
    public static final String TAG = "VenueActivity";

    // Intent key for passing restaurant to other activities.
    public static final String VENUE_KEY = "VENUE_KEY";

    // Views.
    private TextView mTextViewName;
    private TextView mTextViewAddress;
    private TextView mTextViewURL;
    private TextView mTextViewDatabase;
    private Button mButtonMap;
    private Button mButtonMenu;
    private Button mButtonHistory;

    // The restaurant to be featured.
    private Restaurant  mRestaurant;

    // Database helper for search records.
    private SearchRecordDbHelper mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        // Set the main layout.
        setContentView(R.layout.activity_venue);

        // Wire up the views.
        mTextViewName = (TextView) findViewById(R.id.tv_name);
        mTextViewAddress = (TextView) findViewById(R.id.tv_address);
        mTextViewURL = (TextView) findViewById(R.id.tv_url);
        mTextViewDatabase = (TextView) findViewById(R.id.tv_database);
        mButtonMap = (Button) findViewById(R.id.bt_map);
        mButtonMenu = (Button) findViewById(R.id.bt_menu);
        mButtonHistory = (Button) findViewById(R.id.bt_history);

        // Make the database textview scrollable.
        mTextViewDatabase.setMovementMethod(new ScrollingMovementMethod());

        // Set the onclick listeners.
        mButtonMap.setOnClickListener(this);
        mButtonMenu.setOnClickListener(this);
        mButtonHistory.setOnClickListener(this);

        // Retrieve and unpack the intent from the MainActivity.
        Intent venueIntent = getIntent();
        if (venueIntent != null) {
            mRestaurant = (Restaurant) venueIntent.getSerializableExtra(VENUE_KEY);
            // Display the venue information.
            mTextViewName.setText(mRestaurant.getName());
            mTextViewAddress.setText(mRestaurant.getLocation().getAddress()[0] + "\n" +
                    mRestaurant.getLocation().getCity() + ", " +
                    mRestaurant.getLocation().getStateCode() + ", " +
                    mRestaurant.getLocation().getPostalCode());
            mTextViewURL.setText(mRestaurant.getImageUrl());
        }
    } // end onCreate()

    // Handle all the click events.
    @Override
    public void onClick(View v) {
        // Determine what button was clicked and execute code accordingly.
        switch(v.getId()) {
            case R.id.bt_map:
                // The map button was clicked.  Open MapActivity.
                startMapActivity(mRestaurant);
                break;
            case R.id.bt_menu:
                // The menu button was clicked.  Nothing to do yet.
                Toast.makeText(this, "Open menu activity...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_history:
                // The seach history button was clicked.  Display database content.
                // Instantiate a new database handler and use it to retrieve a readable database instance.
                mDbHelper = new SearchRecordDbHelper(this);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                // Create an array of the names of the columns to retrieve for each record.
                String[] columns = { SearchRecordEntry._ID,
                                     SearchRecordEntry.COLUMN_NAME_QUERY_TERM,
                                     SearchRecordEntry.COLUMN_NAME_QUERY_LOCATION,
                                     SearchRecordEntry.COLUMN_NAME_TIME_STAMP };

                // Query the database and retrieve a cursor object.
                Cursor cursor = db.query(SearchRecordEntry.TABLE_NAME, columns,null,null,null,null,null);

                // Move the cursor to the first record in the db.
                cursor.moveToFirst();
                // Print each row to the textview.
                while(!cursor.isLast()) {
                    mTextViewDatabase.append(cursor.getString(0) + " ");
                    mTextViewDatabase.append(cursor.getString(1) + " ");
                    mTextViewDatabase.append(cursor.getString(2) + " ");
                    mTextViewDatabase.append(cursor.getString(3) + "\n");
                    cursor.moveToNext();
                }

                // The last row still needs to be printed.  Print it.
                mTextViewDatabase.append(cursor.getString(0) + " ");
                mTextViewDatabase.append(cursor.getString(1) + " ");
                mTextViewDatabase.append(cursor.getString(2) + " ");
                mTextViewDatabase.append(cursor.getString(3));
                break;
        }
    }

    /**
     * Opens MapActivity.
     *
     * @param restaurant The restaurant object to display in the map.
     */
    private void startMapActivity(Restaurant restaurant) {
        // Create a new intent to call the map activity.
        Intent mapIntent = new Intent(this, MapActivity.class);
        // Store the restaurant object to display on the map.
        mapIntent.putExtra(MapActivity.RESTAURANT_KEY, restaurant);
        // Start the activity.
        startActivity(mapIntent);
    }
}
