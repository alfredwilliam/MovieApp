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

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alfred.movieapp.AppExecutors;
import com.alfred.movieapp.R;
import com.alfred.movieapp.data.MovieDatabase;
import com.alfred.movieapp.data.MovieEntry;
import com.alfred.movieapp.databinding.FavListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.alfred.movieapp.utilities.Constant.DELETE;
import static com.alfred.movieapp.utilities.Constant.DELETE_GROUP_ID;
import static com.alfred.movieapp.utilities.Constant.DELETE_ORDER;
import static com.alfred.movieapp.utilities.Constant.IMAGE_BASE_URL;
import static com.alfred.movieapp.utilities.Constant.IMAGE_FILE_SIZE;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<MovieEntry> mMovieEntries;

    private Context mContext;


    private final FavoriteAdapterOnClickHandler mOnClickHandler;


    public interface FavoriteAdapterOnClickHandler {
        void onFavItemClick(MovieEntry movieEntry);
    }


    public FavoriteAdapter(Context context, FavoriteAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
    }


    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        FavListItemBinding favItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.fav_list_item, parent, false);
        return new FavoriteViewHolder(favItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MovieEntry movieEntry = mMovieEntries.get(position);
        holder.bind(movieEntry);
    }

    @Override
    public int getItemCount() {
        if (mMovieEntries == null) return 0;
        return mMovieEntries.size();
    }

    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

    public List<MovieEntry> getMovies() {
        return mMovieEntries;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        FavListItemBinding mFavItemBinding;

        public FavoriteViewHolder(FavListItemBinding favItemBinding) {
            super(favItemBinding.getRoot());

            mFavItemBinding = favItemBinding;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        void bind(MovieEntry movieEntry) {
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movieEntry.getPosterPath();

            Picasso.get()
                    .load(thumbnail)
                    .into(mFavItemBinding.ivThumbnail);

            mFavItemBinding.tvTitle.setText(movieEntry.getTitle());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieEntry movieEntry = mMovieEntries.get(adapterPosition);
            mOnClickHandler.onFavItemClick(movieEntry);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int adapterPosition = getAdapterPosition();
            MenuItem item = menu.add(DELETE_GROUP_ID, adapterPosition, DELETE_ORDER, v.getContext().getString(R.string.action_delete));
            item.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getTitle().toString()) {
                case DELETE:
                    int adapterPosition = item.getItemId();
                    MovieEntry movieEntry = mMovieEntries.get(adapterPosition);
                    delete(movieEntry);
                    return true;
                default:
                    return false;
            }
        }

        private void delete(final MovieEntry movieEntry) {
            final MovieDatabase db = MovieDatabase.getInstance(mContext);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.movieDao().deleteMovie(movieEntry);
                }
            });
        }
    }
}
