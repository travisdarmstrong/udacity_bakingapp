package com.randomrobotics.bakingapp.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Utility class to save the homescreen widget data in SharedPreferences on a background task to keep it off the main UI thread
 */

public class SavePrefsTask extends AsyncTask<SavePrefsTask.SavePrefsTaskParams, Void, Void> {
    /**
     * Parameters for the {@link SavePrefsTask}
     */
    static class SavePrefsTaskParams {
        Context context;
        int appWidgetId;
        String recipeName;
        ArrayList<String> ingredientsList;

        /**
         * Create a new set of parameters for the {@link SavePrefsTask}
         *
         * @param context         Application Context
         * @param appWidgetId     The ID of the app widget
         * @param recipeName      Name of the {@link com.randomrobotics.bakingapp.data.Recipe}
         * @param ingredientsList List of {@link com.randomrobotics.bakingapp.data.Ingredient}s
         */
        SavePrefsTaskParams(Context context, int appWidgetId, String recipeName, ArrayList<String> ingredientsList) {
            this.context = context;
            this.appWidgetId = appWidgetId;
            this.recipeName = recipeName;
            this.ingredientsList = ingredientsList;
        }
    }

    /**
     * Perform the background work of saving the SharedPreferences data
     */
    @Override
    protected Void doInBackground(SavePrefsTaskParams... _params) {
        SavePrefsTaskParams params = _params[0];
        SharedPreferences.Editor prefs = params.context.getSharedPreferences(IngredientsWidgetConfigureActivity.PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putString(IngredientsWidgetConfigureActivity.PREF_RECIPENAME_KEY + params.appWidgetId, params.recipeName);
        // Use GSON to serialize the ingredientslist
        Gson gson = new Gson();
        String listJson = gson.toJson(params.ingredientsList);
        prefs.putString(IngredientsWidgetConfigureActivity.PREF_INGREDIENTSLIST_KEY + params.appWidgetId, listJson);
        prefs.apply();
        return null;
    }
}
