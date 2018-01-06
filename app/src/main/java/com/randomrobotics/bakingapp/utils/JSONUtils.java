package com.randomrobotics.bakingapp.utils;

import com.randomrobotics.bakingapp.data.Ingredient;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Utilities for parsing JSON data
 */

final class JSONUtils {

    private static final String JSON_RECIPE_ID = "id";
    private static final String JSON_RECIPE_NAME = "name";
    private static final String JSON_RECIPE_SERVINGS = "servings";
    private static final String JSON_RECIPE_IMAGE = "image";
    private static final String JSON_RECIPE_INGREDIENTS = "ingredients";
    private static final String JSON_RECIPE_STEPS = "steps";

    private static final String JSON_INGREDIENT_QUANTITY = "quantity";
    private static final String JSON_INGREDIENT_MEASURE = "measure";
    private static final String JSON_INGREDIENT_NAME = "ingredient";

    private static final String JSON_STEP_SHORTDESC = "shortDescription";
    private static final String JSON_STEP_DESCRIPTION = "description";
    private static final String JSON_STEP_THUMBNAIL = "thumbnailURL";
    private static final String JSON_STEP_VIDEO = "videoURL";

    /**
     * Parse the string into a list of {@link Recipe} objects
     *
     * @param jsonString The full string returned by the web resource
     * @return A list of {@link Recipe}s
     * @throws JSONException in case of parsing error
     */
    static ArrayList<Recipe> parseJSONstring(String jsonString) throws JSONException {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        Timber.v("Parsing json data");
        JSONArray jsonRecipes = new JSONArray(jsonString);
        Timber.v("Got %s recipes", jsonRecipes.length());
        for (int r = 0; r < jsonRecipes.length(); r++) {

            JSONObject recipe = jsonRecipes.getJSONObject(r);
            String id = recipe.getString(JSON_RECIPE_ID);
            String recipeName = recipe.getString(JSON_RECIPE_NAME);
            String servings = recipe.getString(JSON_RECIPE_SERVINGS);
            String image = recipe.getString(JSON_RECIPE_IMAGE);

            Timber.v("Parsing Recipe for '%s'", recipeName);

            // Get the ingredients
            ArrayList<Ingredient> ingredientsList = new ArrayList<>();
            JSONArray ingredients = recipe.getJSONArray(JSON_RECIPE_INGREDIENTS);
            for (int i = 0; i < ingredients.length(); i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);
                String quantity = ingredient.getString(JSON_INGREDIENT_QUANTITY);
                String measure = ingredient.getString(JSON_INGREDIENT_MEASURE);
                String ingredientName = ingredient.getString(JSON_INGREDIENT_NAME);
                Ingredient newIngredient = new Ingredient(ingredientName, quantity, measure);
                ingredientsList.add(newIngredient);
            }

            // Get the steps
            ArrayList<Step> stepList = new ArrayList<>();
            JSONArray steps = recipe.getJSONArray(JSON_RECIPE_STEPS);
            for (int s = 0; s < steps.length(); s++) {
                JSONObject step = steps.getJSONObject(s);
                String shortDescription = step.getString(JSON_STEP_SHORTDESC);
                String description = step.getString(JSON_STEP_DESCRIPTION);
                String thumbnail = step.getString(JSON_STEP_THUMBNAIL);
                String video = step.getString(JSON_STEP_VIDEO);
                Step newStep = new Step(shortDescription, description, thumbnail, video);
                stepList.add(newStep);
            }
            // Create the new recipe and add it to the list
            Recipe newRecipe = new Recipe(id, recipeName, servings, image, ingredientsList, stepList);
            recipeList.add(newRecipe);
        }
        return recipeList;
    }
}
