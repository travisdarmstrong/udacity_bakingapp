package com.randomrobotics.bakingapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.data.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display list of {@link Ingredient}s in the {@link RecyclerView}
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private ArrayList<Ingredient> ingredients;

    /**
     * Create a new {@link IngredientAdapter}
     *
     * @param ingredients list of {@link Ingredient}s
     */
    IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Create the {@link IngredientViewHolder}
     */
    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    /**
     * Bind the {@link IngredientViewHolder} to the position in the list of {@link Ingredient}s
     */
    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientTxt.setText(ingredient.toString());
    }

    /**
     * Get the number of {@link Ingredient}s in the list
     */
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ViewHolder for the {@link Ingredient} item views
     */
    class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_description)
        TextView ingredientTxt;

        /**
         * Constructor for {@link IngredientViewHolder}
         */
        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
