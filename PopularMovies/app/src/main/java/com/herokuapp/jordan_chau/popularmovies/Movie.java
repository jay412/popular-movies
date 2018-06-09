package com.herokuapp.jordan_chau.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    String movieName;
    String image;
    final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public Movie(String mName, String image) {
        this.movieName = mName;
        this.image = BASE_URL.concat(image);
    }

    protected Movie(Parcel in) {
        movieName = in.readString();
        image = in.readString();
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

    public String getTitle(){
        return movieName;
    }

    public String getImage(){
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieName);
        dest.writeString(image);
    }
}
