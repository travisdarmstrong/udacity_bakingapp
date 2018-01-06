package com.randomrobotics.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * {@link Fragment} to display list of {@link Ingredient}s
 */

public class IngredientsFragment extends Fragment {
    public static final String ARGUMENT_RECIPE = "arg-recipe";
    private Recipe recipe;
    @BindView(R.id.ingredients_recycler)
    RecyclerView ingredientsRecycler;
    @BindView(R.id.ingredients_header)
    TextView headerTxt;

    /**
     * Default Constructor
     */
    public IngredientsFragment() {
    }

    /**
     * Create a new instance of the {@link IngredientsFragment} with the supplied arguments
     */
    public static IngredientsFragment newInstance(Recipe recipe) {
        // Create the new Fragment
        IngredientsFragment myfragment = new IngredientsFragment();

        // Add the arguments passed in
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_RECIPE, recipe);
        myfragment.setArguments(args);
        return myfragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        // The arguments must contain a Recipe
        if (args != null && args.containsKey(ARGUMENT_RECIPE)) {
            recipe = args.getParcelable(ARGUMENT_RECIPE);
        } else {
            Timber.e("Arguments did not include a recipe");
        }
    }

    /**
     * Create the view to display the Fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        // Bind the UI elements
        ButterKnife.bind(this, view);
        DisplayIngredients();
        return view;
    }

    /**
     * Display the list of {@link Ingredient}s
     */
    private void DisplayIngredients() {
        Timber.d("Displaying %s ingredients for '%s'", recipe.getIngredients().size(), recipe.getName());
        IngredientAdapter adapter = new IngredientAdapter(recipe.getIngredients());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ingredientsRecycler.setHasFixedSize(true);
        ingredientsRecycler.setLayoutManager(layoutManager);
        ingredientsRecycler.setAdapter(adapter);
        headerTxt.setText(getResources().getString(R.string.ingredients_header, recipe.getName()));
    }

}
