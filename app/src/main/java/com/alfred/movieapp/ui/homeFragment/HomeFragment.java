package com.alfred.movieapp.ui.homeFragment;

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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfred.movieapp.GridSpacingItemDecoration;
import com.alfred.movieapp.R;
import com.alfred.movieapp.databinding.HomeFragmentBinding;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.ui.detail.DetailActivity;
import com.alfred.movieapp.ui.main.FavoriteAdapter;
import com.alfred.movieapp.ui.main.MainActivity;
import com.alfred.movieapp.ui.main.MainActivityViewModel;
import com.alfred.movieapp.ui.main.MainViewModelFactory;
import com.alfred.movieapp.ui.main.MoviePagedListAdapter;
import com.alfred.movieapp.utilities.InjectorUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;
import static com.alfred.movieapp.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.alfred.movieapp.utilities.Constant.GRID_SPACING;
import static com.alfred.movieapp.utilities.Constant.GRID_SPAN_COUNT;
import static com.alfred.movieapp.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.alfred.movieapp.utilities.Constant.REQUEST_CODE_DIALOG;

public class HomeFragment extends Fragment implements MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler {

    private HomeViewModel mViewModel;
    HomeFragmentBinding binding;

    /** MoviePagedListAdapter enables for data to be loaded in chunks */
    private MoviePagedListAdapter mMoviePagedListAdapter;

    /** Member variable for restoring list items positions on device rotation */
    private Parcelable mSavedLayoutState;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        // Check if savedInstance is null not to recreate a dialog when rotating
        if (savedInstanceState == null) {
            // Show a dialog when there is no internet connection
            showNetworkDialog(isOnline());
        }


        // Set the LayoutManager to the RecyclerView and create MoviePagedListAdapter and FavoriteAdapter
        initAdapter();

        // Check if savedInstance is null not to recreate a dialog when rotating
        if (savedInstanceState == null) {
            // Show a dialog when there is no internet connection
            showNetworkDialog(isOnline());
        }

        // Get the sort criteria currently set in Preferences
        //mSortCriteria = MoviePreferences.getPreferredSortCriteria(this);



        // Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
        // SharedPreference has changed. Please note that we must unregister MainActivity as an
        // OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
        /*PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);*/

        // Set the color scheme of the SwipeRefreshLayout and setup OnRefreshListener
        setSwipeRefreshLayout();

        // Set column spacing to make each column have the same spacing
        setColumnSpacing();

        if (savedInstanceState != null) {
            // Get the scroll position
            mSavedLayoutState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            // Restore the scroll position
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
        // Set the colors used in the progress animation
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Set the listener to be notified when a refresh is triggered
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            /**
             * Called when a swipe gesture triggers a refresh
             */
            @Override
            public void onRefresh() {
                // Make the movie data visible and hide an empty message
                showMovieDataView();

                // When refreshing, observe the data and update the UI
                updateUI(getString(R.string.pref_sort_by_now_playing));

                // Hide refresh progress
                hideRefresh();

                // When online, show a snack bar message notifying updated
                showSnackbarRefresh(isOnline());
            }
        });
    }

    private void showSnackbarRefresh(boolean isOnline) {
        if (isOnline) {
            // Show snack bar message
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
        // TODO: Use the ViewModel

        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(
                getContext(), getString(R.string.pref_sort_by_now_playing));
        mViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
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

                // When offline, make the movie data view visible and show a snackbar message
                if (!isOnline()) {
                    showMovieDataView();
                    showSnackbarOffline();
                }
            }
        });
        //updateUI(getString(R.string.pref_sort_by_now_playing));



    }

    /**
     * Set the LayoutManager to the RecyclerView and create MoviePagedListAdapter and FavoriteAdapter
     */
    private void initAdapter() {
        // A GridLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a grid layout.
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), GRID_SPAN_COUNT);
        // Set the layout manager to the RecyclerView
        binding.rvMovie.setLayoutManager(layoutManager);

        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        binding.rvMovie.setHasFixedSize(true);
        binding.rvMovie.setAdapter(mMoviePagedListAdapter);

        // Create MoviePagedListAdapter
        mMoviePagedListAdapter = new MoviePagedListAdapter(this);
        // Create FavoriteAdapter that is responsible for linking favorite movies with the Views
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item clicks.
     *
     * @param movie The movie that was clicked
     */
    @Override
    public void onItemClick(Movie movie) {
        // Wrap the parcelable into a bundle
        // Reference: @see "https://stackoverflow.com/questions/28589509/android-e-parcel-
        // class-not-found-when-unmarshalling-only-on-samsung-tab3"
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(getContext(), DetailActivity.class);
        // Pass the bundle through Intent
        intent.putExtra(EXTRA_MOVIE, b);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);
    }

    /**
     * Get the MainActivityViewModel from the factory
     */
    private void setupViewModel(String sortCriteria) {
        HomeViewModelFactory factory = InjectorUtils.provideHomeViewModelFactory(
                getContext(), sortCriteria);
        mViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        updateUI(getString(R.string.pref_sort_by_now_playing));
    }

    /**
     * Update the UI depending on the sort criteria
     */
    private void updateUI(String sortCriteria) {



        observeMoviePagedList();
    }

    /**
     * Update the MoviePagedList from LiveData in MainActivityViewModel
     */
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

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void showSnackbarOffline() {
        Snackbar snackbar = Snackbar.make(
                binding.frameMain, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
        // Set background color of the snackbar
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set background color of the snackbar
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public void showNetworkDialog(final boolean isOnline) {
        if (!isOnline) {
            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog_Alert);
            // Set an Icon and title, and message
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

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showMovieDataView() {
        // First, hide an empty view
        binding.tvEmpty.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        binding.rvMovie.setVisibility(View.VISIBLE);
    }

}