package com.csc413.group2.tender;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.csc413.group2.tender.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A custom array adapter for populating the restaurant search results list in
 * YelpRequestActivity using the list_item layout in res/layout/.
 *
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 07/09/2015
 */
public class ListItemAdapter extends ArrayAdapter<Restaurant> {
    private final String TAG = "ListItemAdapter";

    private final Context context;
    private final Restaurant[] restaurants;

    /**
     * Constructor.
     * @param context The context in which the object is being instantiated (this).
     * @param restaurants An array of restaurant objects.
     */
    public ListItemAdapter(Context context, Restaurant[] restaurants) {
        // Call super().  The second argument is the resource id. -1 tells it we are using a custom view.
        super(context, -1, restaurants);
        this.context = context;
        this.restaurants = restaurants;
    }

    /**
     * Builds each item view in the list view.
     * @param position The position in the list starting at 0.
     * @param view The view to convert.
     * @param viewGroup The parent view.
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        /*
         * Create a layout inflater and inflate the custom list_item view from res/layout/.
         * This is the layout you designed in xml for the items in the list_view.
         */
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ListItemView = inflater.inflate(R.layout.list_item, viewGroup, false);

        /*
         * From the inflated list_item view, retrieve and wire up the view widgets.  Retreive them
         * using the id that you assigned to them when creating the xml layout.
         */
        ImageView restaurantImage = (ImageView)ListItemView.findViewById(R.id.restaurant_image);
        ImageView restaurantRating = (ImageView)ListItemView.findViewById(R.id.restaurant_rating_image);
        TextView restaurantName = (TextView)ListItemView.findViewById(R.id.restaurant_name);
        TextView restaurantStreet = (TextView)ListItemView.findViewById(R.id.restaurant_street);
        TextView restaurantCity = (TextView)ListItemView.findViewById(R.id.restaurant_city_state_zip);
        TextView restaurantHours = (TextView)ListItemView.findViewById(R.id.restaurant_hours);
        TextView restaurantStatus = (TextView)ListItemView.findViewById(R.id.restaurant_status);

        /*
         * Using the AsyncHttpImageTask class, retrieve all image resources for the list_item view.
         * As of this moment, there are only two - star rating and restaurant image from yelp.  Check
         * that the urls exist before calling the async task.
         */

        // Retrieve star rating image from yelp.
        if(restaurants[position].getRatingImgUrlSmall() != null) {
            try {
                Log.i(TAG, "Rating Image URL: " + restaurants[position].getRatingImgUrlSmall());
                // Pass the image url and download it in the background.
                new AsyncHttpImageTask(restaurantRating).execute(new URL(restaurants[position].getRatingImgUrlSmall())); // CHANGE RESTAURANT OBJECT URL TO FROM STRING TO URL TYPE.
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Rating Image URL: null");
        }

        // Retrieve restaurant image from yelp.
        if(restaurants[position].getImageUrl() != null) {
            try {
                Log.i(TAG, "Restaurant Image URL: " + restaurants[position].getImageUrl());
                // Pass the image url and download it in the background.
                new AsyncHttpImageTask(restaurantImage).execute(new URL(restaurants[position].getImageUrl())); // CHANGE RESTAURANT OBJECT URL TO FROM STRING TO URL TYPE.
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // Set the text attributes of the list_item view.
        restaurantName.setText(restaurants[position].getName());
        restaurantStreet.setText(restaurants[position].getLocation().getAddress()[0]);
        restaurantCity.setText(restaurants[position].getLocation().getCity());
        restaurantCity.setText(restaurants[position].getLocation().getStateCode());
        restaurantCity.setText(restaurants[position].getLocation().getPostalCode());

        /*
         * get hours and status need to be implemented.
         */
//        restaurantHours.setText(restaurants[position].getHours()
        /*
         * if the restaurant is open, set color to green.  otherwise red.
         */
//        restaurantStatus.setText(restaurants[position].getStatus());

        // Return the built list_item view.
        return ListItemView;
    }
}

