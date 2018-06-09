package com.herokuapp.jordan_chau.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie>{

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView movieView = convertView.findViewById(R.id.movie_image);
        //movieView.setImageResource(movie.image);
        Picasso.with(this.getContext()).load(movie.image).into(movieView);

        TextView versionNameView = convertView.findViewById(R.id.movie_text);
        versionNameView.setText(movie.movieName);

        return convertView;
    }
}
