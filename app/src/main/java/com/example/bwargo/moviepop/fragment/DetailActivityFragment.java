package com.example.bwargo.moviepop.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.adapters.CustomLinearLayoutAdapter;
import com.example.bwargo.moviepop.adapters.CustomReviewAdapter;
import com.example.bwargo.moviepop.model.Movie;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.model.Trailer;
import com.example.bwargo.moviepop.model.Utility;
import com.example.bwargo.moviepop.data.MovieContract;
import com.squareup.picasso.Picasso;

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


public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public static final String baseUrl = BuildConfig.IMAGE_BASE_URL;

    private Movie mMovie;
    private CustomLinearLayoutAdapter layoutAdapter;
    private CustomReviewAdapter mReviewAdapter;
    private ImageButton mFavoriteButton;
    private ImageButton mUnFavoriteButton;

    public DetailActivityFragment() {
            setHasOptionsMenu(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        //setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra("movie")) {
            mMovie = (Movie) intent.getSerializableExtra("movie");
            layoutAdapter = new CustomLinearLayoutAdapter(getActivity(), null);
            mReviewAdapter = new CustomReviewAdapter(getActivity(), null);
            updateTrailers();
            updateReviews();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if(mMovie != null) {
            ((TextView) rootView.findViewById(R.id.originalTitle)).setText(mMovie.originalTitle);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.backdrop);
            Picasso.with(getContext()).load(baseUrl + mMovie.backdrop).into(imageView);
            ((TextView) rootView.findViewById(R.id.overview)).setText(mMovie.overview);
            ((TextView) rootView.findViewById(R.id.rating)).setText(mMovie.rating);
            ((TextView) rootView.findViewById(R.id.release_date)).setText(mMovie.releaseDate);
            mFavoriteButton = (ImageButton) rootView.findViewById(R.id.favorite_button);
            mUnFavoriteButton = (ImageButton) rootView.findViewById(R.id.non_favorite_button);

            mMovie.favorite = queryFavorite(mMovie);
            toggleAsFavorite();
        }

        ListView mTrailersListView = (ListView) rootView.findViewById(R.id.trailer_list);
        mTrailersListView.setAdapter(layoutAdapter);

        mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = layoutAdapter.trailers.get(position).key;
                Utility.playYoutube(key, getActivity());
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMovie.favorite = 0;
                //toggleAsFavorite();
                updateMovieDatabase();
                Toast toast = Toast.makeText(getActivity(), "Movie removed from Favorites",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        mUnFavoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMovie.favorite = 1;
                //toggleAsFavorite();
                updateMovieDatabase();
                Toast toast = Toast.makeText(getActivity(), "Movie added to Favorites",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        /*mTrailersAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeightBasedOnChildren(mTrailersListView);
            }
        });*/

        ListView mReviewssListView = (ListView) rootView.findViewById(R.id.reviews_list);

        mReviewssListView.setAdapter(mReviewAdapter);

       /* mReviewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeightBasedOnChildren(mReviewssListView);
            }
        });*/
        return rootView;
    }

    private void toggleAsFavorite() {

        if(mMovie.getFavoriteAsBoolean()) {
            mUnFavoriteButton.setVisibility(View.GONE);
            mFavoriteButton.setVisibility(View.VISIBLE);
        }else{
            mFavoriteButton.setVisibility(View.GONE);
            mUnFavoriteButton.setVisibility(View.VISIBLE);
        }
    }

    private int queryFavorite(Movie mMovie) {
        int fav = 0;
        if(mMovie != null) {
            //First, check if a movie with the id exists
            Cursor movieCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_MOVIEDB_ID,
                            MovieContract.MovieEntry.COLUMN_FAVORITE
                    },
                    MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + " = ?",
                    new String[]{mMovie.getId()},
                    null);
            if(movieCursor != null) {
                if (movieCursor.moveToFirst()) {
                    Log.v(LOG_TAG, DatabaseUtils.dumpCursorToString(movieCursor));
                    int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
                    fav = movieCursor.getInt(movieIdIndex);
                }
                movieCursor.close();
            }
        }
        return fav;
    }

    private void updateMovieDatabase() {
        long movieRowId;
        if(mMovie != null) {
            //First, check if a movie with the id exists
            Cursor movieCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_MOVIEDB_ID
                    },
                    MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + " = ?",
                    new String[]{mMovie.getId()},
                    null);

            if (movieCursor.moveToFirst()) {
                //if it exists, we simply update the Favorite column
                int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                movieRowId = movieCursor.getLong(movieIdIndex);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, mMovie.favorite);
                int updatedId = getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{Long.toString(movieRowId)});
            } else {
                // Create the movie content values to insert
                ContentValues movieValues = new ContentValues();

                // Then add the data, along with the corresponding name of the data type,
                // so the content provider knows what kind of value is being inserted.
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID, mMovie.getId());
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, mMovie.favorite);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.originalTitle);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, mMovie.backdrop);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, mMovie.thumbnailPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, Float.valueOf(mMovie.rating));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.releaseDate);
                // Finally, insert data into the database.
                Uri insertedUri = getActivity().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues
                );

                // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
                long newMovieId = ContentUris.parseId(insertedUri);
            }

            movieCursor.close();
            toggleAsFavorite();
        }
    }

    private void updateTrailerView(LayoutInflater inflater) {

    }

    private void updateTrailers() {
        FetchTrailerData fetchMovieData = new FetchTrailerData();
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sort_by = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.most_popular_value));*/


        fetchMovieData.execute(mMovie.getId());
    }

    private void updateReviews() {
        FetchReviewData fetchReviewsData = new FetchReviewData(getActivity(), mReviewAdapter);
        fetchReviewsData.execute(mMovie.getId());
    }

    @Override
    public void onResume(){
        Log.i(LOG_TAG," onResume");
        super.onResume();
    }
    public class FetchTrailerData extends AsyncTask<String,Void,ArrayList<Trailer>> {

        private final String LOG_TAG = FetchTrailerData.class.getSimpleName();

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            if (params == null) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieDataJsonStr = null;

            try {

                final String MOVIEDATA_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPKEY = "api_key";
                final String moviePath = "videos";

                Uri builtURI = Uri.parse(MOVIEDATA_BASE_URL).buildUpon().appendPath(params[0]).appendPath(moviePath)
                        .appendQueryParameter(APPKEY, getString(R.string.API_THEMOVIEDB_ORG_KEY))
                        .build();

                URL url = new URL(builtURI.toString());

                Log.v(LOG_TAG, "Built Trailer URI " + builtURI.toString());

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
                Log.v(LOG_TAG, "Trailer Data Json Str: " + movieDataJsonStr);

                return getTrailerDataFromJson(movieDataJsonStr);
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
        private ArrayList<Trailer> getTrailerDataFromJson(String forecastJsonStr)
                throws JSONException {

            JSONObject trailersJson = new JSONObject(forecastJsonStr);
            JSONArray resultsArray = trailersJson.getJSONArray("results");
            ArrayList<Trailer> videos = Trailer.fromJson(resultsArray);
            mMovie.trailers = videos;
            for (Trailer s : videos) {

                Log.v(LOG_TAG, "Trailer entry: " + s.name + s.type);
            }
            return videos;

        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> result){

            if(result != null){
                layoutAdapter.trailers = result;
                layoutAdapter.notifyDataSetChanged();
            }
        }
    }
}
