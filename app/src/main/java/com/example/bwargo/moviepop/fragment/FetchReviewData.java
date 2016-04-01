package com.example.bwargo.moviepop.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.adapters.CustomReviewAdapter;
import com.example.bwargo.moviepop.model.Review;

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


public class FetchReviewData extends AsyncTask<String,Void,ArrayList<Review>> {

    Context mContext;
    CustomReviewAdapter mReviewsAdapter;

    public FetchReviewData(Context context, CustomReviewAdapter mReviewAdapter) {
        this.mReviewsAdapter = mReviewAdapter;
        this.mContext = context;
    }

    private final String LOG_TAG = FetchReviewData.class.getSimpleName();

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        if (params == null) {
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewDataJsonStr;

        try {

            final String MOVIEDATA_BASE_URL = BuildConfig.BASE_URL_THEMOVIEDB;
            final String APPKEY = "api_key";
            final String moviePath = "reviews";

            Uri builtURI = Uri.parse(MOVIEDATA_BASE_URL).buildUpon().appendPath(params[0]).appendPath(moviePath)
                    .appendQueryParameter(APPKEY, mContext.getString(R.string.API_THEMOVIEDB_ORG_KEY))
                    .build();

            URL url = new URL(builtURI.toString());

            Log.v(LOG_TAG, "Built Review URI " + builtURI.toString());

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
            reviewDataJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Review Data Json Str: " + reviewDataJsonStr);

            return getReviewDataFromJson(reviewDataJsonStr);
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

    private ArrayList<Review> getReviewDataFromJson(String reviewData)
            throws JSONException {

        JSONObject reviewsJson = new JSONObject(reviewData);
        JSONArray resultsArray = reviewsJson.getJSONArray("results");
        ArrayList<Review> reviews = Review.fromJson(resultsArray);
        for (Review s : reviews) {
            Log.v(LOG_TAG, "Trailer entry: " + s.author + s.content);
        }
        return reviews;

    }

    @Override
    protected void onPostExecute(ArrayList<Review> result) {

        Log.v(LOG_TAG, "onPOSTEXECUTE moviepop2");

        mReviewsAdapter.reviews = result;
        mReviewsAdapter.notifyDataSetChanged();

    }
}
