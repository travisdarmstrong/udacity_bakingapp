package com.randomrobotics.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.randomrobotics.bakingapp.data.Ingredient;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;
import com.randomrobotics.bakingapp.ui.IngredientsFragment;
import com.randomrobotics.bakingapp.ui.RecipeStepListFragment;
import com.randomrobotics.bakingapp.ui.StepDetailFragment;

import butterknife.BindBool;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Activity to display a list of {@link Recipe} {@link Step}s
 */

public class RecipeStepListActivity extends AppCompatActivity
        implements StepDetailFragment.StepNavigationListener,
        RecipeStepListFragment.OnIngredientsClickHandler,
        RecipeStepListFragment.OnStepClickHandler {
    public static final String EXTRA_RECIPE = "extra-recipe";
    private static final String SAVED_RECIPE_DATA = "saved-recipe-data";
    private static final String SAVED_STEPNUM = "saved-step-number";
    private static final String STEP_FRAGMENT_TAG = "step-list-tag";
    private static final String DETAIL_FRAGMENT_TAG = "detail-tag";
    private Recipe recipe;
    // Bind to boolean resource in layouts.xml
    @BindBool(R.bool.two_pane_layout)
    boolean twoPane;
    private int displayedStep = 0;
    private RecipeStepListFragment listFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipesteplist);
        // Bind the UI elements
        ButterKnife.bind(this);

        StepDetailFragment detailFragment;
        // see if there is saved data
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_RECIPE_DATA)) {
            Timber.v("Using saved data");
            recipe = savedInstanceState.getParcelable(SAVED_RECIPE_DATA);
            displayedStep = savedInstanceState.getInt(SAVED_STEPNUM);
            listFragment = (RecipeStepListFragment) getSupportFragmentManager().findFragmentByTag(STEP_FRAGMENT_TAG);
        } else { // New
            // get data from the intent. Must contain a Recipe
            Intent callingIntent = getIntent();
            if (callingIntent.hasExtra(EXTRA_RECIPE)) {
                recipe = callingIntent.getParcelableExtra(EXTRA_RECIPE);
                Timber.i("Recipe Step List Activity created for '%s'", recipe.getName());
            } else {
                Timber.e("Calling intent does not contain the recipe EXTRA that is required");
                finish();
            }
            // Create a new instance of the RecipeStepListFragment and display it
            listFragment = RecipeStepListFragment.newInstance(recipe);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.step_list_container, listFragment, STEP_FRAGMENT_TAG).commit();
            // If tablet display, show the Detail Fragment
            if (twoPane) {
                Timber.d("Setting up two-pane display");
                detailFragment = StepDetailFragment.newInstance(recipe, displayedStep, true);
                fragmentManager.beginTransaction().replace(R.id.twopane_detail_container, detailFragment, DETAIL_FRAGMENT_TAG).commit();
            }
        }
        // Put recipe name in the title bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            setTitle(recipe.getName());
    }

    /**
     * Save {@link Recipe} so it doesn't have to be re-loaded
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.v("saving recipe data");
        outState.putParcelable(SAVED_RECIPE_DATA, recipe);
        outState.putInt(SAVED_STEPNUM, displayedStep);
    }

    /**
     * Display the next {@link Step} in the {@link Recipe}
     * Called via the {@link com.randomrobotics.bakingapp.ui.StepDetailFragment.StepNavigationListener} interface
     */
    @Override
    public void MoveToNext() {
        Timber.d("Moving to next step");
        SwapFragment(++displayedStep);
    }

    /**
     * Display the previous {@link Step} in the {@link Recipe}
     */
    @Override
    public void MoveToPrevious() {
        Timber.d("Moving to previous step");
        SwapFragment(--displayedStep);
    }

    /**
     * Swap out the displayed {@link Recipe} in the Detail Fragment
     */
    private void SwapFragment(int newStep) {
        Timber.d("Swapping in detail fragment for step %s", newStep);
        HighlightStep(newStep);
        StepDetailFragment newFragment = StepDetailFragment.newInstance(recipe, newStep, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.twopane_detail_container, newFragment, DETAIL_FRAGMENT_TAG).commit();
    }

    /**
     * Highlight the selected {@link Step}
     * Call HighlightStep in the displayed {@link RecipeStepListFragment}
     */
    private void HighlightStep(int step) {
        listFragment.HighlightStep(step);
    }

    /**
     * When user clicks on the Ingredients TextView, display the list of {@link Ingredient}s
     * Called via the {@link com.randomrobotics.bakingapp.ui.RecipeStepListFragment.OnIngredientsClickHandler} interface
     * On phone display, this will create a {@link IngredientsActivity} and display it
     * On tablet display, this will show an {@link IngredientsFragment} in the detail pane
     */
    @Override
    public void OnIngredientsClick() {
        Timber.d("Ingredients clicked. Show ingredients activity or swap the fragment");
        if (twoPane) {
            // replace the detail fragment with ingredients
            IngredientsFragment newFragment = IngredientsFragment.newInstance(recipe);
            getSupportFragmentManager().beginTransaction().replace(R.id.twopane_detail_container, newFragment, DETAIL_FRAGMENT_TAG).commit();
            HighlightStep(-1);
        } else {
            // start an activity to display the ingredients fragment
            Intent ingredientsIntent = new Intent(this, IngredientsActivity.class);
            ingredientsIntent.putExtra(IngredientsFragment.ARGUMENT_RECIPE, recipe);
            startActivity(ingredientsIntent);
        }
    }

    /**
     * When user clicks on a {@link Recipe} {@link Step}, show the details for the {@link Step}
     * Called via the {@link com.randomrobotics.bakingapp.ui.RecipeStepListFragment.OnStepClickHandler} interface
     * On phone display, this will create a {@link StepDetailActivity} and display it
     * On tablet display, this will show a {@link StepDetailFragment} in the detail pane
     */
    @Override
    public void OnStepClick(int stepNumber) {
        Timber.d("Show step %s", stepNumber);
        if (twoPane) {
            // replace the detail fragment
            SwapFragment(stepNumber);
        } else {
            // start the activity
            Intent detailIntent = new Intent(this, StepDetailActivity.class);
            detailIntent.putExtra(StepDetailFragment.ARGUMENT_RECIPE, recipe);
            detailIntent.putExtra(StepDetailFragment.ARGUMENT_STEPNUMBER, stepNumber);
            startActivity(detailIntent);
        }
    }
}
