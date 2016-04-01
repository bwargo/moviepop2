package com.example.bwargo.moviepop.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.adapters.CustomListAdapter;
import com.example.bwargo.moviepop.model.MovieResponseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchMovieData extends AsyncTask<String, Void, ArrayList<MovieResponseParser>> {

    private final String LOG_TAG = FetchMovieData.class.getSimpleName();

    public static final String SEPERATOR = "\n";
    String sortBy = "popularity.desc";
    CustomListAdapter customListAdapter;
    Context context;

    public FetchMovieData(Context context, CustomListAdapter customListAdapter) {
        this.context = context;
        this.customListAdapter = customListAdapter;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private ArrayList<MovieResponseParser> getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ROOT_MOVIES_LIST = "results";

        // Define json paths
        final String MOVIE_ID = "id";
        final String MOVIE_POSTER = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String PLOT_SYNOPSIS = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(forecastJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(ROOT_MOVIES_LIST);

        ArrayList<MovieResponseParser> resultMovies = new ArrayList<MovieResponseParser>(movieArray.length());
        for(int i = 0; i < movieArray.length(); i++) {
            String id;
            String imageLink;
            String originalTitle;
            String releaseDate;
            String userRating;
            String plotSynopsis;

            // Get the JSON object representing the movie
            JSONObject movieObject = movieArray.getJSONObject(i);

            id = movieObject.getString(MOVIE_ID);
            imageLink = movieObject.getString(MOVIE_POSTER);
            originalTitle = movieObject.getString(ORIGINAL_TITLE);
            releaseDate = movieObject.getString(RELEASE_DATE);
            userRating = movieObject.getString(USER_RATING);
            plotSynopsis = movieObject.getString(PLOT_SYNOPSIS);

            resultMovies.add(new MovieResponseParser(
                    id + SEPERATOR +
                            imageLink + SEPERATOR +
                            originalTitle + SEPERATOR +
                            releaseDate + SEPERATOR +
                            userRating + SEPERATOR +
                            plotSynopsis))
            ;

        }

        for (MovieResponseParser s : resultMovies) {
            Log.v(LOG_TAG, "Movie Poster: " + s);
        }
        return resultMovies;
    }

   /*     @Override
        protected void onPostExecute(ArrayList<Movie> result){

            if(result != null){
                customListAdapter.clear();
                customListAdapter.addAll(result);
            }
        }*/

    @Override
    protected ArrayList<MovieResponseParser> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieDataJsonStr = null;

        try {

            final String MOVIEDATA_BASE_URL = BuildConfig.BASE_URL_THEMOVIEDB;
            final String SORTBY_PARAM = "sort_by";
            final String APPKEY = "api_key";

            Uri builtURI = Uri.parse(MOVIEDATA_BASE_URL).buildUpon()
                    .appendQueryParameter(SORTBY_PARAM, sortBy)
                    .appendQueryParameter(APPKEY, String.valueOf(R.string.API_THEMOVIEDB_ORG_KEY))
                    .build();

            URL url = new URL(builtURI.toString());

            Log.v(LOG_TAG, "Built URI " + builtURI.toString());

            // Create the request to OpenWeatherMap, and open the connection
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
            Log.v(LOG_TAG, "Move Data Json Str: " + movieDataJsonStr);

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

    @Override
    protected void onPostExecute(ArrayList<MovieResponseParser> result) {
        Log.v(LOG_TAG, " onPostExecute");

        //customListAdapter.movies = (result);
        customListAdapter.notifyDataSetChanged();

    }

}
