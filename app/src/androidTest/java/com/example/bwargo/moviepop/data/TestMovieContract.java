package com.example.bwargo.moviepop.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by bwargo on 4/14/16.
 */
public class TestMovieContract extends AndroidTestCase{
    private static final long TEST_MOVIE_ID = 1419033600;

    public void testBuildMovieUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Movie id not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Movie location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.bwargo.moviepop/movie/1419033600");
    }

    public void testGetIdFromUri(){
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MovieContract.",
                movieUri);

    }
}
