package com.codingwithmitch.foodrecipes.util;

import androidx.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Call: an invocation of a Retrofit method that sends a request to a webserver and returns a response.
 *      - enqueue: Asynchronously send the request and notify callback of its response or if an error occurred
 *      talking to the server, creating the request, or processing the response.
 *
 * Type: is the common superinterface for all types in the Java. parameterized type
 * @param <R>
 */
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private Type responseType;

    // Constructor
    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(final Call<R> call) {
        return new LiveData<ApiResponse<R>>() {
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                call.enqueue(new Callback<R>() {
                    @Override
                    public void onResponse(Call<R> call, Response<R> response) {
                        postValue(apiResponse.create(response));
                    }

                    @Override
                    public void onFailure(Call<R> call, Throwable t) {
                        postValue(apiResponse.create(t));
                    }
                });
            }
        };
    }
}