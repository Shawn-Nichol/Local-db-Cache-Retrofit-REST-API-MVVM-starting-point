package com.codingwithmitch.foodrecipes.requests.responses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * toString:
 */
public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;

    @SerializedName("error")
    @Expose()
    private String error;

    @Nullable
    public Recipe getRecipe(){
        return recipe;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }
}