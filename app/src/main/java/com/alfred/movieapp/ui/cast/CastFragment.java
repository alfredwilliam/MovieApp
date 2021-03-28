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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.alfred.movieapp.databinding.FragmentCastBinding;
import com.alfred.movieapp.model.Cast;
import com.alfred.movieapp.model.Credits;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.model.MovieDetails;
import com.alfred.movieapp.ui.info.InfoViewModel;
import com.alfred.movieapp.ui.info.InfoViewModelFactory;
import com.alfred.movieapp.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;


public class CastFragment extends Fragment {

    public static final String TAG = CastFragment.class.getSimpleName();

    private List<Cast> mCastList;

    private CastAdapter mCastAdapter;

    private FragmentCastBinding mCastBinding;

    private Movie mMovie;

    private InfoViewModel mInfoViewModel;

    public CastFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCastBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cast, container, false);
        View rootView = mCastBinding.getRoot();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mCastBinding.rvCast.setLayoutManager(layoutManager);
        mCastBinding.rvCast.setHasFixedSize(true);

        mCastList = new ArrayList<>();

        mCastAdapter = new CastAdapter(mCastList);
        mCastBinding.rvCast.setAdapter(mCastAdapter);

        showOfflineMessage(isOnline());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovie = getMovieData();

        setupViewModel(this.getActivity(), mMovie.getId());
    }

    private void setupViewModel(Context context, int movieId) {
        // Get the ViewModel from the factory
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = new ViewModelProvider(this, factory).get(InfoViewModel.class);

        // Retrieve live data object using the getMovieDetails() method from the ViewModel
        mInfoViewModel.getMovieDetails().observe(getViewLifecycleOwner(), new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    // Display cast of the movie
                    loadCast(movieDetails);
                }
            }
        });
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

    private void loadCast(MovieDetails movieDetails) {
        // Get Credits from the MovieDetails
        Credits credits = movieDetails.getCredits();
        mCastList = credits.getCast();
        credits.setCast(mCastList);
        mCastAdapter.addAll(mCastList);
    }


    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            mCastBinding.tvOffline.setVisibility(View.INVISIBLE);
            mCastBinding.rvCast.setVisibility(View.VISIBLE);
        } else {
            mCastBinding.rvCast.setVisibility(View.INVISIBLE);
            mCastBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
