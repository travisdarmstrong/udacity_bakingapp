package com.randomrobotics.bakingapp.ui;

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
import timber.log.Timber;

/**
 * Display the list of {@link Recipe} {@link Step}s in the {@link RecyclerView}
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {
    private Recipe recipe;
    private RecipeStepClickHandler stepClickHandler;
    private int selectedItem;

    /**
     * Create a new {@link RecipeStepAdapter}
     *
     * @param recipe       The {@link Recipe} to get the list of {@link Step}s from
     * @param clickHandler The {@link RecipeStepClickHandler} to handle click events
     * @param selectedItem The selected {@link Step}
     */
    RecipeStepAdapter(Recipe recipe, RecipeStepClickHandler clickHandler, int selectedItem) {
        this.recipe = recipe;
        this.stepClickHandler = clickHandler;
        this.selectedItem = selectedItem;
    }

    /**
     * Create the {@link RecipeStepViewHolder}
     */
    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_step_item, parent, false);
        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        Step step = recipe.getSteps().get(position);
        holder.stepDescText.setText(step.getShortDescription());
        if (position == selectedItem) {
            holder.stepDescText.setBackgroundResource(R.color.steps_selected_background);
        }
    }

    @Override
    public int getItemCount() {
        return recipe.getSteps().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ViewHolder for the {@link Step} item views
     */
    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_step_item_shortdesc)
        TextView stepDescText;

        /**
         * Create the {@link RecipeStepViewHolder}
         */
        RecipeStepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Handle the event when the {@link Step} is clicked on
         */
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Step step = recipe.getSteps().get(pos);
            Timber.i("Recipe step %s clicked on. Load details for '%s'", pos, step.getShortDescription());
            // callback to display the selected step
            stepClickHandler.OnStepClick(pos);
        }
    }

    /**
     * Interface to handle event when the {@link Recipe} {@link Step} is clicked on
     * In phone display, this will start the {@link com.randomrobotics.bakingapp.StepDetailActivity}
     * In tablet display, this will update the {@link android.support.v4.app.Fragment} where the {@link Step} is displayed
     */
    public interface RecipeStepClickHandler {
        void OnStepClick(int stepNumber);
    }
}
