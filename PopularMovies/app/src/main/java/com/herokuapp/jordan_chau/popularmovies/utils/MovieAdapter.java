package com.herokuapp.jordan_chau.popularmovies.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.herokuapp.jordan_chau.popularmovies.MovieDetailsActivity;
import com.herokuapp.jordan_chau.popularmovies.R;
import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie>{

    private Activity currentActivity;
    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        currentActivity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView movieView = convertView.findViewById(R.id.movie_image);
        Picasso.with(this.getContext()).load(movie.setPicSize(movie.getImage(), "home")).into(movieView);

        setImageOnClickListener(movieView, movie);

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
}
