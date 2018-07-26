package com.herokuapp.jordan_chau.popularmovies.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.utils.FavoriteAdapter;

public class FavoriteLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavoriteAdapter mAdapter;
    private final Context context;

    public FavoriteLoaderManager(Context c, FavoriteAdapter fa) {
        context = c;
        mAdapter = fa;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(context) {

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

    private Cursor getAllFavoriteMovie() {
        try {
            return context.getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP);
        } catch (Exception e) {
            //Log.e("FMA: ", "Failed to asynchronously load data.");
            //e.printStackTrace();
            return null;
        }
    }
}
