package com.example.android.umovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "uMovies";
    private static final int GRID_COLUMNS_PORTRAIT = 2;
    private static final int GRID_COLUMNS_LANDSCAPE = 3;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout noMoviesMessage;
    private RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbarBar();
        initView();
        setupRecyclerView();
        setRecyclerViewAdapter();
        fetchData();
    }

    private void fetchData() {
        new FetchMovieDataTask().execute(this);
    }

    private class FetchMovieDataTask extends AsyncTask<Context, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Context... params) {
            Context context = params[0];
            URL url = DataUtils.getDBUrl(context);

            try {
                String response = DataUtils.getResponseFromHTTP(url);
                moviesList = DataUtils.getMovieDataFromJson(response);

                return moviesList;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            swipeRefreshLayout.setRefreshing(false);
            if(movies != null && movies.size() > 0) {
                noMoviesMessage.setVisibility(View.INVISIBLE);
                populateMovieList(movies);
            }
            else {
                noMoviesMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void populateMovieList(List<Movie> movies) {
        if(moviesAdapter != null) {
            moviesAdapter.populateMovies(movies);
        }
    }

    private void setRecyclerViewAdapter() {
        moviesList = new ArrayList<>();
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
    }

    @Override
    public void onItemClick() {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    Toolbar toolbar;
    private void initToolbarBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.mipmap.app_icon);
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setPadding(0, ((int)getResources().getDimension(R.dimen.padding_from_top_toolbar)), 0, 0);
        }
        setSupportActionBar(toolbar);
    }
}
