package com.example.bwargo.moviepop.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieProvider extends ContentProvider {

    MovieDbHelper mMovieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int FAVORITES = 102;

    static UriMatcher buildUriMatcher(){

        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIES_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorites", FAVORITES);
        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                //retCursor = getFavoriteMovies(uri, projection, sortOrder);
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES_WITH_ID: {
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.TABLE_NAME +
                                "." + MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + " = ?",
                        new String[]{MovieContract.MovieEntry.getIdFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES: {
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.TABLE_NAME +
                                "." + MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?",
                        new String[]{"1"},
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if(null != context) {
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri returnUri;
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        switch (match) {
            // "movie"
            case MOVIES: {
                long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if(movieRowId > 0){
                    returnUri = MovieContract.MovieEntry.buildMovieUri(movieRowId);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if(null != context) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = buildUriMatcher().match(uri);
        int rowsUpdated;

        //if (selection == null) selection = "1";

        switch (match) {
            case MOVIES: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0) {
            Context context = getContext();
            if(null != context) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deletedRows;

        //if(selection == null) selection = "1";
        int match = sUriMatcher.match(uri);

        switch (match) {
            // "movie"
            case MOVIES: {
                deletedRows = mMovieDbHelper.getWritableDatabase().delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(deletedRows > 0 ){
            Context context = getContext();
            if(null != context) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return deletedRows;
    }
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : contentValues) {
                        //normalizeDate(value);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Context context = getContext();
                if (returnCount > 0) {
                    if (null != context) {
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }
}
