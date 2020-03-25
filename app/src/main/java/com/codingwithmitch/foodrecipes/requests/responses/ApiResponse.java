package com.codingwithmitch.foodrecipes.requests.responses;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;


/**
 *
 * @param <T> The type of value being boxed. Generic allow type (Integer, string, etc.. )to be a
 *           parameter to methods, classes and interfaces.
 *
 */
public class ApiResponse <T>{

    private static final String TAG = "ApiResponse";

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(!error.getMessage().equals("") ? error.getMessage() : "Unknown error\nCheck network connection");
    }

    public class ApiSuccessResponse<T> extends ApiResponse<T> {
        private T body;

        ApiSuccessResponse (T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }

    public ApiResponse<T> create(Response<T> response) {
        if(response.isSuccessful()) {
            T body = response.body();

            if(body == null || response.code() == 204) { // 204 is empty response.
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(body);
            }
        } else {
            String errorMsg = "";
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                Log.e(TAG, "create: ", e);
                errorMsg = response.message();

            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    public class ApiErrorResponse<T> extends ApiResponse<T> {
        private String errorMessage;


        public ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }

    public class ApiEmptyResponse<T> extends ApiResponse<T> {

    }

}
