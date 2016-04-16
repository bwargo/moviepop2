package com.example.bwargo.moviepop.model;

import android.test.AndroidTestCase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by bwargo on 4/15/16.
 */
public class TestMovie extends AndroidTestCase{
   // {"adult":false,"backdrop_path":"/n1y094tVDFATSzkTnFxoGZ1qNsG.jpg","belongs_to_collection":null,"budget":58000000,"genres":[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"},{"id":35,"name":"Comedy"}],"homepage":"http://www.foxmovies.com/movies/deadpool","id":293660,"imdb_id":"tt1431045","original_language":"en","original_title":"Deadpool","overview":"Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool. Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.","popularity":15.463728,"poster_path":"/inVq3FRqcYIRl2la8iZikYYxFNR.jpg","production_companies":[{"name":"Twentieth Century Fox Film Corporation","id":306},{"name":"Marvel Entertainment, LLC","id":325},{"name":"Marvel Enterprises","id":19551}],"production_countries":[{"iso_3166_1":"US","name":"United States of America"}],"release_date":"2016-02-09","revenue":0,"runtime":108,"spoken_languages":[{"iso_639_1":"en","name":"English"}],"status":"Released","tagline":"Witness the beginning of a happy ending","title":"Deadpool","video":false,"vote_average":7.2,"vote_count":2714}
    private static final String movieJson = "{" + "'adult':'false',"
                                            + "'backdrop_path':'/n1y094tVDFATSzkTnFxoGZ1qNsG.jpg',"
                                            + "'id' :293660,"
                                            + "'original_title':'Deadpool',"
                                            + "'overview':'Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool. Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.',"
                                            + "'popularity':15.463728,"
                                            + "'poster_path':'/inVq3FRqcYIRl2la8iZikYYxFNR.jpg',"
                                            + "'release_date':'2016-02-09',"
                                            + "'title':'Deadpool',"
                                            + "'video':'false',"
                                            + "'vote_average':7.2,"
                                            + "'vote_count':2714" + "}";

    public void testMovieFromJsonStr(){
        Movie movie = Movie.fromJsonStr(movieJson);
        assertNotNull("Movie not created from Json String",movie);
        assertNotNull("Id as String is null", movie.getId());
        assertNotNull("Id as int is null", movie.getIdAsInt());
        assertNotNull("originalTitle is null", movie.originalTitle);
        assertNotNull("thumbnail is null", movie.thumbnailPath);
        assertNotNull("overview is null", movie.overview);
        assertNotNull("rating is null", movie.rating);
        assertNotNull("releaseDate is null", movie.releaseDate);
        assertNotNull("backdrop is null", movie.backdrop);
        assertNotNull("favorite is null", movie.favorite);
        assertNotNull("favorite as boolean is null", movie.getFavoriteAsBoolean());
    }

    public void testArrayMovies(){
        String jsonArrayStr = "["+ movieJson +"," + movieJson + "]";
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayStr);
            ArrayList<Movie> movies = Movie.fromJson(jsonArray);
            assertNotNull(movies);
            assertEquals(2, movies.size());
        }catch (JSONException js){
            js.fillInStackTrace();
            fail("Failed to create JsonArray");
        }
    }

}
