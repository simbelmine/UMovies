package com.example.android.umovies.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.umovies.Movie;
import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;

public class FetchSingleMovieTask extends AsyncTask<Void, Void, Movie> {
    private Context context;
    private String movieId;
    private Movie movie;
    private FetchSingleMovieTaskCompleteListener<Movie> listener;

    public FetchSingleMovieTask(Context context, FetchSingleMovieTaskCompleteListener<Movie> listener,  String movieId, Movie movie) {
        this.context = context;
        this.listener = listener;
        this.movieId = movieId;
        this.movie = movie;
    }

    @Override
    protected Movie doInBackground(Void... params) {
        if(movieId != null && movie != null) {
            URL url = DataUtils.getDBUrl(context, -1, new String[]{movieId});

            try {
                String response = DataUtils.getResponseFromHTTP(context, url);
                Movie movieWithAdditionalData = DataUtils.getMovieAdditionalData(movie, response);

                return movieWithAdditionalData;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
        listener.onTaskCompleted(movie);
    }
}
