package com.csc413.group2.tender.splashscreen;


import android.widget.ImageView;
import android.media.MediaPlayer;
import android.graphics.drawable.AnimationDrawable;



/**
 * Created by Ace on 7/19/15.
 */
public class splashAnimation {

    //Declares the initial objects of each type of the animation
    ImageView splashImageView;
    MediaPlayer mp;
    AnimationDrawable frameAnimation;

    /** Default constructor
     *
     *
     * @param splashImageView The imageview object passed from the calling function which is setup in the splash screen.
     * @param mp Media player object
     * @param frameAnimation AnimationDrawable object
     */

    public splashAnimation(ImageView splashImageView, MediaPlayer mp,AnimationDrawable frameAnimation) {

        this.splashImageView = splashImageView; //variable initializations
        this.mp = mp;
        this.frameAnimation = frameAnimation;

    }

    /*
    Method that is called to run the animation using the objects listed above.
     */
    public void runAnimation() {


        splashImageView.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
                //mp.start();
            }
        });

    }
}
