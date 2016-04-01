package com.example.bwargo.moviepop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bwargo.moviepop.fragment.FetchMovieData;

import java.util.Arrays;

public class MovieResponseParser implements Parcelable{
    public String dataString;
    public String[] movieInfoArray;

    public static final String MOVIE_ID_FIELD = "id";
    public static final String MOVIE_POSTER_FIELD = "poster";
    public static final String MOVIE_TITLE_FIELD = "title";
    public static final String MOVIE_YEAR_FIELD = "year";
    public static final String MOVIE_RATING_FIELD = "rating";
    public static final String MOVIE_DESCRIPTTION_FIELD = "description";

    private static final String[] MOVIE_INFO_FIELDS = new String[]{
            MOVIE_ID_FIELD,
            MOVIE_POSTER_FIELD,
            MOVIE_TITLE_FIELD,
            MOVIE_YEAR_FIELD,
            MOVIE_RATING_FIELD,
            MOVIE_DESCRIPTTION_FIELD
    };

    public MovieResponseParser(String data) {
        this.dataString = data;
        this.movieInfoArray = dataString.split(FetchMovieData.SEPERATOR);
    }

    private MovieResponseParser(Parcel in) {
        dataString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dataString);
    }

    public String extractValueFromMovieInfo(final String infoToExtract, final MovieResponseParser movieInfo){

        int position = Arrays.asList(MOVIE_INFO_FIELDS).indexOf(infoToExtract);
        return (movieInfoArray.length > position && !movieInfoArray[position].equals("null"))?movieInfoArray[position]:"";
    }

    public static final Parcelable.Creator<MovieResponseParser> CREATOR = new Parcelable.Creator<MovieResponseParser>() {

        @Override
        public MovieResponseParser createFromParcel(Parcel parcel) {
            return new MovieResponseParser(parcel);
        }

        @Override
        public MovieResponseParser[] newArray(int i) {
            return new MovieResponseParser[i];
        }
    };
}
