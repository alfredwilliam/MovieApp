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

package com.alfred.movieapp.ui.trailer;

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
import com.alfred.movieapp.databinding.FragmentTrailerBinding;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.model.Video;
import com.alfred.movieapp.model.VideoResponse;
import com.alfred.movieapp.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;

public class TrailerFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {

    OnTrailerSelectedListener mCallback;

    public interface OnTrailerSelectedListener {
        void onTrailerSelected(Video video);
    }

    private static final String TAG = TrailerFragment.class.getSimpleName();

    private List<Video> mVideos;

    private TrailerAdapter mTrailerAdapter;

    private Movie mMovie;

    private FragmentTrailerBinding mTrailerBinding;

    private TrailerViewModel mTrailerViewModel;

    public TrailerFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovie = getMovieData();

        setupViewModel(this.getActivity(), mMovie.getId());
    }

    private void setupViewModel(Context context, int movieId) {
        TrailerViewModelFactory factory = InjectorUtils.provideTrailerViewModelFactory(context, movieId);
        mTrailerViewModel = new ViewModelProvider(this, factory).get(TrailerViewModel.class);

        mTrailerViewModel.getVideoResponse().observe(getViewLifecycleOwner(), new Observer<VideoResponse>() {
            @Override
            public void onChanged(@Nullable VideoResponse videoResponse) {
                if (videoResponse != null) {
                    mVideos = videoResponse.getVideoResults();
                    videoResponse.setVideoResults(mVideos);

                    if (!mVideos.isEmpty()) {
                        mCallback.onTrailerSelected(mVideos.get(0));

                        mTrailerAdapter.addAll(mVideos);
                    } else {
                        showNoTrailersMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTrailerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_trailer, container, false);
        View rootView = mTrailerBinding.getRoot();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mTrailerBinding.rvTrailer.setLayoutManager(layoutManager);
        mTrailerBinding.rvTrailer.setHasFixedSize(true);

        mVideos = new ArrayList<>();

        mTrailerAdapter = new TrailerAdapter(mVideos, this);
        mTrailerBinding.rvTrailer.setAdapter(mTrailerAdapter);

        showOfflineMessage(isOnline());

        return rootView;
    }

    private Movie getMovieData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnTrailerSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrailerSelectedListener");
        }
    }

    @Override
    public void onItemClick(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showNoTrailersMessage() {
        mTrailerBinding.rvTrailer.setVisibility(View.INVISIBLE);
        // Then, show a message that says no trailers found
        mTrailerBinding.tvNoTrailers.setVisibility(View.VISIBLE);
    }

    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            mTrailerBinding.tvOffline.setVisibility(View.INVISIBLE);
            mTrailerBinding.rvTrailer.setVisibility(View.VISIBLE);
        } else {
            mTrailerBinding.rvTrailer.setVisibility(View.INVISIBLE);
            mTrailerBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }


    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
