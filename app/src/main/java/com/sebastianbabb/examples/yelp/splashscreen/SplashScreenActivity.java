package com.sebastianbabb.examples.yelp.splashscreen;




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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.view.animation.Animation.AnimationListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.graphics.drawable.AnimationDrawable;
import com.sebastianbabb.examples.yelp.R;



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

        //create new mp and wire it to audio file

        mp = MediaPlayer.create(this,R.raw.chomp_long);


        // Tell the activity which layout to display.
        setContentView(R.layout.activity_splash);



        final ImageView splashImageView = (ImageView) findViewById(R.id.tender);
        splashImageView.setBackgroundResource(R.drawable.animation_list);
        final AnimationDrawable frameAnimation = (AnimationDrawable)splashImageView.getBackground();



        splashImageView.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
                mp.start();
            }
        });


    }

    /*
     * Check that location services are enabled in onResume b/c the activity is not guaranteed to
     * be destroyed when the app opens the location settings screen and as such onCreate() wont necessarily
     * be called again.  If the user does not enable location service and hits the back button to return
     * to our apps splash screen, it will continue with location services disabled and break our app.
     * onResume(), however is "guaranteed" to be called when the user returns from the location settings
     * screen, so we can double check that they actually enabled location services.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        /*
         * Check that location services are enabled.  If they are not, request the user to activate them.
         * Otherwise, continue.
         */
        if(!isLocationEnabled()) {
            openEnableLocationDialog();
        } else {
            // Launch splash service.
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
