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

import com.alfred.movieapp.BuildConfig;

public final class Constant {

    private Constant() {
    }

    /** The base movie URL from TMDb */
    static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";


    public static final String API_KEY = BuildConfig.ApiKey;
    public static final String LANGUAGE = "en-US";
    public static final int PAGE = 1;
    public static final String CREDITS = "credits";

    public static final int RESPONSE_CODE_API_STATUS = 401;

    public static final int REQUEST_CODE_DIALOG = 0;

    public static final int GRID_SPAN_COUNT_2 = 2;
    public static final int GRID_SPAN_COUNT = 3;
    public static final int GRID_SPACING = 2;
    public static final boolean GRID_INCLUDE_EDGE = true;

    // Constants for DetailActivity

    public static final String EXTRA_MOVIE = "movie";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String BACKDROP_FILE_SIZE ="w500";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_THUMBNAIL_URL_JPG = "/0.jpg";
    public static final String SHARE_URL = "https://www.themoviedb.org/movie/";
    public static final int RELEASE_YEAR_BEGIN_INDEX = 0;
    public static final int RELEASE_YEAR_END_INDEX = 4;
    public static final String SHARE_INTENT_TYPE_TEXT = "text/plain";

    public static final String IMAGE_FILE_SIZE = "w185";

    // Constants for DetailPagerAdapter

    public static final int INFORMATION = 0;
    public static final int TRAILERS = 1;
    public static final int CAST = 2;
    public static final int REVIEWS = 3;

    public static final String[] TAP_TITLE = new String[] {"Info", "Trailers", "Cast", "Reviews"};

    public static final int PAGE_COUNT = TAP_TITLE.length;

    public static final String DEFAULT_VALUE = "";

    public static final int BYTE = 0x01;

    public static final String PATTERN_FORMAT_NUMBER = "#,###";
    public static final String PATTERN_FORMAT_CURRENCY = "$###,###";
    public static final String PATTERN_FORMAT_DATE_INPUT = "yyyy-MM-dd";
    public static final String PATTERN_FORMAT_DATE_OUTPUT = "MMM dd, yyyy";

    public static final int TWO = 2;
    public static final int THREE = 3;

    public static final int ONE = 1;

    public static final String RESULTS_RUNTIME = "runtime";
    public static final String RESULTS_RELEASE_YEAR = "release_year";
    public static final String RESULTS_GENRE = "genre";

    public static final String LAYOUT_MANAGER_STATE = "layout_manager_state";

    public static final String DATABASE_NAME = "favoritemovies";

    public static final int NUMBER_OF_THREADS_THREE = 3;

    public static final int DRAWABLES_ZERO = 0;

    public static final int PREVIOUS_PAGE_KEY_ONE = 1;
    public static final int NEXT_PAGE_KEY_TWO = 2;
    public static final int PAGE_ONE = 1;

    public static final int NUMBER_OF_FIXED_THREADS_FIVE = 5;

    public static final int INITIAL_LOAD_SIZE_HINT = 10;
    public static final int PAGE_SIZE = 20;
    public static final int PREFETCH_DISTANCE = 10;

    public static final String DELETE = "Delete";
    public static final int DELETE_GROUP_ID = 0;
    public static final int DELETE_ORDER = 0;

    public static final int ZERO = 0;
}
