package com.herokuapp.jordan_chau.popularmovies.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.herokuapp.jordan_chau.popularmovies.MovieDetailsActivity;
import com.herokuapp.jordan_chau.popularmovies.R;
import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter{

    private Activity currentActivity;
    private Cursor mCursor;

    public FavoriteAdapter(Activity context, Cursor cursor) {
        currentActivity = context;
        mCursor = cursor;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(currentActivity).inflate(R.layout.movie_item, parent, false);
        }

        if(!mCursor.moveToPosition(position))
            return null;

        String title = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_NAME));
        String image = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_IMAGE));
        String releaseDate = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE));
        Double voteAverage = mCursor.getDouble(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE));
        String plotSynopsis = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS));
        String backdrop = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP));
        int id = mCursor.getInt(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry._ID));

        ImageView movieView = convertView.findViewById(R.id.movie_image);
        Picasso.with(currentActivity).load(Movie.setPicSize(image, "home")).into(movieView);

        setImageOnClickListener(movieView, new Movie(title, image, releaseDate, voteAverage, plotSynopsis,backdrop, id));

        return convertView;
    }

    private void setImageOnClickListener(ImageView iv, final Movie m){
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(currentActivity, MovieDetailsActivity.class);
                i.putExtra("movie", m);
                currentActivity.startActivity(i);
            }
        });
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if(newCursor != null) {
            this.notifyDataSetChanged();
        }
    }
}
