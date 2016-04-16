package com.example.bwargo.moviepop.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Movie implements Serializable{
    private int id;
    public String originalTitle;
    public String thumbnailPath;
    public String overview;
    public String rating;
    public String releaseDate;
    public String backdrop;
    public int favorite;
    public List<Trailer> trailers;
    public List<Review> reviews;

    public Movie(int id, String movieInfo, String thumbnailPath){
        this.id = id;
        this.originalTitle = movieInfo;
        this.thumbnailPath = thumbnailPath;

    }

    public String getId(){
        return Integer.toString(id);
    }
    public int getIdAsInt(){
        return id;
    }
    public boolean getFavoriteAsBoolean(){return this.favorite == 1;}

    public Movie(JSONObject object){
        try {
            this.id = object.getInt("id");
            this.originalTitle = object.getString("original_title");
            this.overview = object.getString("overview");
            this.rating = Double.toString(object.getDouble("vote_average"));
            this.releaseDate = object.getString("release_date");
            this.thumbnailPath = object.getString("poster_path");
            this.backdrop = object.getString("backdrop_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Movie fromJsonStr(String jsonStr){
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return new Movie(jsonObject);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<Movie> fromJson(JSONArray jsonObjects) {
        ArrayList<Movie> users = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                users.add(new Movie(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }
}
