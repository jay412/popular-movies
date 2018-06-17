package com.herokuapp.jordan_chau.popularmovies;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.herokuapp.jordan_chau.popularmovies.utils.MovieAdapter;
import com.herokuapp.jordan_chau.popularmovies.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private GridView gridView;
    //TODO: Place API KEY below
    private static final String API_KEY = "";
    private Boolean internet_error = false;

    public MainActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //allows fragment to handle options menu
        setHasOptionsMenu(true);

        // Get a reference to the ListView, and attach this adapter to it.
        gridView = rootView.findViewById(R.id.grid);

        //places api key in the execute parameter, popular is default sort order
        new GetOperation().execute(API_KEY, "popular");

        return rootView;
    }

    /**
     * <h1>Get Operation</h1>
     * <p> This is an AsyncTask class to perform a GET operation in the background
     * Takes in a String parameter to display all movies with the specified api and sort parameter
     * Returns an array of movie data to be formatted
     */
    private class GetOperation extends AsyncTask<String, Void, ArrayList<Movie>> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Please wait ...");
            progressDialog.show();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String api_key = params[0];
            URL movieRequestUrl = NetworkUtility.buildURL(api_key, params[1]);

            try {
                String jsonUserResponse = NetworkUtility.getHttpUrlResponse(movieRequestUrl);
                ArrayList<Movie> movieData = getMovieStringsFromJson(jsonUserResponse);

                return movieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> mData) {
            progressDialog.dismiss();

            if(mData != null) {
                MovieAdapter movieAdapter = new MovieAdapter(getActivity(), mData);
                gridView.setAdapter(movieAdapter);
                internet_error = false;
            } else {
                showErrorMessage();
                internet_error = true;
            }
        }
    }

    private void showErrorMessage() {
        Toast.makeText(getActivity(),"Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
    }

    private ArrayList<Movie> getMovieStringsFromJson(String json) throws JSONException {

        JSONObject movies = new JSONObject(json);
        JSONArray results = movies.getJSONArray("results");

        ArrayList<Movie> parsedMovieData = new ArrayList<>();

        for(int i = 0; i < results.length(); ++i) {
            String title, posterPath, releaseDate, plotSynopsis, backDrop;
            Double voteAverage;

            JSONObject currentMovie = results.getJSONObject(i);

            title = currentMovie.getString("title");
            posterPath = currentMovie.getString("poster_path");
            releaseDate = currentMovie.getString("release_date");
            voteAverage = currentMovie.getDouble("vote_average");
            plotSynopsis = currentMovie.getString("overview");
            backDrop = currentMovie.getString("backdrop_path");

            parsedMovieData.add(new Movie(title, posterPath, releaseDate, voteAverage, plotSynopsis, backDrop));
        }

        return parsedMovieData;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.most_popular:
                new GetOperation().execute(API_KEY, "popular");
                if (!internet_error)
                    getActivity().setTitle("Most Popular");
                return true;
            case R.id.top_rated:
                new GetOperation().execute(API_KEY, "top_rated");
                if (!internet_error)
                    getActivity().setTitle("Top Rated");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
