package com.example.bwargo.moviepop.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.activity.DetailActivity;
import com.example.bwargo.moviepop.adapters.CustomListAdapter;
import com.example.bwargo.moviepop.data.MovieContract;
import com.example.bwargo.moviepop.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    CustomListAdapter customListAdapter;
    private String SORT_BY;

    public MainActivityFragment() {}

    @Override
    public void onStart(){
        Log.v(LOG_TAG, "onStart Moviepop2");
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        Log.v(LOG_TAG, "onCreate Moviepop2");
        super.onCreate(savedInstanceBundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        SORT_BY = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_most_popular));
        Log.i(LOG_TAG, "onCreateView Moviepop2 with sort_by: "+SORT_BY);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView listView = (GridView) rootView.findViewById(R.id.fragment_gridview);

        customListAdapter = new CustomListAdapter(getActivity(), new ArrayList<Movie>());

        listView.setAdapter(customListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie extra = customListAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(extra);
                /*Movie movie = customListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", movie);
                startActivity(intent);*/
            }
        });
        //getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onOptionsItemSelected Moviepop2");
        int id = item.getItemId();
        if(id == R.id.settings){
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        Log.v(LOG_TAG, "updateMovies Moviepop2");
        if (SORT_BY.equals(getString(R.string.pref_sort_by_favorites))){
            Cursor movieCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?",
                    new String[]{"1"},
                    null);
            if(movieCursor != null) {
                if (movieCursor.moveToFirst()) {
                    int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                    long movieRowId = movieCursor.getLong(movieIdIndex);
                }
                movieCursor.close();
            }else{
                Toast toast = Toast.makeText(getActivity(), "No favorites found, please select some",Toast.LENGTH_SHORT);
                toast.show();
            }
        }else {
            FetchMovieData fetchMovieData = new FetchMovieData();
            fetchMovieData.execute(SORT_BY);
        }
    }

    public class FetchMovieData extends AsyncTask<String,Void,ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            //check for IllegalStateException prevention
            Activity activityCheck = getActivity();
            if (!isAdded() && activityCheck == null) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieDataJsonStr;
            String sortBy = (params[0] != null) ? params[0] : "popularity.desc";

            try {

                final String MOVIEDATA_BASE_URL = BuildConfig.BASE_URL_THEMOVIEDB;
                final String SORTBY_PARAM = "sort_by";
                final String APPKEY = "api_key";
                Uri builtURI;
                if(sortBy.equals("vote_average.desc")) {
                    /**
                     * Since we don't want to risk querying for 10.0 vote averages with only one
                     * vote (which could result in incomplete or missing data such as poster_path,
                     * trailers, etc), we will append additional query paramater "vote_count.gte"
                     *  greater than 1000 for more relevant results.
                     */
                     builtURI = Uri.parse(MOVIEDATA_BASE_URL).buildUpon()
                             .appendPath("top_rated")
                            .appendQueryParameter(SORTBY_PARAM, sortBy)
                             .appendQueryParameter("vote_count.gte","1000")
                            .appendQueryParameter(APPKEY, getString(R.string.API_THEMOVIEDB_ORG_KEY))
                            .build();
                }else{
                     builtURI = Uri.parse(MOVIEDATA_BASE_URL).buildUpon()
                             .appendPath("popular")
                            .appendQueryParameter(SORTBY_PARAM, sortBy)
                            .appendQueryParameter(APPKEY, getString(R.string.API_THEMOVIEDB_ORG_KEY))
                            .build();
                }

                Log.d(LOG_TAG, "Built URI " + builtURI.toString());
                URL url = new URL(builtURI.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDataJsonStr = buffer.toString();
                Log.d(LOG_TAG, "Move Data Json Str: " + movieDataJsonStr);

                return getMovieDataFromJson(movieDataJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } catch (JSONException jsonException) {
                Log.e(LOG_TAG, "JSON PARSE error", jsonException);
                jsonException.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray("results");
            ArrayList<Movie> movies = Movie.fromJson(resultsArray);

            for (Movie s : movies) {
                Log.d(LOG_TAG, "Movie entry: " + s.originalTitle + s.thumbnailPath);
            }
            return movies;

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result){
            Log.i(LOG_TAG, "onPostExecute Moviepop2");
            if(isAdded()) {
                if (result != null) {
                    customListAdapter.clear();
                    customListAdapter.addAll(result);
                }
            }
        }
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Serializable movieData);
    }
}
