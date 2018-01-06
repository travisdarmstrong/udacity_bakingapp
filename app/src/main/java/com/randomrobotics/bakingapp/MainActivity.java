package com.randomrobotics.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;
import com.randomrobotics.bakingapp.ui.MainRecipeItemDecoration;
import com.randomrobotics.bakingapp.ui.RecipeAdapter;
import com.randomrobotics.bakingapp.ui.RecipeAdapter.RecipeClickHandler;
import com.randomrobotics.bakingapp.utils.GetRecipeDataTask;
import com.randomrobotics.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Activity to show list of {@link Recipe} items, pulled from a web resource
 */
public class MainActivity extends AppCompatActivity
        implements GetRecipeDataTask.TaskCompleteListener<ArrayList<Recipe>>, RecipeAdapter.RecipeClickHandler {
    private ArrayList<Recipe> recipeList;
    private String SAVED_RECIPE_DATA = "saved-recipe-data";
    @BindView(R.id.main_recyclerview)
    RecyclerView mainRecycler;
    @BindView(R.id.main_errortext)
    TextView errorText;
    @BindView(R.id.main_progressbar)
    ProgressBar mainProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Set up the main UI
        setupUI();

        // See if there is saved data
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_RECIPE_DATA)) {
            recipeList = savedInstanceState.getParcelableArrayList(SAVED_RECIPE_DATA);
            if (recipeList != null) {
                Timber.d("Loading saved instance state with %s recipes", recipeList.size());
                DisplayRecipeData();
            }
        } else {
            // Get data from web
            LoadRecipeData();
        }
    }

    /**
     * Set up the main UI elements
     */
    private void setupUI() {
        // Bind the UI elements
        ButterKnife.bind(this);
        // The layout xml file indicates how many columns to display
        int numColumns = getResources().getInteger(R.integer.layout_num_columns);
        if (numColumns > 1) {
            // grid layout
            GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
            mainRecycler.setLayoutManager(layoutManager);
        } else {
            // linear layout
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mainRecycler.setLayoutManager(layoutManager);
        }
        mainRecycler.setHasFixedSize(true);
        mainRecycler.addItemDecoration(new MainRecipeItemDecoration((int) getResources().getDimension(R.dimen.card_margin_vertical)));
    }

    /**
     * Display the {@link Recipe} data using a {@link RecipeAdapter}
     */
    private void DisplayRecipeData() {
        setDataView();
        RecipeAdapter adapter = new RecipeAdapter(this, recipeList, this);
        mainRecycler.setAdapter(adapter);
    }

    /**
     * Load {@link Recipe} data from the web resource. This could take time so it is performed from
     * a background task using {@link GetRecipeDataTask}
     */
    private void LoadRecipeData() {
        Timber.d("Loading recipe data");
        if (NetworkUtils.isOnline(this)) {
            setLoadingView();
            GetRecipeDataTask task = new GetRecipeDataTask(this);
            task.execute(NetworkUtils.getRecipeListUrl());
        } else {
            Timber.e("Device is not online");
            setErrorView();
        }
    }

    /**
     * Called when the {@link GetRecipeDataTask} is complete
     */
    @Override
    public void onTaskComplete(ArrayList<Recipe> results) {
        Timber.d("GetRecipeTask completed. Returned data to main activity");
        recipeList = results;
        if (results != null)
            Timber.d("%s recipes", recipeList.size());
        DisplayRecipeData();
    }

    /**
     * Save the recipe data so it doesn't have to be re-loaded
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.v("Saving recipe list with %s recipes", recipeList.size());
        outState.putParcelableArrayList(SAVED_RECIPE_DATA, recipeList);
    }

    /**
     * Set the display to indicate data is being loaded in {@link GetRecipeDataTask}
     */
    private void setLoadingView() {
        mainRecycler.setVisibility(View.INVISIBLE);
        mainProgBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    /**
     * Set the display to show {@link Recipe} data
     */
    private void setDataView() {
        mainRecycler.setVisibility(View.VISIBLE);
        mainProgBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    /**
     * Set the display to show an error has occurred
     */
    private void setErrorView() {
        mainRecycler.setVisibility(View.INVISIBLE);
        mainProgBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
    }

    /**
     * When a {@link Recipe} is clicked on, start a {@link RecipeStepListActivity} to show the list of {@link Step}s
     * This is called via the {@link RecipeClickHandler} interface
     *
     * @param recipe The selected {@link Recipe}
     */
    @Override
    public void OnRecipeClicked(Recipe recipe) {
        Intent recipeStepsIntent = new Intent(this, RecipeStepListActivity.class);
        recipeStepsIntent.putExtra(RecipeStepListActivity.EXTRA_RECIPE, recipe);
        startActivity(recipeStepsIntent);
    }
}
