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

package com.alfred.movieapp.ui.review;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.FragmentReviewBinding;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.model.Review;
import com.alfred.movieapp.model.ReviewResponse;
import com.alfred.movieapp.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;

public class ReviewFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = ReviewFragment.class.getSimpleName();

    private List<Review> mReviews;

    private FragmentReviewBinding mReviewBinding;

    private ReviewAdapter mReviewAdapter;

    private Movie mMovie;

    private ReviewViewModel mReviewViewModel;

    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        // Observe the data and update the UI
        setupViewModel(this.getActivity());
    }


    private void setupViewModel(Context context) {
        ReviewViewModelFactory factory = InjectorUtils.provideReviewViewModelFactory(context, mMovie.getId());
        mReviewViewModel = new ViewModelProvider(this, factory).get(ReviewViewModel.class);

        mReviewViewModel.getReviewResponse().observe(getViewLifecycleOwner(), new Observer<ReviewResponse>() {
            @Override
            public void onChanged(@Nullable ReviewResponse reviewResponse) {
                if (reviewResponse != null) {
                    mReviews = reviewResponse.getReviewResults();
                    reviewResponse.setReviewResults(mReviews);
                    if (!mReviews.isEmpty()) {
                        mReviewAdapter.addAll(mReviews);
                    } else {
                        showNoReviewsMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate mReviewBinding using DataBindingUtil
        mReviewBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_review, container, false);
        View rootView = mReviewBinding.getRoot();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mReviewBinding.rvReview.setLayoutManager(layoutManager);
        mReviewBinding.rvReview.setHasFixedSize(true);

        // Create an empty ArrayList
        mReviews = new ArrayList<>();

        // The ReviewAdapter is responsible for displaying each item in the list.
        mReviewAdapter = new ReviewAdapter(mReviews, this);
        // Set ReviewAdapter on RecyclerView
        mReviewBinding.rvReview.setAdapter(mReviewAdapter);

        // Show a message when offline
        showOfflineMessage(isOnline());

        return rootView;
    }

    /**
     * Handles RecyclerView item clicks to open a website that displays the user review.
     *
     * @param url The URL that displays the user review
     */
    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * This method will make the message that says no reviews found visible and
     * hide the View for the review data
     */
    private void showNoReviewsMessage() {
        // First, hide the currently visible data
        mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
        // Then, show a message that says no reviews found
        mReviewBinding.tvNoReviews.setVisibility(View.VISIBLE);
    }

    /**
     * Make the offline message visible and hide the review View when offline
     *
     * @param isOnline True when connected to the network
     */
    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            // First, hide the offline message
            mReviewBinding.tvOffline.setVisibility(View.INVISIBLE);
            // Then, make sure the review data is visible
            mReviewBinding.rvReview.setVisibility(View.VISIBLE);
        } else {
            // First, hide the currently visible data
            mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
            // Then, show an offline message
            mReviewBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
