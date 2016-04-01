package com.example.bwargo.moviepop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.model.Trailer;

import java.util.ArrayList;

public class CustomLinearLayoutAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<Trailer> trailers;

    public CustomLinearLayoutAdapter(Context context, ArrayList<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public int getCount() {
        if(trailers == null) return 0;
        return trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Trailer trailer = trailers.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trailer_row, parent, false);
        }
        // Lookup view
        TextView tvName = (TextView) convertView.findViewById(R.id.trailer);

        // Populate the data into the template view using the data object
        tvName.setText(trailer.site);

        // Return the completed view to render on screen
        return convertView;
    }
}
