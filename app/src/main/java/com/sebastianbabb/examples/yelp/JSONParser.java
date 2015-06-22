package com.sebastianbabb.examples.yelp;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * A class for parsing JSON strings.
 * Created by sebastian on 6/18/15.
 * @author Sebastian Babb
 * @version 1.0
 *          Date: 06/18/2015
 */
public class JSONParser {
    // Log TAG for monitoring in the system logs.
    private static final String TAG = "JSONParser";
    private String mJsonString;
    private JSONObject mJsonObject;

    public JSONParser(String json) throws JSONException {
        // Log the location.
        Log.i(TAG, "In constructor, argument passed: " + json);

        /*
         * Store the JSON string as a member and use it to build a JSONObject.
         */
        this.mJsonString = json;
        this.mJsonObject = new JSONObject(this.mJsonString);
    }

    /**
     * Returns a string values each on a separated line.
     *
     * @param arrayName The name of the array containing the tags.
     * @param tag       The name of the tag to return.
     * @return A String a of newline separated tag values.
     * @throws JSONException
     */
    public String getTag(String arrayName, String tag) throws JSONException {
        // Create a new string builder object.
        StringBuilder result = new StringBuilder();

        /*
         * Create a JSONArray from the JSONObject and loop through it
         * appending each tag value to the string builder with a newline.
         */
        JSONArray jsonArray = this.mJsonObject.getJSONArray(arrayName);
        for (int i = 0; i < jsonArray.length(); i++) {
            // Append the tag value to the end of the string and and a newline.
            result.append(jsonArray.getJSONObject(i).getString(tag) + "\n");
        }

        /*
         * Return the constructed string from the string builder.
         */
        return result.toString();
    }
}
