package com.codingwithmitch.foodrecipes.adapters;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RequestManager, a class for managing and starting requests for glide. Can use lifecycle events to
 * intelligently stop, start and restart requests. Retrieve either by instantiating a new object, or
 * take advantage of built in life cycle handling use the static glide.load methods.
 *
 * ViewPreLoadSizeProvider:
 *
 * TextUtils:
 */
public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPreloader.PreloadModelProvider<String>{


    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;
    private RequestManager requestManager;
    private ViewPreloadSizeProvider<String> preloadSizeProvider;

    // Constructor
    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener, RequestManager requestManager, ViewPreloadSizeProvider<String> viewPreloadSizeProvider) {
        this.mOnRecipeListener = mOnRecipeListener;
        this.requestManager = requestManager;
        this.preloadSizeProvider = viewPreloadSizeProvider;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to an
     *                  adapter
     * @param i The view type of the new View.
     * @return a new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = null;
        switch (i){

            case RECIPE_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }

            case CATEGORY_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_list_item, viewGroup, false);
                return new CategoryViewHolder(view, requestManager, mOnRecipeListener);
            }

            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider);
            }
        }


    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update
     * the contents of the itemView to reflect the item at the given position.
     *
     * @param viewHolder The ViewHolder wich should be updated to represent the contents of the item at
     *                   the given position.
     * @param i The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int itemViewType = getItemViewType(i);
        if(itemViewType == RECIPE_TYPE){
            ((RecipeViewHolder)viewHolder).onBind(mRecipes.get(i));
        } else if(itemViewType == CATEGORY_TYPE){
            ((CategoryViewHolder)viewHolder).onBind(mRecipes.get(i));
        }

    }

    /**
     * Return the View type of the item at position for the purposes of view recycling.
     *
     * @param position position in the query.
     * @return int, identifying the type of the view needed to represent the item at position. Type codes
     * need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else if(position == mRecipes.size() - 1
                && position != 0
                && !mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return LOADING_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    /**
     * Size of the recyclerview.
     */
    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }

    /**
     * display loading during a search request.
     */
    public void displayOnlyLoading() {
        clearRecipesList();
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    private void clearRecipesList() {
        if(mRecipes == null ) {
            mRecipes = new ArrayList<>();
        } else {
            mRecipes.clear();
        }
        notifyDataSetChanged();
    }


    public void setQueryExhausted(){
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()) {
            if(mRecipes.get(0).getTitle().equals("LOADING...")) {
                mRecipes.remove(0);
            } else if(mRecipes.get(mRecipes.size() -1).equals("LOADING...")) {
                mRecipes.remove(mRecipes.size() -1);
            }

            notifyDataSetChanged();
        }
    }

    /**
     * Pagination.
     */
    public void displayLoading(){
        if(mRecipes == null) {
            mRecipes = new ArrayList<>();
        }

        if(!isLoading()){
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            mRecipes.add(recipe);
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                if(mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public void displaySearchCategories(){
        List<Recipe> categories = new ArrayList<>();
        for(int i = 0; i< Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes = categories;
        notifyDataSetChanged();
    }



    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position){
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                return mRecipes.get(position);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
       String url = mRecipes.get(position).getImage_url();
       if(TextUtils.isEmpty(url)) {
           return Collections.emptyList();
       }

        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }
}

