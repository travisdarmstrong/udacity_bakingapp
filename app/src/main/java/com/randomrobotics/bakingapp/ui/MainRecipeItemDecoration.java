package com.randomrobotics.bakingapp.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.MainActivity;

/**
 * Decoration to separate list items when displaying {@link Recipe} list in {@link MainActivity}
 */

public class MainRecipeItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalHeight;

    /**
     * Create a new {@link MainRecipeItemDecoration} with the specified height
     *
     * @param verticalHeight Height of the displayed item
     */
    public MainRecipeItemDecoration(int verticalHeight) {
        this.verticalHeight = verticalHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = verticalHeight;
    }
}
