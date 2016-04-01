package com.example.bwargo.moviepop.adapters;

import android.app.Application;
import android.content.Context;

import com.example.bwargo.moviepop.BuildConfig;
import com.example.bwargo.moviepop.R;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RetroFitAdapterService extends Application{
    private final static String BASE_URL = BuildConfig.BASE_URL_THEMOVIEDB;
    private final static String API_KEY = getContext().getString(R.string.API_THEMOVIEDB_ORG_KEY);
    private static RestAdapter mRetroFit;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public static RestAdapter getRestAdapter() {

        if (mRetroFit == null) {
            mRetroFit = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setRequestInterceptor(mRequestInterceptor)
                    .build();
            if (BuildConfig.DEBUG) {
                mRetroFit.setLogLevel(RestAdapter.LogLevel.FULL);
            }

        }
        return mRetroFit;
    }

    private static RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", API_KEY);
        }
    };
}
