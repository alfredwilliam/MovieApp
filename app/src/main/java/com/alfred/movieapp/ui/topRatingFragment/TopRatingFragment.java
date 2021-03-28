package com.alfred.movieapp.ui.topRatingFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfred.movieapp.GridSpacingItemDecoration;
import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.TopRatingFragmentBinding;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.ui.detail.DetailActivity;
import com.alfred.movieapp.ui.homeFragment.HomeFragment;
import com.alfred.movieapp.ui.homeFragment.HomeViewModel;
import com.alfred.movieapp.ui.homeFragment.HomeViewModelFactory;
import com.alfred.movieapp.ui.main.MoviePagedListAdapter;
import com.alfred.movieapp.utilities.InjectorUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;
import static com.alfred.movieapp.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.alfred.movieapp.utilities.Constant.GRID_SPACING;
import static com.alfred.movieapp.utilities.Constant.GRID_SPAN_COUNT;
import static com.alfred.movieapp.utilities.Constant.GRID_SPAN_COUNT_2;
import static com.alfred.movieapp.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.alfred.movieapp.utilities.Constant.REQUEST_CODE_DIALOG;

public class TopRatingFragment extends Fragment implements MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler {

    private TopRatingViewModel mViewModel;
    private TopRatingFragmentBinding binding;

    private MoviePagedListAdapter mMoviePagedListAdapter;

    private Parcelable mSavedLayoutState;

    public static TopRatingFragment newInstance() {
        return new TopRatingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.top_rating_fragment, container, false);
        if (savedInstanceState == null) {
            showNetworkDialog(isOnline());
        }

        initAdapter();

        if (savedInstanceState == null) {
            showNetworkDialog(isOnline());
        }

        setSwipeRefreshLayout();

        setColumnSpacing();

        if (savedInstanceState != null) {
            mSavedLayoutState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            binding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
        }
        return binding.getRoot();
    }

    private void setColumnSpacing() {
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(
                GRID_SPAN_COUNT, GRID_SPACING, GRID_INCLUDE_EDGE);
        binding.rvMovie.addItemDecoration(decoration);
    }

    private void setSwipeRefreshLayout() {
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            /**
             * Called when a swipe gesture triggers a refresh
             */
            @Override
            public void onRefresh() {
                showMovieDataView();

                updateUI(getString(R.string.pref_sort_by_now_playing));

                hideRefresh();

                showSnackbarRefresh(isOnline());
            }
        });
    }

    private void showSnackbarRefresh(boolean isOnline) {
        if (isOnline) {
            Snackbar.make(binding.rvMovie, getString(R.string.snackbar_updated)
                    , Snackbar.LENGTH_SHORT).show();
        }
    }

    private void hideRefresh() {
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setupViewModel(getString(R.string.pref_sort_by_now_playing));

        TopRatingViewModelFactory factory = InjectorUtils.provideTopRatingViewModelFactory(
                getContext(), getString(R.string.pref_sort_by_top_rated));
        mViewModel = new ViewModelProvider(this, factory).get(TopRatingViewModel.class);
        mViewModel.getMoviePagedList().observe(getViewLifecycleOwner(), new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                if (pagedList!=null) {
                    //ToastUtils.showLong("DATA RECEIVED");
                    showMovieDataView();
                    mMoviePagedListAdapter.submitList(pagedList);
                    binding.rvMovie.setAdapter(mMoviePagedListAdapter);
                    // Restore the scroll position after setting up the adapter with the list of movies
                    binding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }

                if (!isOnline()) {
                    showMovieDataView();
                    showSnackbarOffline();
                }
            }
        });

    }

    private void initAdapter() {

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), GRID_SPAN_COUNT);
        binding.rvMovie.setLayoutManager(layoutManager);

        binding.rvMovie.setHasFixedSize(true);
        binding.rvMovie.setAdapter(mMoviePagedListAdapter);

        mMoviePagedListAdapter = new MoviePagedListAdapter(this);
    }


    @Override
    public void onItemClick(Movie movie) {
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, b);
        startActivity(intent);
    }

    private void setupViewModel(String sortCriteria) {
        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(
                getContext(), sortCriteria);
        mViewModel = new ViewModelProvider(this, factory).get(TopRatingViewModel.class);

        updateUI(getString(R.string.pref_sort_by_now_playing));
    }

    private void updateUI(String sortCriteria) {



        observeMoviePagedList();
    }

    private void observeMoviePagedList() {
        mViewModel.getMoviePagedList().observe(getViewLifecycleOwner(), new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                if (pagedList.size()>0) {
                    showMovieDataView();
                    mMoviePagedListAdapter.submitList(pagedList);
                    binding.rvMovie.setAdapter(mMoviePagedListAdapter);
                    // Restore the scroll position after setting up the adapter with the list of movies
                    binding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }

                // When offline, make the movie data view visible and show a snackbar message
                if (!isOnline()) {
                    showMovieDataView();
                    showSnackbarOffline();
                }
            }
        });
    }

    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void showSnackbarOffline() {
        Snackbar snackbar = Snackbar.make(
                binding.frameMain, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set background color of the snackbar
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void showNetworkDialog(final boolean isOnline) {
        if (!isOnline) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog_Alert);
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(getString(R.string.no_network_title));
            builder.setMessage(getString(R.string.no_network_message));
            builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DIALOG);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showMovieDataView() {
        binding.tvEmpty.setVisibility(View.INVISIBLE);
        binding.rvMovie.setVisibility(View.VISIBLE);
    }

}