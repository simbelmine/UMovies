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
    private static final int GRID_COLUMNS = 2;
    private RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<String> movieTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupRecyclerView();

        // TODO 2. Check if fetchData() is not better somwhere else
        // TODO 3. Set Adapter ONLY if needed
        fetchData();
        setRecyclerViewAdapter();
    }

    private void fetchData() {
        movieTitles = new ArrayList<>();
        movieTitles.add("AAaaaaaaa");
        movieTitles.add("Bbbb");
        movieTitles.add("CCCCCCcccc");
        movieTitles.add("DDdddd");
        movieTitles.add("Eeeeeeeee");
        movieTitles.add("Ffffff");
        movieTitles.add("GGggggggg");
        movieTitles.add("HHhHhhhh");
        movieTitles.add("IIiiii");
        movieTitles.add("JJjjjjjjjjjjj");


        new FetchMovieDataTask().execute(this);
    }

    private class FetchMovieDataTask extends AsyncTask<Context, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Context... params) {
            Context context = params[0];
            URL url = DataUtils.getDBUrl(context);

            try {
                String response = DataUtils.getResponseFromHTTP(url);
                List<Movie> moviesList = DataUtils.getMovieDataFromJson(context, response);

                return moviesList;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            // TODO 1. Populate Adapter with the new Data
            super.onPostExecute(movies);
        }
    }

    private void setRecyclerViewAdapter() {
        moviesAdapter = new MoviesAdapter(movieTitles, this);
        moviesRView.setAdapter(moviesAdapter);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS);
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
