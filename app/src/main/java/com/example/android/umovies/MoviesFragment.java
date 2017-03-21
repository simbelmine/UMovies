package com.example.android.umovies;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.android.umovies.utilities.DataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesFragment extends Fragment  implements
        ItemClickListener, SwipeRefreshLayout.OnRefreshListener, FetchMoviesTaskCompleteListener<Movie>{
    public static final String TAG = "uMovies";
    private static final int GRID_COLUMNS_PORTRAIT = 2;
    private static final int GRID_COLUMNS_LANDSCAPE = 3;
    public static final String MOVIE_OBJ = "MovieObj";
    public static final String MOVIE_POS = "MoviePosition";
    private static final String MOVIES_LIST_OBJ = "MoviesListObj";
    @BindView(R.id.cl_main_container) FrameLayout mainContainer;
    @BindView(R.id.srl_movies_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rl_no_movies_container) RelativeLayout noMoviesMessage;
    @BindView(R.id.rv_movies) RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;
    private int fragmentPosition;
    private Context context;

    public static String POSITION = "POSITION";

    public MoviesFragment(){}
    public static MoviesFragment newInstance(int position) {
        MoviesFragment moviesFragment = new MoviesFragment();

        Bundle args = new Bundle();
        args.putInt("pos", position);
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
        fetchData(fragmentPosition);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_OBJ, (ArrayList<? extends Parcelable>) moviesList);
        outState.putInt(POSITION, fragmentPosition);
        super.onSaveInstanceState(outState);
    }

    private void getSavedInstanceStates(Bundle savedInstanceState) {
        updateTabPosition(savedInstanceState);
        updateMovieList(savedInstanceState);
    }

    private void updateTabPosition(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            fragmentPosition = savedInstanceState.getInt(POSITION);
        }
        else {
            Bundle bundle = getArguments();
            int position = bundle.getInt("pos");
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

    private void fetchData(int fragmentPosition) {
        if(moviesList == null || moviesList.size() == 0) {
            if (DataUtils.isOnline(context)) {
                new FetchMoviesTask(context, fragmentPosition, this).execute();
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

    @Override
    public void onTaskCompleted(List<Movie> movies) {
        moviesList = movies;
        swipeRefreshLayout.setRefreshing(false);
        if(movies != null && movies.size() > 0) {
            populateMovieList(movies);
            DataUtils.setMovieList(movies);
        }
    }

    private void populateMovieList(List<Movie> movies) {
//        if(moviesAdapter != null) {
//            moviesAdapter.populateMovies(movies);
//        }
//        else {
            setRecyclerViewAdapter();
//        }
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
        }

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        moviesList = null;
        fetchData(fragmentPosition);
    }
}
