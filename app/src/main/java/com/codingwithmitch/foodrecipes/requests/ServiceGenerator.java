package com.codingwithmitch.foodrecipes.requests;

import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapter;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codingwithmitch.foodrecipes.util.Constants.CONNECTION_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.READ_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.WRITE_TIMEMOUT;

/**
 * ServiceGenerator gets information from web source.
 */
public class ServiceGenerator {

    // OkHttpClient: Used to create Http connection.
    private static OkHttpClient client = new OkHttpClient.Builder()

            // establish connection to server.
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            .writeTimeout(WRITE_TIMEMOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)

            .build();

    // Retrofit adapts a Java interface to Http calls.
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()

                    // The URL you wish to get information from.
                    .baseUrl(Constants.BASE_URL)

                    // The Http Client used for requests.
                    .client(client)

                    // Add a call adapter factory for supporting service method return types.
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())

                    // Add converter factory for serialization and deserialization of objects.
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
