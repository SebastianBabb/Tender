package com.csc413.group2.tender.splashscreen;

import com.csc413.group2.tender.preferences.AppPreferences;
import com.csc413.group2.tender.yelp.YelpRequestActivity;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.Context;

/**
 * An background service that sleeps for a specified time.
 *
 * @author Sebastian Babb
 */
public class SplashScreenService extends IntentService {
    private final String TAG = "SplashService";

    // The amount of time to display the splash screen in ms.
    private final int SPLASH_TIME = 5000;

    // Default constructor.
    public SplashScreenService() {
        // Dont forget to call the super class constructor.
        super("SplashServiceThread");
        Log.i(TAG, "SplashService()");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        /*
         * No need to retrieve intent extras as the splash activity did not pass this service any
         * any information.
         */




        // Create a new thread that sleeps for three seconds to simulate making api calls over the network.
        try {
            Thread.sleep(SPLASH_TIME);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        //Check if preferences have been previously set.




        // Now that all the api calls are done, start the suggestions activity.
        Intent suggestionsActivityIntent = new Intent(this, YelpRequestActivity.class);
        /*
         * Remove the splash activity from the activity back stack so the user can not navigate back to it.
         * Could also have put android:noHistory="true" in the manifest, but would not have been
         * able to navigate back to the splash screen from the location settings screen.
         */
        suggestionsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(suggestionsActivityIntent);
    }

}
