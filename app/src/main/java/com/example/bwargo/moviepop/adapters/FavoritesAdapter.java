package com.example.bwargo.moviepop.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.data.MovieLoader;
import com.squareup.picasso.Picasso;

public class FavoritesAdapter extends CursorAdapter{

    public FavoritesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private final String LOG_TAG = FavoritesAdapter.class.getSimpleName();
    public static final String baseUrl = BuildConfig.IMAGE_BASE_URL;
    private static final int VIEW_TYPE = 0;
    private static final int VIEW_TYPE_COUNT = 1;
    private LayoutInflater mInflater;

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "newView Moviepop2");
        // if it's not recycled, create and set the attributes
        return mInflater.inflate(R.layout.grid_item_layout, parent, false);
    }
    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "bindView Moviepop2");
        ImageView imageView = (ImageView) view.findViewById(R.id.posterThumbnail);
        String imageName = cursor.getString(MovieLoader.COL_MOVIE_POSTER);
        Picasso.with(context).load(baseUrl+imageName).into(imageView);
        //Glide.with(context).load(Utils.buildPosterImageUrl(imageName)).into((ImageView)view);
    }
}
