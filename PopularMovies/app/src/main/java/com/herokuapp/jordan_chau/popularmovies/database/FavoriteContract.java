package com.herokuapp.jordan_chau.popularmovies.database;

import android.provider.BaseColumns;

public class FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_NAME = "movieName";

        public static final String COLUMN_IMAGE = "image";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

        public static final String COLUMN_PLOT_SYNOPSIS = "plotSynopsis";

        public static final String COLUMN_BACKDROP = "backdrop";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
