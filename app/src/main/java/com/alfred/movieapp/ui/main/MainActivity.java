package com.alfred.movieapp.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alfred.movieapp.GridSpacingItemDecoration;
import com.alfred.movieapp.R;
import com.alfred.movieapp.data.MovieEntry;
import com.alfred.movieapp.data.MoviePreferences;
import com.alfred.movieapp.databinding.ActivityMainBinding;
import com.alfred.movieapp.model.Movie;
import com.alfred.movieapp.utilities.InjectorUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.alfred.movieapp.utilities.Constant.DRAWABLES_ZERO;
import static com.alfred.movieapp.utilities.Constant.EXTRA_MOVIE;
import static com.alfred.movieapp.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.alfred.movieapp.utilities.Constant.GRID_SPACING;
import static com.alfred.movieapp.utilities.Constant.GRID_SPAN_COUNT;
import static com.alfred.movieapp.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.alfred.movieapp.utilities.Constant.REQUEST_CODE_DIALOG;

public class MainActivity extends AppCompatActivity {

    public NavController navController;
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //NavigationUI.setupWithNavController(binding.bottomNavigation, navHostFragment.getNavController());
        NavigationUI.setupWithNavController(mMainBinding.bottomNavigation, navHostFragment.getNavController());
    }

}