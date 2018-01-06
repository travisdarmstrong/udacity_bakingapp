package com.randomrobotics.bakingapp.utils;

import android.os.AsyncTask;

import com.randomrobotics.bakingapp.data.Recipe;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Background Task to get {@link Recipe} data from web resource
 */
public class GetRecipeDataTask extends AsyncTask<String, Void, ArrayList<Recipe>> {
    private TaskCompleteListener<ArrayList<Recipe>> listener;

    /**
     * Create a new {@link GetRecipeDataTask}
     *
     * @param listener Get the results via the {@link TaskCompleteListener} interface
     */
    public GetRecipeDataTask(TaskCompleteListener<ArrayList<Recipe>> listener) {
        this.listener = listener;
    }

    /**
     * Perform the background work
     */
    @Override
    protected ArrayList<Recipe> doInBackground(String... args) {
        String url = args[0];

        String data = "";

        // Get the data
        try {
            data = NetworkUtils.getResponseFromHttp(url);
        } catch (IOException ioEx) {
            Timber.e("IOException when getting data from http: %s", ioEx.toString());
        } catch (Exception ex) {
            Timber.e("Exception when getting data from http: %s", ex.toString());
        }

        if (data.isEmpty()) {
            Timber.e("Empty response");
            return null;
        }

        // Parse the JSON string into a list of Recipes
        ArrayList<Recipe> recipeList = null;
        try {
            recipeList = JSONUtils.parseJSONstring(data);
        } catch (JSONException jsonEx) {
            Timber.e("JSONException when parsing data: %s", jsonEx.toString());
        } catch (Exception ex) {
            Timber.e("Exception when parsing data: %s", ex.toString());
        }

        return recipeList;
    }

    /**
     * When the task is finished, call the {@link TaskCompleteListener}
     */
    @Override
    protected void onPostExecute(ArrayList<Recipe> recipes) {
        super.onPostExecute(recipes);
        Timber.d("GetRecipeDataTask finished");
        listener.onTaskComplete(recipes);
    }

    /**
     * Get the results when the {@link GetRecipeDataTask} is complete
     */
    public interface TaskCompleteListener<T> {
        /**
         * Called when the {@link GetRecipeDataTask} completes the operation
         */
        void onTaskComplete(T results);
    }
}