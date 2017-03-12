package com.example.android.umovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    public static final String TAG = "uMovies";
    private static final int GRID_COLUMNS_PORTRAIT = 2;
    private static final int GRID_COLUMNS_LANDSCAPE = 3;
    private RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            populateMovieList(movies);
        }
    }

    private void populateMovieList(List<Movie> movies) {
        if(movies != null && movies.size() > 0) {
            if(moviesAdapter != null) {
                moviesAdapter.populateMovies(movies);
            }
        }
    }

    private void setRecyclerViewAdapter() {
        moviesList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesList, this);
        moviesRView.setAdapter(moviesAdapter);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE)
            layoutManager = new GridLayoutManager(this, GRID_COLUMNS_LANDSCAPE);
        else
            layoutManager = new GridLayoutManager(this, GRID_COLUMNS_PORTRAIT);
        moviesRView.setLayoutManager(layoutManager);
    }

    private void initView() {
        moviesRView = (RecyclerView) findViewById(R.id.rv_movies);
    }

    @Override
    public void onItemClick() {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }
}
