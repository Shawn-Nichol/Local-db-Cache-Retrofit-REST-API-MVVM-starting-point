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

    public static final String QUERY_EXHAUSTED = "No more results";
    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private RecipeRepository recipeRepository;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    // Query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "RecipeListViewModel: ");
        recipeRepository = RecipeRepository.getInstance(application);
        init();

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

    public int getPageNumber() {
        return pageNumber;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(!isPerformingQuery) {
            if(pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }

    private void executeSearch() {
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    recipes.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS ){
                        isPerformingQuery = false;
                        if(listResource.data != null) {
                            if (listResource.data.size() == 0) {
                                Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                recipes.setValue(new Resource<List<Recipe>>(
                                        Resource.Status.ERROR,
                                        listResource.data,
                                        QUERY_EXHAUSTED
                                ));
                                isPerformingQuery = true;
                            }
                        }
                        // must remove or it will keep listening to repository
                        recipes.removeSource(repositorySource);
                    }
                    else if(listResource.status == Resource.Status.ERROR ){
                        isPerformingQuery = false;
                        recipes.removeSource(repositorySource);
                    }
                }
                else{
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }


}















