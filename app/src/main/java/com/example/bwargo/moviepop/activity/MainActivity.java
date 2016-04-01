package com.example.bwargo.moviepop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.data.MovieLoader;
import com.example.bwargo.moviepop.fragment.DetailActivityFragment;
import com.example.bwargo.moviepop.fragment.MainActivityFragment;
import com.example.bwargo.moviepop.model.Movie;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean tabletLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate Moviepop2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        tabletLayout = findViewById(R.id.full_movie_details) != null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "onCreateOptionsMenu Moviepop2");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onOptionsItemSelected MoviePop2");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        Log.v(LOG_TAG, "onResume Moviepop2");
        super.onResume();
        loadFragment();
    }
    private void loadFragment() {
        Log.v(LOG_TAG, "loadFragment Moviepop2");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sort_by = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_most_popular));
        if(sort_by.equals(getString(R.string.favorite_value))) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_main, new MovieLoader())
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_main, new MainActivityFragment())
                    .commit();
        }
    }
    @Override
    public void onItemSelected(String movieData) {
        if (tabletLayout) {
            Log.v(LOG_TAG, "onItemSelected tablet-size MoviePop2");
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putSerializable("movie",Movie.fromJsonStr(movieData));

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_detail, fragment)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onItemSelected phone-size MoviePop2");
            Intent intent = new Intent(this, DetailActivity.class).putExtra("movie", Movie.fromJsonStr(movieData));
            startActivity(intent);
        }
    }
}