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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.ReviewListItemBinding;
import com.alfred.movieapp.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> mReviews;

    private final ReviewAdapterOnClickHandler mOnClickHandler;

    public interface ReviewAdapterOnClickHandler {
        void onItemClick(String url);
    }

    public ReviewAdapter(List<Review> reviews, ReviewAdapterOnClickHandler onClickHandler) {
        mReviews = reviews;
        mOnClickHandler = onClickHandler;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ReviewListItemBinding reviewItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(reviewItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (null == mReviews) return 0;
        return mReviews.size();
    }

    public void addAll(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ReviewListItemBinding mReviewItemBinding;

        ReviewViewHolder(ReviewListItemBinding reviewItemBinding) {
            super(reviewItemBinding.getRoot());
            mReviewItemBinding = reviewItemBinding;

            // Call setOnClickListener on  the View passed into the constructor
            itemView.setOnClickListener(this);
        }

        void bind(Review review) {
            // Set author and content of review to the TextView
            mReviewItemBinding.setReview(review);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviews.get(adapterPosition);
            mOnClickHandler.onItemClick(review.getUrl());
        }
    }
}
