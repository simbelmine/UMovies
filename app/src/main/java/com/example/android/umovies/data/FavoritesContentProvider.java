package com.example.android.umovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.umovies.data.FavoriteMoviesContract.AUTHORITY;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME;
import static com.example.android.umovies.data.FavoriteMoviesContract.PATH_FAVORITES;

public class FavoritesContentProvider extends ContentProvider {
    public static final int FAVORITES = 100;
    public static final int FAVORITES_BY_ID = 101;
    private FavoriteMoviesDbHelper dbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITES + "/*", FAVORITES_BY_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new FavoriteMoviesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {
            case FAVORITES:
                returnCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case FAVORITES_BY_ID:
                String id = uri.getPathSegments().get(1);
                String selection_ = COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs_ = new String[]{id};

                returnCursor = db.query(TABLE_NAME,
                        projection,
                        selection_,
                        selectionArgs_,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                }
                else {
                    throw new android.database.SQLException("Fails to insert row into: " + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        int taskDeleted;

        switch (match) {
            case FAVORITES_BY_ID:
                String id = uri.getPathSegments().get(1);
                taskDeleted = db.delete(TABLE_NAME, COLUMN_MOVIE_ID + "=?", new String[]{id});
                break;
            case FAVORITES:
                taskDeleted = 0;
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if(taskDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return taskDeleted;
    }
}
