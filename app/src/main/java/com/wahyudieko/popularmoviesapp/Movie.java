package com.wahyudieko.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EKO on 08/07/2017.
 */

public class Movie implements Parcelable {

    final String title;
    final String releaseDate;
    final String posterPath;
    final Double voteAverage;
    final String overview;

    public Movie (String title, String releaseDate, String posterPath, Double voteAverage, String overview){
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    private Movie(Parcel in){
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
