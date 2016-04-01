package com.example.bwargo.moviepop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Movie> {
    private final String LOG_TAG = CustomListAdapter.class.getSimpleName();
    public static final String baseUrl = BuildConfig.IMAGE_BASE_URL;
    private final Context context;
    private final ArrayList<Movie> movies;

    public CustomListAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.grid_item_layout, movies);
        this.context = context;
        this.movies = movies;
    }

    static class ViewHolder {
        public ImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "getView MoviePop2");
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.posterThumbnail);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Movie movie = getItem(position);
        Picasso.with(context).load(baseUrl+movie.thumbnailPath).into(holder.image);
        return convertView;
    }
}