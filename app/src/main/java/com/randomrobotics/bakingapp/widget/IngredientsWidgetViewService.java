package com.randomrobotics.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Service to create views for the homescreen widget
 */

public class IngredientsWidgetViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsWidgetViewFactory(this.getApplicationContext(), intent);
    }
}
