package com.codingwithmitch.foodrecipes.repositories;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.requests.ServiceGenerator;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;



public class RecipeRepository {

    private static final String TAG = "RecipeRepository";
    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    //Singleton
    public static RecipeRepository getInstance(Context context){
        Log.d(TAG, "getInstance: ");
        if(instance == null){
            Log.d(TAG, "getInstance: creating Instance");
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    // Constructor.
    private RecipeRepository(Context context) {
        Log.d(TAG, "RecipeRepository: Constructor");
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
                if(item.getRecipes() != null) { // recipe list will be null if the api key is expired.
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];

                    int index = 0;
                    for(long rowid: recipeDao.insertRecipes((Recipe[]) (item.getRecipes().toArray(recipes)))) {
                        if(rowid == -1) {
                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in the cache");
                            // if the recipe already exists... I don't want to sest the ingredients or timestamp
                            // because they will be erased.
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
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
