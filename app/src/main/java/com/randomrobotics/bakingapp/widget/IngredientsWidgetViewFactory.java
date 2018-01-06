package com.randomrobotics.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.data.Ingredient;

import java.util.ArrayList;

/**
 * Create the views for the homescreen widget
 */

public class IngredientsWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private ArrayList<String> ingredientsList;

    /**
     * Constructor
     */
    IngredientsWidgetViewFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    /**
     * Create the widget view. Get the list of {@link Ingredient}s from the SharedPreferences (via the ConfigureActivity static method)
     */
    @Override
    public void onCreate() {
        ingredientsList = IngredientsWidgetConfigureActivity.getIngredientsListFromPrefs(context, appWidgetId);
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientsList.size();
    }

    /**
     * Set displayed information on the View
     */
    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_item);
        String ingredient = ingredientsList.get(i);
        views.setTextViewText(R.id.ingredients_widget_item_text, ingredient);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
