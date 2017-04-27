package com.example.android.umovies.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.example.android.umovies.Movie;
import com.example.android.umovies.R;
import com.example.android.umovies.data.FavoriteMoviesContract;
import com.example.android.umovies.data.FavoriteMoviesDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class DataUtils {
    private static final int  RESPONSE_LOWER_LIMIT = 200;
    private static final int  RESPONSE_UPPER_LIMIT = 300;
    private static final String GENRES_SEPARATOR = "    ";
    private static final String RATING_SEPARATOR = "/";
    public static final String MOVIE_DATA_SEPARATOR = "##";
    public static final int TOTAL_COUNT_RATING_STARS = 5;

    public static URL getDBUrl(Context context, int fragmentPosition, String[] singleMoviePath) {
        String baseUrl = context.getResources().getString(R.string.DATA_BASE_URL);
        String apiKey = context.getResources().getString(R.string.API_KEY);
        String queryStr = context.getResources().getString(R.string.QUERY_STR);
        URL url = null;
        Uri uri;

        if(singleMoviePath == null || singleMoviePath.length == 0) {
            String paramToAdd = getUrlExtension(context, fragmentPosition);
            uri = Uri.parse(baseUrl).buildUpon()
                    .appendPath(paramToAdd)
                    .appendQueryParameter(queryStr, apiKey)
                    .build();
        }
        else {
            if(singleMoviePath.length > 1) {
                uri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(singleMoviePath[0])
                        .appendPath(singleMoviePath[1])
                        .appendQueryParameter(queryStr, apiKey)
                        .build();
            }
            else {
                uri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(singleMoviePath[0])
                        .appendQueryParameter(queryStr, apiKey)
                        .build();
            }
        }

        try {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static String getUrlExtension(Context context, int fragmentPosition) {
        String paramPopular = context.getResources().getString(R.string.PARAM_POPULAR);
        String paramTopRated = context.getResources().getString(R.string.PARAM_TOP_RATED);

        switch (fragmentPosition) {
            case 0:
                return paramPopular;
            case 1:
                return paramTopRated;
            default:
                return paramPopular;
        }
    }


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

    public static Movie getMovieReviewData(Movie movie, String jsonStr) throws JSONException {
        final String REVIEWS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String RATING = "rating";
        Movie newMovie;

            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray reviewsInfoArray = movieJson.getJSONArray(REVIEWS);

            List<String> authors = new ArrayList<>();
            List<String> contents = new ArrayList<>();
            List<String> ratings = new ArrayList<>();

            for (int i = 0; i < reviewsInfoArray.length(); i++) {
                JSONObject currJson = (JSONObject) reviewsInfoArray.get(i);

                String author = currJson.getString(AUTHOR);
                String content = currJson.getString(CONTENT);
                String rating ;
                try {
                   rating =  currJson.getString(RATING);
                }
                catch (JSONException ex) {
                    rating = "";
                }

                authors.add(author);
                contents.add(content);
                ratings.add(rating);
            }

        newMovie = new Movie.MovieBuilder(movie.getId(), movie.getTitle(), movie.getImageURL())
                .reviewAuthor(authors)
                .reviewContent(contents)
                .reviewRating(ratings)
                .build();

        return newMovie;
    }

    public static Movie getMovieTrailerKeys(Movie movie, String jsonStr) throws JSONException {
        final String RESULTS = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        List<String> keys = new ArrayList<>();
        List<String> names = new ArrayList<>();
        Movie newMovie;

        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray reviewsInfoArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewsInfoArray.length(); i++) {
            JSONObject currJson = (JSONObject) reviewsInfoArray.get(i);

            String key = currJson.getString(TRAILER_KEY);
            keys.add(key);

            String name = currJson.getString(TRAILER_NAME);
            names.add(name);
        }

        newMovie = new Movie.MovieBuilder(movie.getId(), movie.getTitle(), movie.getImageURL())
                .trailerKeys(keys)
                .trailerNames(names)
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


    private static SQLiteDatabase favoriteMoviesDB;
    public static void initFavoriteMoviesReadableDB(Context context) {
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(context);
        favoriteMoviesDB = dbHelper.getReadableDatabase();
    }

    public static long insertToDb(Context context, ContentValues cv) {
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(context);
        favoriteMoviesDB = dbHelper.getWritableDatabase();
        long res = favoriteMoviesDB.insert(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME, null, cv);
        favoriteMoviesDB.close();

        return res;
    }

    public static int deleteFromDb(Context context, String id) {
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(context);
        favoriteMoviesDB = dbHelper.getWritableDatabase();
        int res = favoriteMoviesDB.delete(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + id, null);
        favoriteMoviesDB.close();

        return res;
    }

    public static List<Movie> getFavoriteMoviesListFromSQLite(Context context) {
        DataUtils.initFavoriteMoviesReadableDB(context);

        List<Movie> movieList = new ArrayList<>();
        Cursor cursor = getFavoriteMovies();

        if(cursor != null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                Movie currMovie = getMovieFromCursor(cursor);
                movieList.add(currMovie);
                cursor.moveToNext();
            }
        }
        cursor.close();
        favoriteMoviesDB.close();

        return movieList;
    }

    private static Movie getMovieFromCursor(Cursor cursor) {
        String movieId = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID));
        String movieName = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_NAME));
        String movieImgUrl = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_IMG_URL));
        String movieSynopsis = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS));
        String movieReleaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE));
        String movieRating = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING));
        String movieVotes = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES));
        String movieTagline = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE));
        String movieRuntime = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RUNTIME));
        String movieRevenue = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVENUE));
        String movieGenres = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_GENRES));
        String movieReviewAuthors = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_AUTHORS));
        String movieReviewContents = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_CONTENTS));
        String movieReviewRatings = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_RATINGS));
        String movieTrailerKeys = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TRAILER_KEYS));
        String movieTrailerNames = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TRAILER_NAMES));

        List<String> listGenres = getListFromString(movieGenres);
        List<String> listReviewAuthors = getListFromString(movieReviewAuthors);
        List<String> listReviewContents = getListFromString(movieReviewContents);
        List<String> listReviewRatings = getListFromString(movieReviewRatings);
        List<String> listTrailerKeys = getListFromString(movieTrailerKeys);
        List<String> listTrailerNames = getListFromString(movieTrailerNames);


        return new Movie.MovieBuilder(movieId, movieName, movieImgUrl)
                .synopsis(movieSynopsis)
                .releaseDate(movieReleaseDate)
                .rating(movieRating)
                .votes(movieVotes)
                .tagline(movieTagline)
                .runtime(movieRuntime)
                .revenue(movieRevenue)
                .genres(listGenres)
                .reviewAuthor(listReviewAuthors)
                .reviewContent(listReviewContents)
                .reviewRating(listReviewRatings)
                .trailerKeys(listTrailerKeys)
                .trailerNames(listTrailerNames)
                .build();
    }

    public static boolean isMovieInDB(Context context, String movieID) {
        initFavoriteMoviesReadableDB(context);
        String queryStr = "SELECT * FROM " +
                FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME +
                " WHERE " + FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID +
                " = " + movieID;

        Cursor cursor = favoriteMoviesDB.rawQuery(queryStr, null);
        if(cursor.getCount() <= 0) {
            cursor.close();
            favoriteMoviesDB.close();
            return false;
        }

        cursor.close();
        favoriteMoviesDB.close();
        return true;
    }

    private static List<String> getListFromString(String str) {
        String[] genres = str.split(MOVIE_DATA_SEPARATOR);

        List<String> list = Arrays.asList(genres);
        return new ArrayList<>(list);
    }

    public static String getSeparatedStringFromList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int size = list.size();

        for(int i = 0; i < size; i++) {
            stringBuilder.append(list.get(i));
            if(i < size-1) {
                stringBuilder.append(MOVIE_DATA_SEPARATOR);
            }
        }

        return stringBuilder.toString();
    }

    private static Cursor getFavoriteMovies() {
        return favoriteMoviesDB.query(
                FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TIMESTAMP
        );
    }

    public static String getRuntime(String originalStr) {
        int runtimeNum = Integer.valueOf(originalStr);
        int hours = Math.round(runtimeNum/60);
        int minutes = runtimeNum - (hours*60);

        return hours + " hr. " + minutes + " min.";
    }

    public static String getRevenue(String original) {
        int billion = 1000000000;
        int million = 1000000;
        int thousand = 1000;
        int revenueAmount = Integer.valueOf(original);
        double result;
        String postfix;

        if(revenueAmount/billion > 0) {
            result = revenueAmount/(double)billion;
            postfix = "B";
        }
        else if(revenueAmount/million > 0) {
            result = revenueAmount/(double)million;
            postfix = "M";
        }
        else if(revenueAmount/thousand > 0) {
            result = revenueAmount/(double)thousand;
            postfix = "k";
        }
        else {
            result = revenueAmount;
            postfix = "";
        }

        result = (double)Math.round(result * 10d) / 10d;

        if(isZeroAfterFloatingPoint(result)){
            return "$" + (int)result + postfix;
        }

        return "$" + result + postfix;
    }

    private static boolean isZeroAfterFloatingPoint(double val) {
        double res = val - Math.floor(val);
        res = (res%1.0)*10;

        if(res == 0)
            return true;

        return false;
    }

    public static String getGenres(List<String> genres) {
        StringBuilder genresStr = new StringBuilder();

        for(String g : genres) {
            genresStr.append(g + GENRES_SEPARATOR);
        }

        return genresStr.toString();
    }

    public static String getGenresForDB(String genres) {
        String[] arrGenres = genres.split(GENRES_SEPARATOR);
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < arrGenres.length; i++) {
            strBuilder.append(arrGenres[i]);
            if(i < arrGenres.length-1){
                strBuilder.append(DataUtils.MOVIE_DATA_SEPARATOR);
            }
        }

        return strBuilder.toString();
    }

    public static String getRating(String rating) {
        double ratingVal = Double.valueOf(rating);
        double result = (double)Math.round((ratingVal/2) * 10d) / 10d;
        String ratingStr;

        if(isZeroAfterFloatingPoint(result)) {
            ratingStr = String.valueOf((int) result);
        }
        else {
            ratingStr = String.valueOf(result);
        }

        return ratingStr + RATING_SEPARATOR + TOTAL_COUNT_RATING_STARS;
    }

    public static String getRatingForDB(String rating) {
        String[] arrRating = rating.split(RATING_SEPARATOR);
        return arrRating[0];
    }
}
