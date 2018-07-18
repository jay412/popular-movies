package com.herokuapp.jordan_chau.popularmovies.database;

import android.provider.BaseColumns;

public class FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_NAME = "movieName";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
