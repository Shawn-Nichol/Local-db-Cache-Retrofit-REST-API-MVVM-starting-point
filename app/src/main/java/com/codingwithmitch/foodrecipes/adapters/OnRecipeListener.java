package com.codingwithmitch.foodrecipes.adapters;

/**
 * listen for item clicks.
 */
public interface OnRecipeListener {

    void onRecipeClick(int position);

    void onCategoryClick(String category);
}
