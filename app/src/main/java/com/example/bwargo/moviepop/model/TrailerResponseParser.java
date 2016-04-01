package com.example.bwargo.moviepop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bwargo.moviepop.BuildConfig;

import java.util.ArrayList;

public class TrailerResponseParser {
    public ArrayList<Trailer> results;

    public static class Trailer implements Parcelable {

        public static final Parcelable.Creator<Trailer> CREATOR
                = new Parcelable.Creator<Trailer>() {
            public Trailer createFromParcel(Parcel in) {
                return new Trailer(in);
            }

            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };

        public String key;

        public String name;

        public Trailer() {
        }

        private Trailer(Parcel in) {
            key = in.readString();
            name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(key);
            out.writeString(name);
        }

        /**
         * Helper method to build youtube link.
         */
        public String getYoutubeLink() {
            return BuildConfig.YOUTUBE_BASE_URL + this.key;
        }
    }
}
