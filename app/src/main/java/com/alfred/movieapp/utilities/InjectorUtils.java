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

package com.alfred.movieapp.utilities;

import android.content.Context;

import com.alfred.movieapp.AppExecutors;
import com.alfred.movieapp.data.MovieDatabase;
import com.alfred.movieapp.data.MovieRepository;
import com.alfred.movieapp.ui.homeFragment.HomeViewModelFactory;
import com.alfred.movieapp.ui.info.InfoViewModelFactory;
import com.alfred.movieapp.ui.main.FavViewModelFactory;
import com.alfred.movieapp.ui.main.MainViewModelFactory;
import com.alfred.movieapp.ui.review.ReviewViewModelFactory;
import com.alfred.movieapp.ui.searchFragment.SearchViewModelFactory;
import com.alfred.movieapp.ui.topRatingFragment.TopRatingViewModelFactory;
import com.alfred.movieapp.ui.trailer.TrailerViewModelFactory;

/**
 * Provides static methods to inject the various classes needed for PopularMovies
 */
public class InjectorUtils {

    public static MovieRepository provideRepository(Context context) {
        MovieDatabase database = MovieDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        // The Retrofit class generates an implementation of the TheMovieApi interface
        TheMovieApi theMovieApi = Controller.getClient().create(TheMovieApi.class);
        return MovieRepository.getInstance(database.movieDao(), theMovieApi, executors);
    }

    public static HomeViewModelFactory provideHomeViewModelFactory(Context context, String sortCriteria) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new HomeViewModelFactory(repository, sortCriteria);
    }

    public static SearchViewModelFactory provideSearchViewModelFactory(Context context, String search) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new SearchViewModelFactory(repository, search);
    }

    public static TopRatingViewModelFactory provideTopRatingViewModelFactory(Context context, String sortCriteria) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new TopRatingViewModelFactory(repository, sortCriteria);
    }

    public static InfoViewModelFactory provideInfoViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new InfoViewModelFactory(repository, movieId);
    }

    public static ReviewViewModelFactory provideReviewViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new ReviewViewModelFactory(repository, movieId);
    }

    public static TrailerViewModelFactory provideTrailerViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new TrailerViewModelFactory(repository, movieId);
    }

    public static FavViewModelFactory provideFavViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new FavViewModelFactory(repository, movieId);
    }
}
