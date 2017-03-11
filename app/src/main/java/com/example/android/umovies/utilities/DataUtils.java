package com.example.android.umovies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.umovies.MainActivity;
import com.example.android.umovies.Movie;
import com.example.android.umovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sve on 3/10/17.
 */

public final class DataUtils {

    public static URL getDBUrl(Context context) {
        String baseUrl = context.getResources().getString(R.string.DATA_BASE_URL);
        String apiKey = context.getResources().getString(R.string.API_KEY);
        URL url = null;
        try {
            url = new URL(baseUrl + apiKey);
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHTTP(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            InputStream input = connection.getInputStream();
            Scanner scanner = new Scanner(input);
            scanner.useDelimiter("\\A");

            boolean hasNext = scanner.hasNext();
            if(hasNext) {
                return scanner.next();
            }
            else {
                return null;
            }
        }
        finally {
            connection.disconnect();
        }
    }

    public static List<Movie> getMovieDataFromJson(Context context, String jsonStr) throws JSONException {
        final String TITLE = "original_title";
        final String IMG_URL = "poster_path";
        final String SYNOPSIS = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
        final String RES_LIST = "results";

        List<Movie> movieList = null;
        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(RES_LIST);

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String title = movie.getString(TITLE);
            String imgUrl = movie.getString(IMG_URL);
            String synopsis = movie.getString(SYNOPSIS);
            String releaseDate = movie.getString(RELEASE_DATE);
            String rating = movie.getString(RATING);

            Log.v(MainActivity.TAG, title + "\n" + imgUrl + "\n" + synopsis + "\n" + releaseDate + "\n" + rating);
        }

        return movieList;
    }

}
