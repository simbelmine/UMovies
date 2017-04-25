package com.example.android.umovies.utilities;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.umovies.MoviesRVHolder;
import com.example.android.umovies.transformations.BlurTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageUtils {
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";
    private static final int BLUR_RADIUS = 25;

    public static String getImageUrl(String shortUrl) {
        return BASE_IMAGE_URL + POSTER_SIZE + shortUrl;
    }

    public static void loadImageWithPicasso(Context context, String url, View view, boolean isBlurred) {
        Picasso picasso = Picasso.with(context);
        if(isBlurred) {
            picasso.load(url).fit().transform(new BlurTransformation(context, BLUR_RADIUS)).into((ImageView) view);
        }
        else {
            picasso.load(url).fit().into((ImageView) view);
        }
    }
}
