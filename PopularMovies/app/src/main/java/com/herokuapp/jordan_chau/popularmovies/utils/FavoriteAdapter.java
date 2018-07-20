package com.herokuapp.jordan_chau.popularmovies.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.herokuapp.jordan_chau.popularmovies.MovieDetailsActivity;
import com.herokuapp.jordan_chau.popularmovies.R;
import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteAdapter extends ArrayAdapter<Movie>{

    private Activity currentActivity;
    private Cursor mCursor;

    public FavoriteAdapter(Activity context, ArrayList<Movie> movies, Cursor cursor) {
        super(context, 0, movies);
        currentActivity = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, parent, false);
        }

        //ImageView movieView = convertView.findViewById(R.id.movie_image);
        //Picasso.with(this.getContext()).load(movie.setPicSize(movie.getImage(), "home")).into(movieView);

        //setImageOnClickListener(movieView, movie);

        if(!mCursor.moveToPosition(position))
            return null;

        String name = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_NAME));
        TextView favoriteName = convertView.findViewById(R.id.favorite_name);
        favoriteName.setText(name);

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

    public int getItemCount() {
        return mCursor.getCount();
    }
}
