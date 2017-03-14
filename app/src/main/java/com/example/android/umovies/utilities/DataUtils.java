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
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sve on 3/10/17.
 */

public final class DataUtils {

    public static URL getDBUrl(Context context, String id) {
        String baseUrl = context.getResources().getString(R.string.DATA_BASE_URL);
        String movieUrl = context.getResources().getString(R.string.DATA_SINGLE_MOVIE_URL);
        String apiKey = context.getResources().getString(R.string.API_KEY);
        String queryStr = context.getResources().getString(R.string.QUERY_STR);
        URL url = null;
        try {
            if(id == null) {
                url = new URL(baseUrl + queryStr + apiKey);
            }
            else {
                url = new URL(movieUrl + id + queryStr + apiKey);
            }
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
}
