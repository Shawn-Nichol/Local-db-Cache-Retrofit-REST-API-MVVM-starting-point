package com.codingwithmitch.foodrecipes.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Displays no more recipes when database runs out of recipes.
 */
public class SearchExhaustedViewHolder extends RecyclerView.ViewHolder {

    public SearchExhaustedViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
