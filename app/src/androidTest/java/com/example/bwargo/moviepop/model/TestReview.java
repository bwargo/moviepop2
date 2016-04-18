package com.example.bwargo.moviepop.model;

import android.test.AndroidTestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bwargo on 4/17/16.
 */
public class TestReview extends AndroidTestCase {

    //{"id":293660,"page":1,"results":[{"id":"56c146cac3a36817f900d5f0","author":"huy.duc.eastagile","content":"A funny movie with a romantic love story.\r\n\r\nWade Wilson (Ryan Reynolds) is a former Special Forces operative who now works as a mercenary. His world comes crashing down when evil scientist Ajax (Ed Skrein) tortures, disfigures and transforms him into Deadpool. \r\n\r\nThe rogue experiment leaves Deadpool with accelerated healing powers and a twisted sense of humor. With help from mutant allies Colossus and Negasonic Teenage Warhead (Brianna Hildebrand), Deadpool uses his new skills to hunt down the man who nearly destroyed his life.","url":"https://www.themoviedb.org/review/56c146cac3a36817f900d5f0"},{"id":"56ca035a9251414a7a0062f0","author":"Wong","content":"I actually enjoyed the movie so much that i'll recommend it to all my friends, at first i didn't really want to watch it because i'm not into super hero movies at all, but i did anyway, i mean people were talking so much about it i had to see it myself and what an awesome choice i made. The good thing about this movie is that Deadpool is a hero but in a very comedic way, you don't usually expect comedy from a superhero film but this one was full of comedy and the way they treated the plot was amazing, it was there, humor was there in every scene, even when there was fighting or romance or any other scene, the writers managed to add comedy everywhere in a very good way that'll surprisingly make you want to watch it again, and again. Thank you for taking the time read my review and if you're asking yourself if you should watch this movie, it's a definite Yes.","url":"https://www.themoviedb.org/review/56ca035a9251414a7a0062f0"}],"total_pages":1,"total_results":2}
    private static final String reviewStr = "{'author':'huy.duc.eastagile',"
                                            +"'content':'A funny movie with a romantic love story." +
                                            "\r\n\r\nWade Wilson (Ryan Reynolds) is a former Special" +
                                            " Forces operative who now works...'";

    public void testReviewIntoJSONObject(){
        try{
        JSONObject jsonObject = new JSONObject(reviewStr);
            assertNotNull(jsonObject);
    }catch(JSONException je){
        je.printStackTrace();
        fail("JSon exception, failed to create Json object from Json string");
    }
    }
    public void testReviewFromJsonStr(){
        try {
            Review review = new Review( new JSONObject(reviewStr));
            assertNotNull("Review not created from Json String", review);
            assertNotNull("Author is null", review.author);
            assertNotNull("Content is null", review.content);
            assertEquals("huy.duc.eastagile",review.author);
        }catch(JSONException je){
            je.printStackTrace();
            fail("JSon exception, failed to create Review object from Json string");
        }
    }

    public void testArrayReviews(){
        String jsonArrayStr = "["+ reviewStr +"," + reviewStr + "]";
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayStr);
            ArrayList<Review> reviews = Review.fromJson(jsonArray);
            assertNotNull(reviews);
            assertEquals(2, reviews.size());
        }catch (JSONException js){
            js.fillInStackTrace();
            fail("Failed to create JsonArray");
        }
    }
}
