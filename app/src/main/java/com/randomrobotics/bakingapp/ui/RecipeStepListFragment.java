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
import com.randomrobotics.bakingapp.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * {@link Fragment} to display list of {@link Recipe} {@link Step}s
 */

public class RecipeStepListFragment extends Fragment
        implements RecipeStepAdapter.RecipeStepClickHandler {
    private static final String ARGUMENT_RECIPE = "arg-recipe";
    private static final String SAVED_SELECTED_STEP = "saved-step";
    private Recipe recipe;
    @BindView(R.id.recipe_steps_recycler)
    RecyclerView stepsRecycler;
    @BindView(R.id.recipe_steps_ingredients)
    TextView stepsIngredients;
    private OnIngredientsClickHandler ingredientsClickHandler;
    private OnStepClickHandler stepClickHandler;
    private int lastSelectedStep = -1;

    /**
     * Default Constructor
     */
    public RecipeStepListFragment() {
    }

    /**
     * Create a new {@link RecipeStepListFragment} with the supplied {@link Recipe}
     *
     * @param recipe The recipe to get the {@link Step}s from
     */
    public static RecipeStepListFragment newInstance(Recipe recipe) {
        RecipeStepListFragment newFragment = new RecipeStepListFragment();
        // Put the Recipe in the arguments
        Bundle args = new Bundle();
        args.putParcelable(RecipeStepListFragment.ARGUMENT_RECIPE, recipe);
        newFragment.setArguments(args);
        return newFragment;
    }

    /**
     * Called when the {@link RecipeStepListFragment} is created
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // See if there is saved data holding the step that should be highlighted
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_SELECTED_STEP)) {
            lastSelectedStep = savedInstanceState.getInt(SAVED_SELECTED_STEP);
        }

        // Verify the calling activity implements OnIngredientsClickHandler
        try {
            ingredientsClickHandler = (OnIngredientsClickHandler) getActivity();
        } catch (Exception e) {
            Timber.e("%s must implement OnIngredientsClickHandler interface", getActivity().getLocalClassName());
            throw new ClassCastException(getActivity().toString() + " must implement OnIngredientsClickHandler");
        }
        // Verify the calling activity implements OnStepClickHandler
        try {
            stepClickHandler = (OnStepClickHandler) getActivity();
        } catch (Exception e) {
            Timber.e("%s must implement OnStepClickHandler interface", getActivity().getLocalClassName());
            throw new ClassCastException(getActivity().toString() + " must implement OnStepClickHandler interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipesteplist, container, false);
        // Bind the UI elements
        ButterKnife.bind(this, view);
        // Get the Recipe from the calling arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGUMENT_RECIPE)) {
            recipe = args.getParcelable(ARGUMENT_RECIPE);
        }
        // Display the Recipe information
        DisplayRecipeInfo();
        // Highlight the selected step (only valid when in two-pane tablet display mode)
        if (lastSelectedStep > -1) HighlightStep(lastSelectedStep);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the highlighted step
        outState.putInt(SAVED_SELECTED_STEP, lastSelectedStep);
    }

    /**
     * Display the {@link Recipe} information
     */
    private void DisplayRecipeInfo() {
        Timber.d("Displaying recipe steps for '%s'", recipe.getName());
        stepsRecycler.setHasFixedSize(true);
        RecipeStepAdapter stepAdapter = new RecipeStepAdapter(recipe, this, lastSelectedStep);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        stepsRecycler.setLayoutManager(layoutManager);
        stepsRecycler.setAdapter(stepAdapter);
    }

    /**
     * Highlight the specified {@link Step} (only done on two-pane tablet display)
     */
    public void HighlightStep(int stepNum) {
        RecipeStepAdapter.RecipeStepViewHolder holder = (RecipeStepAdapter.RecipeStepViewHolder)
                stepsRecycler.findViewHolderForAdapterPosition(lastSelectedStep);
        // Reset the background color on the previously selected Step
        if (holder != null)
            holder.stepDescText.setBackgroundResource(R.color.steps_background);
        // Highlight the newly selected step
        holder = (RecipeStepAdapter.RecipeStepViewHolder) stepsRecycler.findViewHolderForAdapterPosition(stepNum);
        if (holder != null)
            holder.stepDescText.setBackgroundResource(R.color.steps_selected_background);
        // Update the saved step number
        lastSelectedStep = stepNum;
    }

    /**
     * Handle the click event from the Ingredients TextView button at the top of the display
     */
    @OnClick(R.id.recipe_steps_ingredients)
    public void ingredientsClick(View view) {
        Timber.d("Ingredients clicked");
        // report to the calling activity so it will handle it propertly
        ingredientsClickHandler.OnIngredientsClick();
    }

    /**
     * Handle the click event from the {@link Step} item. This comes from the
     * {@link RecipeStepAdapter} because this fragment implemented the
     * {@link RecipeStepAdapter.RecipeStepClickHandler} interface
     * Call the method on the calling activity (that has implemented the
     * {@link OnStepClickHandler} interface
     */
    @Override
    public void OnStepClick(int stepNumber) {
        // display the selected step
        Timber.d("Step %s requested", stepNumber);
        stepClickHandler.OnStepClick(stepNumber);
    }

    /**
     * Report that the user has clicked on the Ingredients button
     * In phone display, this will start a {@link com.randomrobotics.bakingapp.IngredientsActivity} to show the list of {@link com.randomrobotics.bakingapp.data.Ingredient}s
     * In tablet display, this will update the {@link Fragment} to show the list of {@link com.randomrobotics.bakingapp.data.Ingredient}s
     */
    public interface OnIngredientsClickHandler {
        void OnIngredientsClick();
    }

    /**
     * Report that the user has clicked on a {@link Step}
     * In phone display, this will start a {@link com.randomrobotics.bakingapp.StepDetailActivity} to display the {@link Step} details
     * In tablet display, this will update the {@link Fragment} to show the {@link Step} details
     */
    public interface OnStepClickHandler {
        void OnStepClick(int stepNumber);
    }

}
