package com.example.android.umovies.utilities;

import android.view.View;
import android.widget.ProgressBar;

import com.example.android.umovies.MoviesRVHolder;
import com.squareup.picasso.Callback;

/**
 * Created by Sve on 3/12/17.
 */

public class ImageUtils {
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    public static String getImageUrl(String shortUrl) {
        return BASE_IMAGE_URL + POSTER_SIZE + shortUrl;
    }

    public static Callback getImageCallback(final ProgressBar progressBar) {
        return new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError() {

            }
        };
    }
}
