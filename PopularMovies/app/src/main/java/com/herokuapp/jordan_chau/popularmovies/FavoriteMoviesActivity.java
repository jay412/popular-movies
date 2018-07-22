package com.herokuapp.jordan_chau.popularmovies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.database.FavoriteDbHelper;
import com.herokuapp.jordan_chau.popularmovies.utils.FavoriteAdapter;

public class FavoriteMoviesActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private FavoriteAdapter mAdapter;
    private GridView mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        setTitle("My Favorites");

        //Creates the back arrow on the top left corner to return to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGrid = findViewById(R.id.fgrid);

        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
        mDb = dbHelper.getReadableDatabase();
        //get data
        Cursor cursor = getAllFavoriteMovie();
        mAdapter = new FavoriteAdapter(this, cursor);
        mGrid.setAdapter(mAdapter);
    }

    private Cursor getAllFavoriteMovie() {
        return mDb.query(
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP
        );
    }
}
