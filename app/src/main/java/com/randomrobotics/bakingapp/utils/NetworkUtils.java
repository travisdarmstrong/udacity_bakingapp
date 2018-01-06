package com.randomrobotics.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.randomrobotics.bakingapp.data.Recipe;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Utilities for Network actions
 */

public final class NetworkUtils {

    private static final String RECIPE_LIST_URL = "http://go.udacity.com/android-baking-app-json";

    /**
     * Get the URL for the list of {@link Recipe}s
     */
    public static String getRecipeListUrl() {
        return RECIPE_LIST_URL;
    }

    /**
     * Get the data from the provided web address. Data will be in JSON format
     *
     * @param url The URL of the data
     * @throws IOException In case of error
     */
    static String getResponseFromHttp(String url) throws IOException {
        Timber.v("Getting response from http '%s'", url);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            Timber.d("Got response %s", response.code());
            String result = "";
            return response.body().string();
        } catch (Exception ex) {
            Timber.e("Error getting data from http: %s", ex.toString());
            return "";
        }
    }

    /**
     * Is the device online
     *
     * @return True if the device is online
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo net = cm.getActiveNetworkInfo();
            return net != null && net.isConnectedOrConnecting();
        } catch (Exception ex) {
            Timber.e("Error checking to see if device is online: %s", ex.toString());
            return false;
        }
    }

}
