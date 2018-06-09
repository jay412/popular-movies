package com.herokuapp.jordan_chau.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.herokuapp.jordan_chau.popularmovies.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //place api key in the execute parameter
        new GetOperation().execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * <h1>Get Operation</h1>
     * <p> This is an AsyncTask class to perform a GET operation in the background
     * Takes in a String parameter to display all users with the specified candidate parameter
     * @return String[] - Returns an array of user data to be formatted
     */
    private class GetOperation extends AsyncTask<String, Void, ArrayList<Movie>> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

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
            URL movieRequestUrl = NetworkUtility.buildURL(api_key);

            try {
                String jsonUserResponse = NetworkUtility.getHttpUrlResponse(movieRequestUrl);
                Log.v("MainActivity.java", "HTTP URL RESPONSE: " + jsonUserResponse);

                ArrayList<Movie> movieData = getMovieStringsFromJson(jsonUserResponse);
                Log.v("MainActivity.java", "FIRST MOVIE TITLE: " + movieData.get(0).getTitle());
                Log.v("MainActivity.java", "FIRST MOVIE IMAGE: " + movieData.get(0).getImage());

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
                /*

                MainActivityFragment maf = new MainActivityFragment();
                maf.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, maf).commit();

                //setMovies(mData);
                */
                //Intent i = new Intent(MainActivity.this, MainActivityFragment.class);
                //i.putParcelableArrayListExtra("mData", mData);
                //startActivity(i);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("moData", mData);
                MainActivityFragment frag = new MainActivityFragment();
                frag.setArguments(bundle);
            } else {
                Log.v("MainActivity.java", "POST EXECUTE: " + "MDATA IS NULL");
                //show error msg
                //showErrorMessage();
            }
        }
    }

    /**
     * This method creates a String array that is formatted to be displayed in the user data text view
     * By using the user JSON String that is retrieved from a GET request
     * @param json - String parameter to create the String array with
     * @return String[] - Returns a String array that is formatted and represents all user info
     */
    private ArrayList<Movie> getMovieStringsFromJson(String json) throws JSONException {

        JSONObject movies = new JSONObject(json);
        JSONArray results = movies.getJSONArray("results");

        ArrayList<Movie> parsedMovieData = new ArrayList<Movie>();

        for(int i = 0; i < results.length(); ++i) {
            String title, posterPath;

            JSONObject currentMovie = results.getJSONObject(i);

            title = currentMovie.getString("title");
            posterPath = currentMovie.getString("poster_path");

            parsedMovieData.add(new Movie(title, posterPath));
        }

        return parsedMovieData;
    }
}
