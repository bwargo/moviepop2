package com.example.bwargo.moviepop.data;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.adapters.FavoritesAdapter;
import com.example.bwargo.moviepop.fragment.MainActivityFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieLoader extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieLoader.class.getSimpleName();
    private static final int MOVIES_LOADER = 0;
    private FavoritesAdapter mFavoritesAdapter;

    public MovieLoader() {
        super();
    }

    private static final String[] FAVORITE_MOVIES_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIEDB_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_BACKDROP,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    // These indices are tied to MOVIES_COLS.  If MOVIES_COLS changes, these must change.
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_BACKDROP = 3;
    public static final int COL_MOVIE_POSTER = 4;
    public static final int COL_MOVIE_OVERVIEW = 5;
    public static final int COL_MOVIE_RATING = 6;
    public static final int COL_MOVIE_RELEASE_DATE = 7;
    public static final int COL_MOVIE_FAVORITE = 8;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader MoviePop2");
        String sortOrder = MovieContract.MovieEntry._ID + " DESC";
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                FAVORITE_MOVIES_COLUMNS,
                MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?",
                new String[]{"1"},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "onLoadFinished MoviePop2");
        if(data.getCount() > 0) {
            mFavoritesAdapter.swapCursor(data);
        } else{
            Toast toast = Toast.makeText(getActivity(), "No favorites found, please select some",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset MoviePop2");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate MoviePop2");
        super.onCreate(savedInstanceState);
        // Handle Menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated MoviePop2");
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView MoviePop2");
        // The CursorAdapter will take data from our cursor and populate the ListView.
        mFavoritesAdapter = new FavoritesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        GridView mGridView = (GridView) rootView.findViewById(R.id.fragment_gridview);
        mGridView.setAdapter(mFavoritesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mFavoritesAdapter.getCursor();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", cursor.getString(COL_MOVIE_ID));
                    jsonObject.put("original_title", cursor.getString(COL_MOVIE_TITLE));
                    jsonObject.put("overview", cursor.getString(COL_MOVIE_OVERVIEW));
                    jsonObject.put("vote_average", String.format("%.02f", cursor.getFloat(COL_MOVIE_RATING)));
                    jsonObject.put("release_date", cursor.getString(COL_MOVIE_RELEASE_DATE));
                    jsonObject.put("poster_path", cursor.getString(COL_MOVIE_POSTER));
                    jsonObject.put("backdrop_path", cursor.getString(COL_MOVIE_BACKDROP));
                    jsonObject.put("favorite", cursor.getInt(COL_MOVIE_FAVORITE));
                } catch (JSONException e) {
                    Log.e("casting JSON from DB", e.getLocalizedMessage());
                    e.fillInStackTrace();
                }
                ((MainActivityFragment.Callback) getActivity()).onItemSelected(jsonObject.toString());
            }
        });

        return rootView;
    }
}
