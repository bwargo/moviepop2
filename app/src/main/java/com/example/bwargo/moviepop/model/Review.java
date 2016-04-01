package com.example.bwargo.moviepop.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Review {

    public String author;
    public String content;

    public Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public Review(JSONObject object){
        try {
            this.author = object.getString("author");
            this.content = object.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Review> fromJson(JSONArray jsonObjects) {
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                reviews.add(new Review(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }
}
