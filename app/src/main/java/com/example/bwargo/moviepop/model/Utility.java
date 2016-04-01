package com.example.bwargo.moviepop.model;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.bwargo.moviepop.BuildConfig;

public class Utility {

    private static final String VND_YOUTUBE = BuildConfig.VND_YOUTUBE;
    private static final String YOUTUBE_BASE_URL = BuildConfig.YOUTUBE_BASE_URL;

    public static void playYoutube(String id, Context context){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(VND_YOUTUBE + id));
            context.startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse(YOUTUBE_BASE_URL +id));
            context.startActivity(intent);
        }
    }
}
