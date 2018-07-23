package com.herokuapp.jordan_chau.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.jordan_chau.popularmovies.database.FavoriteContract;
import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.herokuapp.jordan_chau.popularmovies.utils.NetworkUtility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView reviews;
    private HashMap<String, String> trailers;
    private LinearLayout trailerLayout;

    private Movie m;

    private final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Creates the back arrow on the top left corner to return to MainActivity, DELETE PARENT?
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView image = findViewById(R.id.iv_image);
        TextView voteAverage = findViewById(R.id.tv_vote_average);
        TextView plotSynopsis = findViewById(R.id.tv_plot_synopsis);
        TextView releaseDate = findViewById(R.id.tv_release_date);
        reviews = findViewById(R.id.tv_reviews);
        ImageView backDrop = findViewById(R.id.iv_backdrop);
        trailerLayout = findViewById(R.id.ll_trailers);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        m = intent.getParcelableExtra("movie");
        setTitle(m.getTitle());

        Picasso.with(this).load(Movie.setPicSize(m.getImage(), "detail")).into(image);
        Picasso.with(this).load(Movie.setPicSize(m.getBackDrop(), "backdrop")).into(backDrop);
        voteAverage.setText(m.getVoteAverage().toString().concat("/10"));
        plotSynopsis.setText(m.getPlotSynopsis());
        releaseDate.setText(m.getReleaseDate());

        new GetOperation(this).execute(m.getId());
    }

    //change name in future
    private class GetOperation extends AsyncTask<Integer, Void, ArrayList<String>> {
        final ProgressDialog progressDialog;
        final Context context;

        private GetOperation(Context c){
            context = c;
            progressDialog = new ProgressDialog(c);
        }

        @Override
        protected void onPreExecute() {
            //checks for internet connection before proceeding
            if(!NetworkUtility.checkInternetConnection(context)) {
                this.cancel(true);
                Toast.makeText(context,"Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
            }
            else {
                super.onPreExecute();

                progressDialog.setTitle("Please wait ...");
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            URL reviewUrl = NetworkUtility.buildMovieURL(params[0], BuildConfig.API_KEY, "reviews");
            URL trailerUrl = NetworkUtility.buildMovieURL(params[0], BuildConfig.API_KEY, "trailers");

            try {
                String jsonUserResponse = NetworkUtility.getHttpUrlResponse(reviewUrl);
                String jsonTrailerResponse = NetworkUtility.getHttpUrlResponse(trailerUrl);

                ArrayList<String> reviewData = parseReviewsString(jsonUserResponse);
                trailers = parseTrailersString(jsonTrailerResponse);

                return reviewData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> rData) {
            //format reviews and add to textview --> make into method later
            if (rData != null) {
                StringBuilder reviewText = new StringBuilder();
                for (int x = 0; x < rData.size(); ++x) {
                    reviewText.append(rData.get(x)).append("\n\n");
                }

                reviews.setText(reviewText.toString());
            }

            //add button and trailer name to layout --> make into method later
            if (trailers != null) {
                for (String name : trailers.keySet()) {
                    final String key = trailers.get(name);

                    ImageButton b = new ImageButton(context);
                    b.setBackgroundResource(R.drawable.ic_play);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(key);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });

                    TextView tv = new TextView(context);
                    //doesn't work
                    //Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "font/chela_one_regular.ttf");
                    //tv.setTypeface(custom_font);
                    tv.setText(name);

                    //add button first, then textview
                    trailerLayout.addView(b);
                    trailerLayout.addView(tv);
                }
            }

            progressDialog.dismiss();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handles back arrow presses
        if(id == android.R.id.home) {
            finish();
            //handle favorite star presses
        } else if (id == R.id.action_favorite) {
            //check to see which msg to display in case of duplicate favorite movies
            Boolean showMsg = true;

            try {
                addToFavorites();
            } catch (SQLiteException exception) {
                showMsg = false;
                removeFromFavorites();
                Toast.makeText(this,"Removed from favorites!", Toast.LENGTH_LONG).show();
            }

            if(showMsg)
                Toast.makeText(this,"Added to favorites!", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> parseReviewsString(String json) throws JSONException{

        JSONObject reviews = new JSONObject(json);
        JSONArray results = reviews.getJSONArray("results");

        ArrayList<String> parsedReviewData = new ArrayList<>();

        for(int i = 0; i < results.length(); ++i) {
            String author, content;

            JSONObject currentReview = results.getJSONObject(i);

            author = currentReview.getString("author");
            content = currentReview.getString("content");

            String review = author + ": " + content;
            parsedReviewData.add(review);
        }

        return parsedReviewData;
    }

    private HashMap<String, String> parseTrailersString(String json) throws JSONException{

        JSONObject reviews = new JSONObject(json);
        JSONArray results = reviews.getJSONArray("results");

        HashMap<String, String> parsedTrailerData = new HashMap<>();

        for(int i = 0; i < results.length(); ++i) {
            String name, key;

            JSONObject currentTrailer = results.getJSONObject(i);

            name = currentTrailer.getString("name");
            key = YOUTUBE_URL.concat(currentTrailer.getString("key"));

            parsedTrailerData.put(name, key);
        }

        return parsedTrailerData;
    }

    private void addToFavorites() {
        if (m.getTitle() == null) {
            return;
        }

        ContentValues cv = new ContentValues();

        cv.put(FavoriteContract.FavoriteEntry._ID, m.getId());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_NAME, m.getTitle());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_IMAGE, m.getImage());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, m.getReleaseDate());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, m.getVoteAverage());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, m.getPlotSynopsis());
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP, m.getBackDrop());

        getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, cv);
    }

    private void removeFromFavorites() {
        String stringId = Integer.toString(m.getId());
        Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

    /*@Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    } */
}
