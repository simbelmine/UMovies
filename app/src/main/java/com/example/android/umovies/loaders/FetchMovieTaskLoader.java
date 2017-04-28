package com.example.android.umovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.example.android.umovies.Movie;
import com.example.android.umovies.MoviesFragment;
import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;
import java.util.List;


public class FetchMovieTaskLoader extends AsyncTaskLoader<List<Movie>> {
    private Context context;
    private List<Movie> moviesList;
    private int fragmentPosition;
    private Bundle args;

    public FetchMovieTaskLoader(Context context, Bundle args) {
        super(context);
        this.context = context;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (args == null) {
            return;
        }

        fragmentPosition = args.getInt(MoviesFragment.FRAGMENT_POSITION);

        if (moviesList != null) {
            deliverResult(moviesList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {
        if(fragmentPosition == MoviesFragment.FAVORITES_FRAGMENT_POSITION) {
            moviesList = DataUtils.getFavoriteMoviesListFromSQLite(context);

            return moviesList;
        }
        else {
            URL url = DataUtils.getDBUrl(context, fragmentPosition, null);
            if (url == null) {
                return null;
            }

            try {
                String response = DataUtils.getResponseFromHTTP(context, url);
                moviesList = DataUtils.getMovieListDataFromJson(response);

                return moviesList;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void deliverResult(List<Movie> movies) {
        moviesList = movies;
        super.deliverResult(movies);
    }
}
