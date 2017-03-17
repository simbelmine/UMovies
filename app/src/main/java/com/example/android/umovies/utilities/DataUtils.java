package com.example.android.umovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

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
import java.util.List;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class DataUtils {

    public static URL getDBUrl(Context context, String id) {
        String baseUrl = context.getResources().getString(R.string.DATA_BASE_URL);
        String paramPopular = context.getResources().getString(R.string.PARAM_POPULAR);
        String paramTopRated = context.getResources().getString(R.string.PARAM_TOP_RATED);
        String apiKey = context.getResources().getString(R.string.API_KEY);
        String queryStr = context.getResources().getString(R.string.QUERY_STR);
        URL url = null;

        String paramToAdd;
        if(id == null) {
            paramToAdd = paramPopular;
        }
        else {
            paramToAdd = id;
        }

        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(paramToAdd)
                .appendQueryParameter(queryStr, apiKey)
                .build();

        try {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static final int  RESPONSE_LOWER_LIMIT = 200;
    private static final int  RESPONSE_UPPER_LIMIT = 300;
    public static String getResponseFromHTTP(final Context context, URL url) {
        // Test URL : url = new URL("http://www.google.com:81");
        String resultJsonStr = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && (response.code() >= RESPONSE_LOWER_LIMIT && response.code() < RESPONSE_UPPER_LIMIT)) {
                resultJsonStr = response.body().string();
            } else {
                showHttpFailureMsg(context);
            }
        }
        catch (IOException ex) {
            showHttpFailureMsg(context);
        }

        return resultJsonStr;
    }

    private static void showHttpFailureMsg(Context context) {
        Activity activity = ((Activity)context);
        View view = activity.findViewById(R.id.cl_main_container);
        showSnackbarMessage(context, view, context.getResources().getString(R.string.http_fail_msg));
    }

    public static List<Movie> getMovieListDataFromJson(String jsonStr) throws JSONException {
        final String RES_LIST = "results";

        List<Movie> movieList = new ArrayList<>();
        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(RES_LIST);

        populateMovieData(moviesArray, movieList);

        return movieList;
    }

    private static void populateMovieData(JSONArray moviesArray, List<Movie> movieList) throws JSONException{
        final String ID = "id";
        final String TITLE = "original_title";
        final String IMG_URL = "poster_path";
        final String SYNOPSIS = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
        final String VOTE_COUNT = "vote_count";

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieFromJson = moviesArray.getJSONObject(i);
            String id = movieFromJson.getString(ID);
            String title = movieFromJson.getString(TITLE);
            String imgUrl = movieFromJson.getString(IMG_URL);
            String synopsis = movieFromJson.getString(SYNOPSIS);
            String releaseDate = movieFromJson.getString(RELEASE_DATE);
            String rating = movieFromJson.getString(RATING);
            String voteCount = movieFromJson.getString(VOTE_COUNT);

            Movie movie = new Movie.MovieBuilder(id, title, imgUrl)
                    .releaseDate(releaseDate)
                    .synopsis(synopsis)
                    .rating(rating)
                    .votes(voteCount)
                    .build();

            movieList.add(movie);
        }
    }


    public static Movie getMovieAdditionalData(Movie movie, String jsonStr) throws JSONException {
        final String GENRES = "genres";
        final String RUNTIME = "runtime";
        final String TAGLINE = "tagline";
        final String REVENUE = "revenue";
        List<String> genresList = new ArrayList<>();
        Movie newMovie;

        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray genresArray = movieJson.getJSONArray(GENRES);
        populateGenres(genresArray, genresList);
        String runtime = movieJson.getString(RUNTIME);
        String tagline = movieJson.getString(TAGLINE);
        String revenue = movieJson.getString(REVENUE);

        newMovie = new Movie.MovieBuilder(movie.getId(), movie.getTitle(), movie.getImageURL())
                .releaseDate(movie.getReleaseDate())
                .synopsis(movie.getSynopsis())
                .rating(movie.getRating())
                .votes(movie.getVotes())
                .runtime(runtime)
                .tagline(tagline)
                .revenue(revenue)
                .genres(genresList)
                .build();

        return newMovie;
    }

    private static void populateGenres(JSONArray genresArray, List<String> genresList) throws JSONException {
        final String GENRE_NAME = "name";

        for(int i = 0; i < genresArray.length(); i++) {
            JSONObject currGenre = genresArray.getJSONObject(i);
            String genreName = currGenre.getString(GENRE_NAME);
            genresList.add(genreName);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showSnackbarMessage(Context context, View view, String message) {
        Snackbar errSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        setSnackbarTextColor(context, errSnackbar);
        errSnackbar.show();
    }

    private static void setSnackbarTextColor(Context context, Snackbar snackbar) {
        View snackbarView = snackbar.getView();
        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(context.getResources().getColor(R.color.colorAccent));
    }

    private static  List<Movie> movieListGlobal;

    public static  List<Movie> getMovieList() {
        return movieListGlobal;
    }

    public static  void setMovieList(List<Movie> movieList) {
        movieListGlobal = movieList;
    }

    public static void updateMovie(Movie newMovie, int pos) {
        newMovie.setIsFullyUpdated(true);
        movieListGlobal.set(pos, newMovie);
    }
}
