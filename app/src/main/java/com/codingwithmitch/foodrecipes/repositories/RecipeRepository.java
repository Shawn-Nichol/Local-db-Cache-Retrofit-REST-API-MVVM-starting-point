package com.codingwithmitch.foodrecipes.repositories;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.ServiceGenerator;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;

import persistence.RecipeDao;
import persistence.RecipeDatabase;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    // Singleton
    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    // Constructor.
    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance() ) {


            /**
             * Save the response from Retrofit into the cache.
             * @param item
             */
            @NonNull
            @Override
            public void saveCallResult(@NonNull RecipeSearchResponse item) {

            }

            /**
             * Decides weather or not to refresh the cache, use a timestamp variable.
             * @param data
             * @return
             */
            @NonNull
            @Override
            public boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            /**
             * Retrieves data from local cache.
             * @return
             */
            @NonNull
            @Override
            public LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            /**
             * Creates a LiveData Retrofit call object.
             * @return
             */
            @NonNull
            @Override
            public LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi()
                        .searchRecipe(
                                Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }
        }.getAsLiveData();
    }
}