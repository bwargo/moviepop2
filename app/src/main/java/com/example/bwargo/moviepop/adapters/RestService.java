package com.example.bwargo.moviepop.adapters;

import com.example.bwargo.moviepop.model.MovieResponseParser;
import com.example.bwargo.moviepop.model.ReviewResponseParser;
import com.example.bwargo.moviepop.model.TrailerResponseParser;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RestService {

    // Example: /discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
    @GET("/discover/movie")
    void getMovieList(@Query("sort_by") String sortBy, Callback<MovieResponseParser> callback);

    @GET("/movie/{id}/videos")
    void getTrailers(@Path("id") long id, Callback<TrailerResponseParser> callback);

    @GET("/movie/{id}/reviews")
    void getReviews(@Path("id") long id, Callback<ReviewResponseParser> callback);
}
