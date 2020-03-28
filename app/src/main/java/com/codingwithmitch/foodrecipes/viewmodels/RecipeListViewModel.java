package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private RecipeRepository recipeRepository;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "RecipeListViewModel: ");

        init();
        recipeRepository = RecipeRepository.getInstance(application);
    }

    private void init(){
        Log.d(TAG, "init: ");
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewstate(){
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public void searchRecipesApi(String query, int pageNumber){
        Log.d(TAG, "searchRecipesApi: ");
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                recipes.setValue(listResource);
            }
        });
    }

}















