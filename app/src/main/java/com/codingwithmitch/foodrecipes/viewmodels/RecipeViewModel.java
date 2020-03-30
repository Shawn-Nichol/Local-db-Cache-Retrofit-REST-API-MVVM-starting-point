package com.codingwithmitch.foodrecipes.viewmodels;


import android.app.Application;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

/**
 * View Model for a selected recipe.
 */
public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository recipeRepository;

    // Constructor.
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
    }

    // Live data returned from the Repository.
    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return recipeRepository.searchRecipesApi(recipeId);
    }


}





















