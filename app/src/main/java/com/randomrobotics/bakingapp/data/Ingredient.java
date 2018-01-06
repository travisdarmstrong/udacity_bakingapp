package com.randomrobotics.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Recipe Ingredient
 */

public class Ingredient implements Parcelable {
    private String quantity;
    private String measure;
    private String name;
    // Divider when generating string
    private static final String DIVIDER = "  ";

    /**
     * Get the quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Get the unit of measure
     */
    public String getMeasure() {
        return measure;
    }

    /**
     * Get the name of the ingredient
     */
    public String getName() {
        return name;
    }

    /**
     * Create a new {@link Ingredient}
     *
     * @param name     Name of the ingredient
     * @param quantity Quantity
     * @param measure  Unit of measure
     */
    public Ingredient(String name, String quantity, String measure) {
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
    }

    /**
     * Generate a string that represents the ingredient
     * "{Quantity}  {Unit of measure}  {Name}"
     */
    @Override
    public String toString() {
        // If the measure is "UNIT" then don't display that text, just the item (like eggs)
        if (measure.equals("UNIT")) {
            return String.format("%s%s%s", quantity, DIVIDER, name);
        } else {
            return String.format("%s%s%s%s%s", quantity, DIVIDER, measure, DIVIDER, name);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(quantity);
        parcel.writeString(measure);
        parcel.writeString(name);
    }

    /**
     * Create a new {@link Ingredient} from the saved parcel
     */
    private Ingredient(Parcel in) {
        quantity = in.readString();
        measure = in.readString();
        name = in.readString();
    }

    static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
