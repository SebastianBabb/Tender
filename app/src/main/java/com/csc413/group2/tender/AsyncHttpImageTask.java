package com.csc413.group2.tender;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Downloads an image from a given URL and displays it in a given imageview.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 07/09/2015
 */
public class AsyncHttpImageTask extends AsyncTask<URL, Integer, Bitmap> {
    private final WeakReference<ImageView> restaurantImageViewWeakReference;

    /**
     * Constructor.
     * @param imageView The ImageView object in which the downloaded image will be displayed.
     */
    public AsyncHttpImageTask(ImageView imageView) {
        super();
        restaurantImageViewWeakReference = new WeakReference<>(imageView);
    }

    /*
     * Executed prior performing background operations.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Performs all operations on the background thread.
     * @param urls An array containing urls to images.
     *
     */
    @Override
    protected Bitmap doInBackground(URL... urls) {
        return downloadBitmap(urls[0]);
    }

    /**
     * Performed after the background thread is done.  Updates the imageview with the
     * downloaded image.
     * @param bitmap The bitmap image to display in the imageview.
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // Check that the bitmap was successfully downloaded and converted.
        if(isCancelled()) {
            bitmap = null;
        }
        // Check that the weak reference to the imageview set in the constructor is not null.
        if(restaurantImageViewWeakReference != null) {
            // Retrieve the imageview and double check it and the bitmap and then display the image.
            ImageView imageView = restaurantImageViewWeakReference.get();
            if(imageView != null) {
                if(bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    /* If the bitmap is not there, set place holder (if you want one other than
                     * the one set in the imageview's xml.
                     */
//                    Drawable placeholder = imageView.getContext().getDrawable(R.mipmap.default_image);
//                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }

    /**
     * Downloads an image from a given url.
     * @param url The url to the image resource.
     * @return The downloaded image as a bitmap.
     */
    private Bitmap downloadBitmap(URL url) {
        // Create a bitmap object to return.
        Bitmap bitmap = null;
        /*
         * Open a http connection to the image resource,
         * retrieve the image stream, and then convert it
         * using the BitmapFactory.
         */
        try {
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

            bitmap = BitmapFactory.decodeStream(is);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Return the downloaded image as a bitmap.
        return bitmap;
    }

}
