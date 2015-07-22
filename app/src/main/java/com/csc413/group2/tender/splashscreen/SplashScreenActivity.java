package com.csc413.group2.tender.splashscreen;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.media.MediaPlayer;
import android.graphics.drawable.AnimationDrawable;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import com.csc413.group2.tender.R;
import com.csc413.group2.tender.preferences.AppPreferences;
import com.csc413.group2.tender.splashAnimation;




/**
 * An example splash screen activity that uses a service intent to perform background operations.
 *
 * @author Sebastian Babb
 */
public class SplashScreenActivity extends Activity {


    private final String TAG = "SplashActivity";
    MediaPlayer mp;





    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate()");



        /*
         * Set the layout of the current activity to activity_splash
         */

        this.setContentView(R.layout.activity_splash);



















    }

    /*
     * Check that location services are enabled in onResume b/c the activity is not guaranteed to
     * be destroyed when the app opens the location settings screen and as such onCreate() wont necessarily
     * be called again.  If the user does not enable location service and hits the back button to return
     * to our apps splash screen, it will continue with location services disabled and break our app.
     * onResume(), however is "guaranteed" to be called when the user returns from the location settings
     * screen, so we can double check that they actually enabled location services. Also, since onResume
     * is called after onCreate, this can be used to exploit the android lifecycle to only launch the
     * preferences activity after the settings page has been visited and saved the first time, otherwise
     * it continues straight to the splash screen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");

        //create shared preferences object.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //create editor object to be used to change the default SharedPreferences value of first_time
        SharedPreferences.Editor editor = prefs.edit();
        //create media player object.
        mp = MediaPlayer.create(this,R.raw.chomp_long);


        /*
         * Create an ImageView and link it to the declared imageview in activity_splash.xml.
         * Similarly, link the animation_list with the ImageView and create the AnimationDrawable object
         * that cycles through the individual images and displays them for a specified period of time.
         */
        final ImageView splashImageView = (ImageView) findViewById(R.id.tender);

        //Change here for switching back to plate style bite animation
        splashImageView.setBackgroundResource(R.drawable.animation_list_chicken);
        final AnimationDrawable frameAnimation = (AnimationDrawable) splashImageView.getBackground();

        /*
         * Create splashAnimation object from items listed above
         */
        splashAnimation sa = new splashAnimation(splashImageView,mp,frameAnimation);







        /*
         * Check that location services are enabled.  If they are not, request the user to activate them.
         * Otherwise, continue.
         */
        if(!isLocationEnabled()) {
            openEnableLocationDialog();
        }

        /*
         * Check if preferences have been previously set. If it has not, launch the intent to the preferences screen
         * where the user can select the preferences. If it has been set, jump directly to the splash screen.
         *
         */
        else if(!prefs.getBoolean("first_time",false))
        {
            Log.i(TAG, "in first time");

            editor.putBoolean("first_time", true);
            editor.commit();

            Intent i = new Intent(SplashScreenActivity.this, AppPreferences.class);
            i.putExtra("FROM_ACTIVITY","splash");
            this.startActivity(i);
            this.finish();
        }
        else
        {
            // Launch splash service which waits for 4 secondswhile 
            sa.runAnimation();
            Intent splashServiceIntent = new Intent(this, SplashScreenService.class);

            startService(splashServiceIntent);
        }
    }


    /**
     * Checks that location services are enabled by querying the settings.  For SDKs 19 and greater,
     * the constant LOCATION_MODE is compared to the constant LOCATION_MODE_OFF to determine the
     * current status of location services.  For all previous SDKs, the constant LOCATION_PROVIDERS_ALLOWED
     * is checked.  If it is an empty string, no location providers are enabled and location services
     * are not available.
     *
     * @return boolean True if location services are enabled.  False otherwise.
     */
    private boolean isLocationEnabled() {
        int locationMode = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            return !TextUtils.isEmpty(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED));
        }
    }

    /**
     * Creates and displays a simple prompt that asks the user to enable location services and
     * opens the location settings screen when the button is touched.
     */
    private void openEnableLocationDialog() {
        String dialogTitle = "Enable Location Services Prompt";
        String dialogMessage = "Please enable location services.";
        String positiveButton = "OK";

        AlertDialog.Builder enableLocationServicesDialogBuilder = new AlertDialog.Builder(this);
        enableLocationServicesDialogBuilder.setTitle(dialogTitle);
        enableLocationServicesDialogBuilder.setMessage(dialogMessage);
        enableLocationServicesDialogBuilder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableLocationServicesIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableLocationServicesIntent);
            }
        });

        Dialog enableLocationServicesDialog = enableLocationServicesDialogBuilder.create();
        enableLocationServicesDialog.setCanceledOnTouchOutside(false);
        enableLocationServicesDialog.show();

    }

}
