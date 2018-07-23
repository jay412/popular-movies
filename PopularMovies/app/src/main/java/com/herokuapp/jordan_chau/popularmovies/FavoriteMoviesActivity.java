package com.herokuapp.jordan_chau.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.utils.FavoriteAdapter;

public class FavoriteMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavoriteAdapter mAdapter;

    private static final int FAVORITE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        setTitle("My Favorites");

        //Creates the back arrow on the top left corner to return to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        GridView mGrid = findViewById(R.id.fgrid);
        //initialize and get data
        mAdapter = new FavoriteAdapter(this, null);

        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
        mGrid.setAdapter(mAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            //when loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    //deliver previous loaded data
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            //performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                return getAllFavoriteMovie();
            }

            //send result of the load to registered listener
            @Override
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        //refresh favorite movies when activity is resumed
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        super.onResume();
    }

    /*@Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    } */


    private Cursor getAllFavoriteMovie() {
        try {
            return getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP);
        } catch (Exception e) {
            Log.e("FMA: ", "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }
}
