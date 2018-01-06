package com.randomrobotics.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.RecipeStepListActivity;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Display the list of {@link Recipe}s in the {@link RecyclerView}
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyRecipeViewHolder> {
    private ArrayList<Recipe> recipeList;
    private RecipeClickHandler recipeClickHandler;
    private Context context;

    /**
     * Create a new {@link RecipeAdapter} to display the list of {@link Recipe}s
     *
     * @param recipeList         The list of {@link Recipe}s
     * @param recipeClickHandler The {@link RecipeClickHandler} to handle click events
     */
    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList, RecipeClickHandler recipeClickHandler) {
        this.context = context;
        this.recipeList = recipeList;
        this.recipeClickHandler = recipeClickHandler;
    }

    @Override
    public MyRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_recipe_card, parent, false);
        return new MyRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.nameTxt.setText(recipe.getName());
        if (recipe.hasImage()) {
            holder.nameTxt.setBackgroundResource(R.color.main_transparent);
            Picasso.with(context).load(recipe.getImage())
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .into(holder.recipeImage);
            holder.recipeImage.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ViewHolder for the {@link Recipe} item views
     */
    public class MyRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_item_name)
        TextView nameTxt;
        @BindView(R.id.recipe_item_image)
        ImageView recipeImage;

        /**
         * Constructor
         */
        MyRecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Handle event where the {@link Recipe} item is clicked on
         */
        @OnClick(R.id.recipe_item_name)
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Recipe recipe = recipeList.get(pos);
            Timber.v("Recipe '%s' clicked on. Report to activity", recipe.getName());
            recipeClickHandler.OnRecipeClicked(recipe);
        }
    }

    /**
     * Handle event when the {@link Recipe} is clicked on
     * In phone display, this will start {@link RecipeStepListActivity} to display the {@link Recipe} {@link Step}s
     * In tablet display, this will update the {@link android.support.v4.app.Fragment} where the {@link Recipe} {@link Step}s are displayed
     */
    public interface RecipeClickHandler {
        void OnRecipeClicked(Recipe recipe);
    }
}
