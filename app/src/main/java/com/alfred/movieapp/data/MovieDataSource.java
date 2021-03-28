/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alfred.movieapp.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.model.MovieResponse;
import com.alfred.movieapp.utilities.Constant;
import com.alfred.movieapp.utilities.Controller;
import com.alfred.movieapp.utilities.TheMovieApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alfred.movieapp.utilities.Constant.NEXT_PAGE_KEY_TWO;
import static com.alfred.movieapp.utilities.Constant.PREVIOUS_PAGE_KEY_ONE;
import static com.alfred.movieapp.utilities.Constant.RESPONSE_CODE_API_STATUS;

public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    private static final String TAG = MovieDataSource.class.getSimpleName();

    private TheMovieApi mTheMovieApi;

    private String mSortCriteria;

    public MovieDataSource(String sortCriteria) {
        mTheMovieApi = Controller.getClient().create(TheMovieApi.class);
        mSortCriteria = sortCriteria;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, Constant.PAGE_ONE)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            callback.onResult(response.body().getMovieResults(),
                                    PREVIOUS_PAGE_KEY_ONE, NEXT_PAGE_KEY_TWO);

                        } else if (response.code() == RESPONSE_CODE_API_STATUS) {
                            Log.e(TAG, "Invalid Api key. Response code: " + response.code());
                        } else {
                            Log.e(TAG, "Response Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed initializing a PageList: " + t.getMessage());
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Movie> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull final LoadCallback<Integer, Movie> callback) {

        final int currentPage = params.key;

        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, currentPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            int nextKey = currentPage + 1;
                            callback.onResult(response.body().getMovieResults(), nextKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed appending page: " + t.getMessage());
                    }
                });

    }
}
