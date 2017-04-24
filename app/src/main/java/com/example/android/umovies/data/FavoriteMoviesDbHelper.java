package com.example.android.umovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sve on 4/23/17.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME + "(" +
                FavoriteMoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_IMG_URL + " TEXT NOT NULL," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RUNTIME + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVENUE + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_GENRES + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_AUTHORS + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_CONTENTS + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_RATINGS + " TEXT," +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
