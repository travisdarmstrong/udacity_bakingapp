package com.randomrobotics.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.ui.IngredientsFragment;
import com.randomrobotics.bakingapp.data.Ingredient;

import timber.log.Timber;

/**
 * Activity to display a {@link android.support.v4.app.Fragment} with a list of {@link Ingredient}s
 */
public class IngredientsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(IngredientsFragment.ARGUMENT_RECIPE)) {
            Timber.d("creating ingredients fragment");
            Recipe recipe = args.getParcelable(IngredientsFragment.ARGUMENT_RECIPE);
            // Create a new IngredientsFragment with the Recipe that was passed in
            IngredientsFragment fragment = IngredientsFragment.newInstance(recipe);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.ingredients_container, fragment).commit();
        } else {
            Timber.e("IngredientsActivity was called without including a Recipe!");
        }
    }
}
