package com.codingwithmitch.foodrecipes.util;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Provides vertical spacing between each item in the RecyclerView.
 */
public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    // Constructor.
    public VerticalSpacingItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    /**
     * Retrieve any offsets for the given item.
     * @param outRect Rect to receive the output.
     * @param view The child view.
     * @param parent RecyclerView this ItemDecoration is decorating.
     * @param state Teh current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        outRect.top = verticalSpaceHeight;
    }
}
