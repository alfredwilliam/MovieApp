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

package com.alfred.movieapp.ui.cast;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.CastListItemBinding;
import com.alfred.movieapp.model.Cast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.alfred.movieapp.utilities.Constant.IMAGE_BASE_URL;
import static com.alfred.movieapp.utilities.Constant.IMAGE_FILE_SIZE;


public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> mCasts;

    public CastAdapter(List<Cast> casts) {
        mCasts = casts;
    }


    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        CastListItemBinding castItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.cast_list_item, viewGroup, false);
        return new CastViewHolder(castItemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = mCasts.get(position);
        holder.bind(cast);
    }


    @Override
    public int getItemCount() {
        if (null == mCasts) return 0;
        return mCasts.size();
    }


    public void addAll(List<Cast> casts) {
        mCasts.clear();
        mCasts.addAll(casts);
        notifyDataSetChanged();
    }



    public class CastViewHolder extends RecyclerView.ViewHolder {
        CastListItemBinding mCastItemBinding;

        CastViewHolder(CastListItemBinding castItemBinding) {
            super(castItemBinding.getRoot());
            mCastItemBinding = castItemBinding;
        }


         void bind(Cast cast) {
            // The complete profile image url
            String profile = IMAGE_BASE_URL + IMAGE_FILE_SIZE + cast.getProfilePath();
            // Load image with Picasso library
            Picasso.get()
                    .load(profile)
                    // Create circular avatars
                    .into(mCastItemBinding.ivCast, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) mCastItemBinding.ivCast.getDrawable())
                                    .getBitmap();
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                    itemView.getContext().getResources(), // to determine density
                                    imageBitmap); // image to round
                            drawable.setCircular(true);
                            mCastItemBinding.ivCast.setImageDrawable(drawable);
                        }

                        @Override
                        public void onError(Exception e) {

                            mCastItemBinding.ivCast.setImageResource(R.drawable.account_circle);
                        }
                    });

            // Set the cast name and character name to the TextViews
            mCastItemBinding.setCast(cast);
        }
    }
}
