package com.randomrobotics.bakingapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainUITest {

    /**
     * Recipe names
     */
    private String[] RecipeNames = {"Nutella Pie", "Brownies", "Yellow Cake", "Cheesecake"};

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Verify the Recipes are displayed
     */
    @Test
    public void VerifyDisplayedRecipe() {
        onView(withId(R.id.main_recyclerview))
                .check(matches(hasDescendant(withText(RecipeNames[0]))));
        onView(withId(R.id.main_recyclerview))
                .check(matches(hasDescendant(withText(RecipeNames[1]))));
        onView(withId(R.id.main_recyclerview))
                .check(matches(hasDescendant(withText(RecipeNames[2]))));
        onView(withId(R.id.main_recyclerview))
                .check(matches(hasDescendant(withText(RecipeNames[3]))));
    }

    /**
     * Verify the Nutella Pie recipe steps are displayed
     */
    @Test
    public void VerifyNutellaPieStepsDisplayed() {
        onView(withId(R.id.main_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recipe_steps_recycler))
                .check(matches(hasDescendant(withText("Recipe Introduction"))));
        onView(withId(R.id.recipe_steps_recycler))
                .check(matches(hasDescendant(withText("Starting prep"))));
        onView(withId(R.id.recipe_steps_recycler))
                .check(matches(hasDescendant(withText("Prep the cookie crust."))));
        onView(withId(R.id.recipe_steps_recycler))
                .check(matches(hasDescendant(withText("Press the crust into baking form."))));
    }

    /**
     * Verify the Brownie recipe is displayed with correct ingredients
     * Also tests the Ingredients button
     */
    @Test
    public void VerifyBrownieIngredientsDisplayed() {
        onView(withId(R.id.main_recyclerview))
                .perform(actionOnItem(hasDescendant(withText("Brownies")), click()));
        onView(withId(R.id.recipe_steps_ingredients))
                .perform(ViewActions.click());
        onView(withId(R.id.ingredients_recycler))
                .check(matches(hasDescendant(withText("350  G  Bittersweet chocolate (60-70% cacao)"))));
        onView(withId(R.id.ingredients_recycler))
                .check(matches(hasDescendant(withText("226  G  unsalted butter"))));
    }

    /**
     * Verify the Yellow Cake recipe is displayed with correct step details
     * Also test the Next nav button
     */
    @Test
    public void VerifyYellowCakeStepDetails() {
        onView(withId(R.id.main_recyclerview))
                .perform(actionOnItem(hasDescendant(withText("Yellow Cake")), click()));
        onView(withId(R.id.recipe_steps_recycler))
                .perform(actionOnItem(hasDescendant(withText("Recipe Introduction")), click()));
        onView(withId(R.id.detail_instructions))
                .check(matches(withText("Recipe Introduction")));
        onView(withId(R.id.detail_video))
                .check(matches(isDisplayed()));
        try {
            onView(withId(R.id.detail_nav_next)).check(matches(isDisplayed()));
            // on phone display, use the next button
            onView(withId(R.id.detail_nav_next))
                    .perform(click());
        } catch (Exception e) {
            // on tablet display, click the next step
            onView(withId(R.id.recipe_steps_recycler))
                    .perform(actionOnItem(hasDescendant(withText("Starting prep")), click()));
        }
        onView(withId(R.id.detail_instructions))
                .check(matches(withText("1. Preheat the oven to 350°F. Butter the bottoms and sides of two 9\" round pans with 2\"-high sides. Cover the bottoms of the pans with rounds of parchment paper, and butter the paper as well.")));
    }

    /**
     * Verify the Cheesecake Recipe is displayed with correct step details
     * Also test the Previous nav button
     */
    @Test
    public void VerifyCheesecakeStepDetails() {
        onView(withId(R.id.main_recyclerview))
                .perform(actionOnItem(hasDescendant(withText("Cheesecake")), click()));
        onView(withId(R.id.recipe_steps_recycler))
                .perform(actionOnItem(hasDescendant(withText("Final cooling and set.")), click()));
        onView(withId(R.id.detail_instructions))
                .check(matches(withText("12. Cover the cheesecake with plastic wrap, not allowing the plastic to touch the top of the cake, and refrigerate it for at least 8 hours. Then it's ready to serve!")));
        try {
            onView(withId(R.id.detail_nav_previous)).check(matches(isDisplayed()));
            // on phone display, use the PREVIOUS button
            onView(withId(R.id.detail_nav_previous))
                    .perform(click());
        } catch (Exception e) {
            // on tablet display, click the previous step
            onView(withId(R.id.recipe_steps_recycler))
                    .perform(actionOnItem(hasDescendant(withText("Remove from oven and cool at room temperature.")), click()));
        }
        onView(withId(R.id.detail_instructions))
                .check(matches(withText("11. Take the cheesecake out of the oven. It should look pale yellow or golden on top and be set but still slightly jiggly. Let it cool to room temperature. ")));

    }
}
