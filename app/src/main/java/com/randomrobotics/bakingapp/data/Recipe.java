package com.randomrobotics.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Recipe object
 */
public class Recipe implements Parcelable {
    private String name;
    private String servings;
    private String image;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    /**
     * Get the {@link Recipe} name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of servings the {@link Recipe} will make
     */
    public String getServings() {
        return servings;
    }

    /**
     * Get the image web resource of the {@link Recipe}
     */
    public String getImage() {
        return image;
    }

    /**
     * Return TRUE if the Recipe has an image resource
     */
    public boolean hasImage() {

        return (!(image.isEmpty() || image.equals("")));
    }

    /**
     * Get the list of {@link Ingredient}s needed to make the {@link Recipe}
     */
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Get the list of {@link Step}s to make the {@link Recipe}
     */
    public ArrayList<Step> getSteps() {
        return steps;
    }

    /**
     * Create a {@link Recipe} object
     *
     * @param name        Name of the recipe
     * @param servings    Number of servings the recipe will make
     * @param image       Image web resource
     * @param ingredients List of {@link Ingredient}s
     * @param steps       List of {@link Step}s
     */
    public Recipe(String name, String servings, String image, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    /**
     * Generate a string that describes the {@link Recipe}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Recipe Name: %s", name)).append("\r\n");
        sb.append(String.format("Serves %s", servings)).append("\r\n");
        if (!image.isEmpty())
            sb.append(String.format("Image: %s", image)).append("\r\n");
        sb.append(String.format("%s Ingredients", ingredients.size())).append("\r\n");
        sb.append(getIngredientsListAsString());
        sb.append(String.format("%s Steps", steps.size())).append("\r\n");
        for (int i = 0; i < steps.size(); i++) {
            sb.append(steps.get(i).toString()).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Generate a single string with all {@link Ingredient}s listed
     */
    private String getIngredientsListAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            sb.append(ingredients.get(i).toString()).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Get the list of {@link Ingredient}s
     */
    public ArrayList<String> getIngredientsListAsStringArrayList() {
        ArrayList<String> ingredientsList = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            ingredientsList.add(ingredients.get(i).toString());
        }
        return ingredientsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(servings);
        parcel.writeString(image);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
    }

    /**
     * Create the {@link Recipe} from the saved parcel
     */
    private Recipe(Parcel in) {
        name = in.readString();
        servings = in.readString();
        image = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
