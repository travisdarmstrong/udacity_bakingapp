package com.randomrobotics.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;
import com.randomrobotics.bakingapp.ui.StepDetailFragment;

import timber.log.Timber;

/**
 * Activity to display the detailed instructions for the {@link Recipe} {@link Step}
 */
public class StepDetailActivity extends FragmentActivity
        implements StepDetailFragment.StepNavigationListener {
    private final static String FRAGMENT_TAG = "detail-fragment";
    private final static String SAVED_RECIPE = "saved-recipe";
    private final static String SAVED_STEP = "saved-step";
    private Recipe recipe;
    private int stepNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        // Use saved data if available
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_RECIPE) && savedInstanceState.containsKey(SAVED_STEP)) {
            Timber.v("Restoring step detail activity from saved instance state");
            recipe = savedInstanceState.getParcelable(SAVED_RECIPE);
            stepNumber = savedInstanceState.getInt(SAVED_STEP);
        } else {
            // Get data from the calling intent
            Bundle args = getIntent().getExtras();
            if (args != null && args.containsKey(StepDetailFragment.ARGUMENT_RECIPE)) {
                recipe = args.getParcelable(StepDetailFragment.ARGUMENT_RECIPE);
                stepNumber = args.getInt(StepDetailFragment.ARGUMENT_STEPNUMBER);
            } else {
                Timber.d("StepDetailActivity created without passing in a Recipe and Step Number");
                finish();
            }
            // Create the StepDetailFragment and display it
            StepDetailFragment newFragment = StepDetailFragment.newInstance(recipe, stepNumber, false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.detail_container, newFragment, FRAGMENT_TAG).commit();
        }

    }


    /**
     * Handle the request to show the next {@link Step}
     * Called via the {@link com.randomrobotics.bakingapp.ui.StepDetailFragment.StepNavigationListener} interface
     */
    @Override
    public void MoveToNext() {
        Timber.d("StepDetailActivity moving to next step");
        SwapFragment(++stepNumber);
    }

    /**
     * Handle the request to show the previous {@link Step}
     * Called via the {@link com.randomrobotics.bakingapp.ui.StepDetailFragment.StepNavigationListener} interface
     */
    @Override
    public void MoveToPrevious() {
        Timber.d("StepDetailActivity moving to previous step");
        SwapFragment(--stepNumber);
    }

    /**
     * Swap the displayed {@link Step} details in the {@link StepDetailFragment}
     */
    private void SwapFragment(int newStep) {
        Timber.d("Swapping in detail fragment for step %s", newStep);
        StepDetailFragment newFragment = StepDetailFragment.newInstance(recipe, newStep, false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.detail_container, newFragment, FRAGMENT_TAG).commit();
    }

    /**
     * Save the {@link Recipe} and {@link Step} number so it can be re-used
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_RECIPE, recipe);
        outState.putInt(SAVED_STEP, stepNumber);
    }
}
