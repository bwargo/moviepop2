package com.example.bwargo.moviepop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bwargo.moviepop.R;
import com.example.bwargo.moviepop.model.Review;

import java.util.ArrayList;

public class CustomReviewAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Review> reviews;

    public CustomReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        if(reviews == null) return 0;
        return reviews.size();
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
        Review review = reviews.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.review_row, parent, false);
        }
        // Lookup view
        TextView tvName = (TextView) convertView.findViewById(R.id.review_author);

        // Populate the data into the template view using the data object
        tvName.setText(review.author);

        TextView tvContent = (TextView) convertView.findViewById(R.id.review_content);

        tvContent.setText(review.content);
        // Return the completed view to render on screen
        return convertView;

    }
}
