package com.codingwithmitch.foodrecipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.codingwithmitch.foodrecipes.adapters.OnRecipeListener;
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Resource;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

import static com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;


/**
 * data:
 * listResource:
 * clearFocus: called when the view wants to give up focus.
 */
public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Log.d(TAG, "onCreate: ");

        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    /**
     * Observe the livedata from the site.
     */
    private void subscribeObservers(){
        Log.d(TAG, "subscribeObservers: ");

        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if(listResource.data != null){
                        switch (listResource.status ) {
                            case LOADING:
                                if(mRecipeListViewModel.getPageNumber() > 1) {
                                    mAdapter.displayLoading();
                                } else {
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            case ERROR:
                                Log.e(TAG, "onChanged: cannot refresh the cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: ERROR, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_LONG).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)) {
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            case SUCCESS:
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: SUCCESS, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;
                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewstate().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                if(viewState != null){
                    switch (viewState){

                        case RECIPES:{
                            // recipes will show automatically from other observer
                            break;
                        }

                        case CATEGORIES:{
                            displaySearchCategories();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Provides type independent options to customize loads with glide.
     */
    private RequestManager initGlide() {
        RequestOptions options= new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);
        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }

    /**
     * Search Recipe API
     */
    private void searchRecipeApi(String query){
        Log.d(TAG, "searchRecipeApi: ");
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    /**
     * Start RecyclerView.
     */
    private void initRecyclerView(){
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloader);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloader,
                30);

        mRecyclerView.addOnScrollListener(preloader);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!mRecyclerView.canScrollVertically(1)
                        && mRecipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.RECIPES){
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Call when the user submits the query.
             * @param s The query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the SearchView perform the default
             *          action.
             */
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchRecipeApi(s);
                return false;
            }

            /**
             * Called when the query text is changed by the user.
             * @param s
             * @return false if the SearchView should perform the default action of showing any suggestions
             *          if available, true if the action was handled by a listener.
             */
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    /**
     * Run from the Recipe viewHolder.
     * @param position the recipe clicked.
     */
    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    /**
     * The category selected.
     */
    @Override
    public void onCategoryClick(String category) {
        searchRecipeApi(category);
    }

    /**
     * Displays the search categories.
     */
    private void displaySearchCategories(){
        mAdapter.displaySearchCategories();
    }


    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        // Back press if the in the categories view.
        if(mRecipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
            Log.d(TAG, "onBackPressed: do nothing");
        } else {
            Log.d(TAG, "onBackPressed: return to categories");
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }
    }
}








