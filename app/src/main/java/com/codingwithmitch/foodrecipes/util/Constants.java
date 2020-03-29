package com.codingwithmitch.foodrecipes.util;

public class Constants {
    public static final String BASE_URL = "https://recipesapi.herokuapp.com/";
    public static final String API_KEY = "";

    public static final int CONNECTION_TIMEOUT = 10; // 10 SECONDS
    public static final int READ_TIMEOUT = 2;  // 2 Seconds
    public static final int WRITE_TIMEMOUT = 2; // 2 Seconds

    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30;


    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
