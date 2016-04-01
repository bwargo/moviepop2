package com.example.bwargo.moviepop.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.bwargo.moviepop.R;

public class CustomViewHolder extends RecyclerView.ViewHolder{

    public ImageView thumbnail;

    public CustomViewHolder(View itemView) {
        super(itemView);
        this.thumbnail = (ImageView) itemView.findViewById(R.id.posterThumbnail);
        itemView.setClickable(true);
    }
}
