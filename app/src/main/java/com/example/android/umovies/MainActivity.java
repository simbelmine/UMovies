package com.example.android.umovies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.android.umovies.utilities.DataUtils;
import com.example.android.umovies.utilities.WindowUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ItemClickListener, SwipeRefreshLayout.OnRefreshListener, FetchMoviesTaskCompleteListener<Movie> {
    public static final String TAG = "uMovies";
    private static final int GRID_COLUMNS_PORTRAIT = 2;
    private static final int GRID_COLUMNS_LANDSCAPE = 3;
    public static final String MOVIE_OBJ = "MovieObj";
    public static final String MOVIE_POS = "MoviePosition";
    private static final String MOVIES_LIST_OBJ = "MoviesListObj";
    private FrameLayout mainContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout noMoviesMessage;
    private RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.initToolbarBar(this);
        initView();
        setupRecyclerView();
        getSavedInstanceStates(savedInstanceState);
        fetchData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_OBJ, (ArrayList<? extends Parcelable>) moviesList);
        super.onSaveInstanceState(outState);
    }

    private void getSavedInstanceStates(Bundle savedInstanceState) {
        updateMovieList(savedInstanceState);
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


    private void fetchData() {
        if(moviesList != null && moviesList.size() > 0) {
            moviesList = DataUtils.getMovieList();
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            if(DataUtils.isOnline(this)) {
                new FetchMoviesTask(this, this).execute();
                moviesRView.setVisibility(View.VISIBLE);
                noMoviesMessage.setVisibility(View.INVISIBLE);
            }
            else {
                moviesRView.setVisibility(View.INVISIBLE);
                noMoviesMessage.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                DataUtils.showSnackbarMessage(this, mainContainer, getResources().getString(R.string.no_network));
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
        if(moviesAdapter != null) {
            moviesAdapter.populateMovies(movies);
        }
        else {
            setRecyclerViewAdapter();
        }
    }

    private void setRecyclerViewAdapter() {
        moviesAdapter = new MoviesAdapter(this, moviesList, this);
        moviesRView.setAdapter(moviesAdapter);
    }

    private void setupRecyclerView() {
        final GridLayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE)
            layoutManager = new GridLayoutManager(this, GRID_COLUMNS_LANDSCAPE);
        else
            layoutManager = new GridLayoutManager(this, GRID_COLUMNS_PORTRAIT);
        moviesRView.setLayoutManager(layoutManager);
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_movies_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        noMoviesMessage = (RelativeLayout) findViewById(R.id.rl_no_movies_container);
        moviesRView = (RecyclerView) findViewById(R.id.rv_movies);
        mainContainer = (FrameLayout) findViewById(R.id.cl_main_container);

        if(Build.VERSION.SDK_INT >= 21) {
            mainContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }

    @Override
    public void onItemClick(int position) {
        Movie currMovie;
        Intent intent = new Intent(this, DetailsActivity.class);
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
        fetchData();
    }
}
