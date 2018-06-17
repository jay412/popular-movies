package com.herokuapp.jordan_chau.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    private String movieName, image, releaseDate, plotSynopsis, backDrop;
    private Double voteAverage;
    private final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private final String DETAIL_URL = "http://image.tmdb.org/t/p/w500/";
    private final String BACKDROP_URL = "http://image.tmdb.org/t/p/w780/";

    public Movie(String mName, String image, String releaseDate, Double voteAverage, String plotSynopsis, String backDrop) {
        this.movieName = mName;
        this.image = image;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.backDrop = backDrop;
    }

    private Movie(Parcel in) {
        movieName = in.readString();
        image = in.readString();
        releaseDate = in.readString();
        plotSynopsis = in.readString();
        backDrop = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getBackDrop() { return backDrop; }

    public String setPicSize(String url, String size) {
        switch (size) {
            case "detail":
                return DETAIL_URL.concat(url);
            case "home":
                return BASE_URL.concat(url);
            case "backdrop":
                return BACKDROP_URL.concat(url);
            default:
                return "error";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieName);
        dest.writeString(image);
        dest.writeString(releaseDate);
        dest.writeString(plotSynopsis);
        dest.writeString(backDrop);
        if (voteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(voteAverage);
        }
    }
}
