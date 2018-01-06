package com.randomrobotics.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.ui.MainRecipeItemDecoration;
import com.randomrobotics.bakingapp.ui.RecipeAdapter;
import com.randomrobotics.bakingapp.utils.GetRecipeDataTask;
import com.randomrobotics.bakingapp.utils.NetworkUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * The configuration screen for the {@link IngredientsWidget IngredientsWidget} AppWidget.
 */
public class IngredientsWidgetConfigureActivity extends Activity
        implements GetRecipeDataTask.TaskCompleteListener<ArrayList<Recipe>>, RecipeAdapter.RecipeClickHandler {

    public static final String PREFS_NAME = "com.randomrobotics.bakingapp.widget.IngredientsWidget";
    public static final String PREF_RECIPENAME_KEY = "widget_recipe_";
    public static final String PREF_INGREDIENTSLIST_KEY = "widget_ingredientslist_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @BindView(R.id.widget_recipe_recycler)
    RecyclerView recipeRecycler;
    private ArrayList<Recipe> recipeList;

    public IngredientsWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveValuesToPrefs(Context context, int appWidgetId, String recipeName, ArrayList<String> ingredientsList) {
        SavePrefsTask saveTask = new SavePrefsTask();
        saveTask.execute(new SavePrefsTask.SavePrefsTaskParams(context, appWidgetId, recipeName, ingredientsList));
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String getRecipeNameFromPrefs(final Context context, final int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String recipeName = prefs.getString(PREF_RECIPENAME_KEY + appWidgetId, null);
        if (recipeName != null) {
            return recipeName;
        } else {
            return context.getResources().getString(R.string.widget_empty_name);
        }
    }

    /**
     * Get the list of {@link com.randomrobotics.bakingapp.data.Ingredient}s from SharedPreferences
     */
    static ArrayList<String> getIngredientsListFromPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String listJson = prefs.getString(PREF_INGREDIENTSLIST_KEY + appWidgetId, null);
        try {
            Type ingredientType = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            return gson.fromJson(listJson, ingredientType);
        } catch (Exception e) {
            Timber.e("Error reading saved ingredients list from SharedPreferences: :%s", e.toString());
            ArrayList<String> badResult = new ArrayList<>();
            badResult.add("Error");
            return badResult;
        }
    }

    /**
     * Delete the saved values from the SharedPreferences
     */
    static void deleteRecipeValues(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.remove(PREF_RECIPENAME_KEY + appWidgetId);
        prefs.remove(PREF_INGREDIENTSLIST_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.ingredients_widget_configure);
        ButterKnife.bind(this);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        LoadRecipeData();
    }

    /**
     * Load the list of {@link Recipe}s from the web resource in a background task using {@link GetRecipeDataTask}
     */
    private void LoadRecipeData() {
        Timber.d("Loading recipe data for widget");
        if (NetworkUtils.isOnline(this)) {
            GetRecipeDataTask task = new GetRecipeDataTask(this);
            task.execute(NetworkUtils.getRecipeListUrl());
        } else {
            Timber.e("Device is not online");
            Toast.makeText(this, "Device is not online!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the results from the {@link GetRecipeDataTask} via the {@link com.randomrobotics.bakingapp.utils.GetRecipeDataTask.TaskCompleteListener} interface
     */
    @Override
    public void onTaskComplete(ArrayList<Recipe> results) {
        if (results != null) {
            recipeList = results;
            Timber.d("Got %s recipes", results.size());
            DisplayRecipeList();
        } else {
            Timber.e("No results from GetRecipeDataTask");
        }
    }

    /**
     * Display the list of {@link Recipe}s using a {@link RecipeAdapter}
     */
    private void DisplayRecipeList() {
        RecipeAdapter adapter = new RecipeAdapter(this, recipeList, this);
        recipeRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recipeRecycler.setHasFixedSize(true);
        recipeRecycler.addItemDecoration(new MainRecipeItemDecoration((int) getResources().getDimension(R.dimen.widget_config_margin)));
        recipeRecycler.setAdapter(adapter);
    }

    /**
     * User has selected a {@link Recipe} from the list
     * Called via the {@link RecipeAdapter.RecipeClickHandler} interface
     */
    @Override
    public void OnRecipeClicked(Recipe recipe) {
        Timber.d("Recipe %s selected in widget", recipe.getName());
        ArrayList<String> recipeIngredientsList = recipe.getIngredientsListAsStringArrayList();
        saveValuesToPrefs(this, mAppWidgetId, recipe.getName(), recipeIngredientsList);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        IngredientsWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

