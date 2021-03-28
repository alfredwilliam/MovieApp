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

package com.alfred.movieapp.ui.main;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.MovieListItemBinding;
import com.alfred.movieapp.model.Movie;
import com.squareup.picasso.Picasso;

import static com.alfred.movieapp.utilities.Constant.IMAGE_BASE_URL;
import static com.alfred.movieapp.utilities.Constant.IMAGE_FILE_SIZE;


public class MoviePagedListAdapter extends PagedListAdapter<Movie, MoviePagedListAdapter.MoviePagedViewHolder> {

    private final MoviePagedListAdapterOnClickHandler mOnClickHandler;

    public interface MoviePagedListAdapterOnClickHandler {
        void onItemClick(Movie movie);
    }

    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public MoviePagedListAdapter(MoviePagedListAdapterOnClickHandler onClickHandler) {
        super(MoviePagedListAdapter.DIFF_CALLBACK);
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public MoviePagedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MovieListItemBinding mMovieItemBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.movie_list_item, parent, false);

        return new MoviePagedViewHolder(mMovieItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePagedViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class MoviePagedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MovieListItemBinding mMovieItemBinding;

        public MoviePagedViewHolder(MovieListItemBinding movieItemBinding) {
            super(movieItemBinding.getRoot());
            mMovieItemBinding = movieItemBinding;
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movie.getPosterPath();

            Picasso.get()
                    .load(thumbnail)
                    .error(R.drawable.image)
                    .into(mMovieItemBinding.ivThumbnail);

            mMovieItemBinding.tvTitle.setText(movie.getTitle());
            mMovieItemBinding.tvRate.setText("  " + movie.getVoteAverage()+"");

        }

        /**
         * Called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = getItem(adapterPosition);
            mOnClickHandler.onItemClick(movie);
        }
    }
}
