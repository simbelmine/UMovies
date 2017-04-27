package com.example.android.umovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.umovies.utilities.DataUtils;

import java.net.URL;

class FetchMovieReviewsTaskLoader extends AsyncTaskLoader<Movie> {
    private Context context;
    private Movie movie;
    private String path;
    private String loaderId;
    private Bundle args;


    public FetchMovieReviewsTaskLoader(Context context, Bundle args) {
        super(context);
        this.context = context;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (args == null) {
            return;
        }

        path = args.getString("path");
        movie = args.getParcelable("movie");
        loaderId = args.getString("loaderId");

        if ((loaderId == DetailsActivity.MOVIE_TRAILERS_LOADER_ID && movie.getTrailers() != null) ||
                (loaderId == DetailsActivity.MOVIE_REVIEW_LOADER_ID && movie.getReviewAuthor() != null)) {
            deliverResult(movie);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie loadInBackground() {
        String[] arr = path.split("/");

        URL url = DataUtils.getDBUrl(context, -1, arr);
        if (url == null) {
            return null;
        }

        try {
            String response = DataUtils.getResponseFromHTTP(context, url);
            if(loaderId == DetailsActivity.MOVIE_REVIEW_LOADER_ID) {
                movie = DataUtils.getMovieReviewData(movie, response);
            }
            else if(loaderId == DetailsActivity.MOVIE_TRAILERS_LOADER_ID) {
                movie = DataUtils.getMovieTrailerKeys(movie, response);
            }

            return movie;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Movie data) {
        movie = data;
        super.deliverResult(data);
    }
}
