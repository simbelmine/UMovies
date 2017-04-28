package com.example.android.umovies;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.android.umovies.loaders.FetchMovieTaskLoader;
import com.example.android.umovies.utilities.DataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesFragment extends Fragment  implements
        ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int GRID_COLUMNS_PORTRAIT = 2;
    private static final int GRID_COLUMNS_LANDSCAPE = 3;
    public static final String MOVIE_OBJ = "MovieObj";
    public static final String MOVIE_POS = "MoviePosition";
    private static final String MOVIES_LIST_OBJ = "MoviesListObj";
    public static String FRAGMENT_POSITION = "fragmentPosition";
    public static int FAVORITES_FRAGMENT_POSITION = 2;
    private static final String MOVIES_LOADER_ID = "LoaderId";
    @BindView(R.id.cl_main_container) FrameLayout mainContainer;
    @BindView(R.id.srl_movies_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rl_no_movies_container) RelativeLayout noMoviesMessage;
    @BindView(R.id.rv_movies) RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;
    private int fragmentPosition;
    private Context context;
    private int loaderId;
    private LoaderManager loaderManager;

    public MoviesFragment(){}
    public static MoviesFragment newInstance(int position) {
        MoviesFragment moviesFragment = new MoviesFragment();

        Bundle args = new Bundle();
        args.putInt(FRAGMENT_POSITION, position);
        args.putInt(MOVIES_LOADER_ID, position);
        moviesFragment.setArguments(args);

        return moviesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_fragment, container, false);
        ButterKnife.bind(this, view);

        initView();
        setupRecyclerView();
        getSavedInstanceStates(savedInstanceState);
        initLoader();
        fetchData(fragmentPosition);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_OBJ, (ArrayList<? extends Parcelable>) moviesList);
        outState.putInt(MOVIES_LOADER_ID, loaderId);
        outState.putInt(FRAGMENT_POSITION, fragmentPosition);
        super.onSaveInstanceState(outState);
    }

    private void getSavedInstanceStates(Bundle savedInstanceState) {
        updateLoaderId(savedInstanceState);
        updateTabPosition(savedInstanceState);
        updateMovieList(savedInstanceState);
    }

    private void updateLoaderId(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIES_LOADER_ID)) {
            loaderId = savedInstanceState.getInt(MOVIES_LOADER_ID);
        }
        else {
            int id = getIntBundleExtra(MOVIES_LOADER_ID);
            loaderId = id;
        }
    }

    private void updateTabPosition(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_POSITION)) {
            fragmentPosition = savedInstanceState.getInt(FRAGMENT_POSITION);
        }
        else {
            int position = getIntBundleExtra(FRAGMENT_POSITION);
            fragmentPosition = position;
        }
    }

    private void updateMovieList(Bundle savedInstanceState) {
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_LIST_OBJ)) {
            moviesList = new ArrayList<>();
        }
        else {
            moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_OBJ);
            setRecyclerViewAdapter();
        }
    }

    private int getIntBundleExtra(String extraName) {
        Bundle bundle = getArguments();
        return bundle.getInt(extraName);
    }


    private void fetchData(int fragmentPosition) {
        if(moviesList == null || moviesList.size() == 0) {
            if (DataUtils.isOnline(context) || fragmentPosition == FAVORITES_FRAGMENT_POSITION) {
                loadFetchedMovies(fragmentPosition);
                moviesRView.setVisibility(View.VISIBLE);
                noMoviesMessage.setVisibility(View.INVISIBLE);
            } else {
                moviesRView.setVisibility(View.INVISIBLE);
                noMoviesMessage.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                DataUtils.showSnackbarMessage(context, mainContainer, getResources().getString(R.string.no_network));
            }
        }
    }

    private void loadFetchedMovies(int fragmentPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_POSITION, fragmentPosition);

        Loader<List<Movie>> loader = getLoader();
        if(loader == null) {
            loaderManager.initLoader(loaderId, bundle, this);
        }
        else {
            loaderManager.restartLoader(loaderId, bundle, this);
        }
    }

    private Loader<List<Movie>> getLoader() {
        return loaderManager.getLoader(loaderId);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMovieTaskLoader(context, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        moviesList = movies;

        swipeRefreshLayout.setRefreshing(false);
        if(movies != null && movies.size() > 0) {
            populateMovieList();
            DataUtils.setMovieList(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private void populateMovieList() {
        setRecyclerViewAdapter();
    }

    private void setRecyclerViewAdapter() {
        moviesAdapter = new MoviesAdapter(context, moviesList, this);
        moviesRView.setAdapter(moviesAdapter);
    }

    private void setupRecyclerView() {
        final GridLayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE)
            layoutManager = new GridLayoutManager(context, GRID_COLUMNS_LANDSCAPE);
        else
            layoutManager = new GridLayoutManager(context, GRID_COLUMNS_PORTRAIT);
        moviesRView.setLayoutManager(layoutManager);
    }

    private void initLoader() {
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(loaderId, null, this);
    }

    private void initView() {
        context = getActivity();
        swipeRefreshLayout.setOnRefreshListener(this);

        if(Build.VERSION.SDK_INT >= 21) {
            mainContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }

    @Override
    public void onItemClick(int position) {
        Movie currMovie;
        Intent intent = new Intent(context, DetailsActivity.class);

        if(moviesList != null) {
            currMovie = moviesList.get(position);
            intent.putExtra(MOVIE_OBJ, currMovie);
            intent.putExtra(MOVIE_POS, position);
            intent.putExtra(FRAGMENT_POSITION, fragmentPosition);
        }

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        moviesList = null;
        fetchData(fragmentPosition);
    }
}
