package com.csc413.group2.tender.preferences;

import android.content.Intent;


/**
 * Created by Ace on 7/17/15.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.csc413.group2.tender.R;
import com.csc413.group2.tender.splashscreen.SplashScreenActivity;
import com.csc413.group2.tender.MainActivity;
/*
An activity that displays the preference screen. Android takes care of saving the settings that are
saved in the preferences.xml file which can then be accessed using the key given in the
preferences.xml file
 */

public class AppPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); //Sets preference activity view page
    }
    @Override
    /*
    Sets the action of the back button to go back to the splash screen after saving the settings
    rather than quit the app.
     */
    public void onBackPressed()
    {

        Intent i = new Intent(this, SplashScreenActivity.class);//Intent to go to the splash screen if that's where we came from.
        Intent fromIntent = getIntent();//Intent to get extras from to see which activity was previous
        String previousActivity;
        previousActivity = fromIntent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity.equals("splash")){
            this.startActivity(i);
    }
        if (previousActivity.equals("request")){
            Intent yelpRequest = new Intent(this, MainActivity.class);
            this.startActivity(yelpRequest);
        }
    }


}
