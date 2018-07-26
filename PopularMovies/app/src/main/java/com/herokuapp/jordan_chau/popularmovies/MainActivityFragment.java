package com.herokuapp.jordan_chau.popularmovies;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.herokuapp.jordan_chau.popularmovies.loader.FavoriteLoaderManager;
import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.herokuapp.jordan_chau.popularmovies.utils.FavoriteAdapter;
import com.herokuapp.jordan_chau.popularmovies.utils.MovieAdapter;
import com.herokuapp.jordan_chau.popularmovies.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private GridView gridView;
    private static final String API_KEY = BuildConfig.API_KEY;

    //adapters
    private MovieAdapter movieAdapter;
    private FavoriteAdapter mAdapter;

    //for saved instance
    private String sortOrder;
    private static final String SORT_ORDER = "sort_order";
    private static final String GRID_POSITION = "grid_position";

    //for loader
    private static final int FAVORITE_LOADER_ID = 0;

    public MainActivityFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //allows fragment to handle options menu
        setHasOptionsMenu(true);

        // Get a reference to the ListView, and attach this adapter to it.
        gridView = rootView.findViewById(R.id.grid);

        //if there is a saved instance state
        if(savedInstanceState != null) {
            //save sort order
            sortOrder = savedInstanceState.getString(SORT_ORDER);

            if (sortOrder.equals("popular")) {
                getActivity().setTitle("Most Popular");
                if(movieAdapter != null)
                    gridView.setAdapter(movieAdapter);
                else
                    new GetOperation().execute(API_KEY, sortOrder);

            } else if(sortOrder.equals("top_rated")) {
                getActivity().setTitle("Top Rated");
                if(movieAdapter != null)
                    gridView.setAdapter(movieAdapter);
                else
                    new GetOperation().execute(API_KEY, sortOrder);
            } 

            //move to previous position in gridview
            int mCurrentPosition = savedInstanceState.getInt(GRID_POSITION);
            gridView.setSelection(mCurrentPosition);
        } else {

            //default startup
            getActivity().setTitle("Most Popular");

            //favorites loader
            //mAdapter = new FavoriteAdapter(getActivity(), null);
            //getActivity().getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, new FavoriteLoaderManager(getActivity(), mAdapter));

            //places api key in the execute parameter, popular is default sort order
            new GetOperation().execute(API_KEY, "popular");
        }

        return rootView;
    }

    /**
     * <h1>Get Operation</h1>
     * <p> This is an AsyncTask class to perform a GET operation in the background
     * Takes in a String parameter to display all movies with the specified api and sort parameter
     * Returns an array of movie data to be formatted
     */
    private class GetOperation extends AsyncTask<String, Void, ArrayList<Movie>> {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //checks for internet connection before proceeding
            if(!NetworkUtility.checkInternetConnection(getActivity())) {
                this.cancel(true);
                NetworkUtility.showErrorMessage(gridView);
            }
            else {
                super.onPreExecute();

                progressDialog.setTitle("Please wait ...");
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String api_key = params[0];
            sortOrder = params[1];
            URL movieRequestUrl = NetworkUtility.buildURL(api_key, sortOrder);

            try {
                String jsonUserResponse = NetworkUtility.getHttpUrlResponse(movieRequestUrl);

                return getMovieStringsFromJson(jsonUserResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> mData) {
            progressDialog.dismiss();

            if(mData != null) {
                movieAdapter = new MovieAdapter(getActivity(), mData);
                gridView.setAdapter(movieAdapter);
            } else {
                NetworkUtility.showErrorMessage(gridView);
            }
        }
    }

    private ArrayList<Movie> getMovieStringsFromJson(String json) throws JSONException {

        JSONObject movies = new JSONObject(json);
        JSONArray results = movies.getJSONArray("results");

        ArrayList<Movie> parsedMovieData = new ArrayList<>();

        for(int i = 0; i < results.length(); ++i) {
            String title, posterPath, releaseDate, plotSynopsis, backDrop;
            int id;
            Double voteAverage;

            JSONObject currentMovie = results.getJSONObject(i);

            title = currentMovie.getString("title");
            posterPath = currentMovie.getString("poster_path");
            releaseDate = currentMovie.getString("release_date");
            voteAverage = currentMovie.getDouble("vote_average");
            plotSynopsis = currentMovie.getString("overview");
            backDrop = currentMovie.getString("backdrop_path");
            id = currentMovie.getInt("id");

            parsedMovieData.add(new Movie(title, posterPath, releaseDate, voteAverage, plotSynopsis, backDrop, id));
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
                getActivity().setTitle("Most Popular");
                gridView.setAdapter(movieAdapter);
                new GetOperation().execute(API_KEY, "popular");
                return true;

            case R.id.top_rated:
                getActivity().setTitle("Top Rated");
                gridView.setAdapter(movieAdapter);
                new GetOperation().execute(API_KEY, "top_rated");
                return true;

            case R.id.favorites:
                getActivity().setTitle("My Favorites");
                //sortOrder = "my_favorites";
                mAdapter = new FavoriteAdapter(getActivity(), null);
                getActivity().getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, new FavoriteLoaderManager(getActivity(), mAdapter));
                gridView.setAdapter(mAdapter);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //save grid position and sort order when device is rotated
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        int position = gridView.getFirstVisiblePosition();

        outState.putString(SORT_ORDER, sortOrder);
        outState.putInt(GRID_POSITION, position);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        //refresh favorite movies when activity is resumed if cursor is not null
        if(mAdapter != null && mAdapter.getCursor() != null)
            getActivity().getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, new FavoriteLoaderManager(getActivity(), mAdapter));

        super.onResume();
    }
}
