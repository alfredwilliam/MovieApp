package com.alfred.movieapp.ui.topRatingFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.alfred.movieapp.data.MovieDataSourceFactory;
import com.alfred.movieapp.data.MovieRepository;
import com.alfred.movieapp.model.Movie;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.alfred.movieapp.utilities.Constant.INITIAL_LOAD_SIZE_HINT;
import static com.alfred.movieapp.utilities.Constant.NUMBER_OF_FIXED_THREADS_FIVE;
import static com.alfred.movieapp.utilities.Constant.PAGE_SIZE;
import static com.alfred.movieapp.utilities.Constant.PREFETCH_DISTANCE;

public class TopRatingViewModel extends ViewModel {


    private final MovieRepository mRepository;

    private LiveData<PagedList<Movie>> mMoviePagedList;
    private String mSortCriteria;

    public TopRatingViewModel(MovieRepository repository, String sortCriteria) {
        mRepository = repository;
        mSortCriteria = sortCriteria;
        init(sortCriteria);
    }

    private void init(String sortCriteria) {
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_FIXED_THREADS_FIVE);

        MovieDataSourceFactory movieDataFactory = new MovieDataSourceFactory(sortCriteria);

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .build();

        mMoviePagedList = new LivePagedListBuilder<>(movieDataFactory, config)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<PagedList<Movie>> getMoviePagedList() {
        return mMoviePagedList;
    }

    public void setMoviePagedList(String sortCriteria) {
        init(sortCriteria);
    }
}