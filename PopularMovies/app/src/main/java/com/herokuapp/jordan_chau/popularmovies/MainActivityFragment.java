package com.herokuapp.jordan_chau.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Intent.getIntent;

public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;

    ArrayList<Movie> movies;

    public MainActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle b = getArguments();
        if (b != null) {
            movies = b.getParcelableArrayList("moData");
            Log.v("MAF.java", "MOVIESS SIZE: " + movies.size());
        }

        //movieAdapter = new MovieAdapter(getActivity(), movies);

        // Get a reference to the ListView, and attach this adapter to it.
        //GridView gridView = rootView.findViewById(R.id.grid);
        //gridView.setAdapter(movieAdapter);

        return rootView;
    }
}
