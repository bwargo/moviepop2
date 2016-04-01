package com.example.bwargo.moviepop.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Trailer implements Serializable {
    public String key;
    public String name;
    public String site;
    public String type;

    public Trailer(JSONObject object){
        try {
            this.key = object.getString("key");
            this.name = object.getString("name");
            this.site = object.getString("site");
            this.type = object.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Trailer> fromJson(JSONArray jsonObjects) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                trailers.add(new Trailer(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trailers;
    }

}