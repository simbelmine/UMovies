package com.example.android.umovies.data;

import android.provider.BaseColumns;

/**
 * Created by Sve on 4/22/17.
 */

public class FavoriteMoviesContract {
    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_MOVIE_IMG_URL = "imgUrl";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_VOTES = "votes";
        public static final String COLUMN_MOVIE_TAGLINE = "tagline";
        public static final String COLUMN_MOVIE_RUNTIME = "runtime";
        public static final String COLUMN_MOVIE_REVENUE = "revenue";
        public static final String COLUMN_MOVIE_GENRES = "genres";
        public static final String COLUMN_MOVIE_REVIEW_AUTHORS = "reviewAuthors";
        public static final String COLUMN_MOVIE_REVIEW_CONTENTS = "reviewContents";
        public static final String COLUMN_MOVIE_REVIEW_RATINGS = "reviewRatings";
        public static final String COLUMN_MOVIE_TRAILER_KEYS = "trailerKeys";
        public static final String COLUMN_MOVIE_TRAILER_NAMES = "trailerNames";
        public static final String COLUMN_MOVIE_TIMESTAMP = "timestamp";
    }
}
