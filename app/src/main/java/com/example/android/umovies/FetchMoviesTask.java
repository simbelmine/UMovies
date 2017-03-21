package com.example.android.umovies;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by Sve on 3/17/17.
 */

public class FetchMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
    private Context context;
    private List<Movie> moviesList;
    private int fragmentPosition;
    private FetchMoviesTaskCompleteListener<Movie> listener;

    public FetchMoviesTask(Context context, int fragmentPosition, FetchMoviesTaskCompleteListener<Movie> listener) {
        this.context = context;
        this.fragmentPosition = fragmentPosition;
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {
        URL url = DataUtils.getDBUrl(context, fragmentPosition, null);

        try {
            String response = DataUtils.getResponseFromHTTP(context, url);
            moviesList = DataUtils.getMovieListDataFromJson(response);

            return moviesList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
        super.onPostExecute(movieList);
        listener.onTaskCompleted(movieList);
    }
}
