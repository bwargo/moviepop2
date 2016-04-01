package com.example.bwargo.moviepop.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.bwargo.moviepop";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";


    /* Inner class that defines the table contents of the location table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_MOVIEDB_ID = "movie_id";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_TITLE = "title";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_BACKDROP = "backdrop_path";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_POSTER = "poster";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_OVERVIEW = "overview";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // This is MovieDB's unique id that we store
        public static final String COLUMN_RATING = "rating";

        // integer value 0: not a favorite, 1: favorite
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
