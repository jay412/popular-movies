package com.herokuapp.jordan_chau.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    //Content Provider strings
    public static final String AUTHORITY = "com.herokuapp.jordan_chau.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE = "favorite";

    //database table
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_NAME = "movieName";

        public static final String COLUMN_IMAGE = "image";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

        public static final String COLUMN_PLOT_SYNOPSIS = "plotSynopsis";

        public static final String COLUMN_BACKDROP = "backdrop";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
