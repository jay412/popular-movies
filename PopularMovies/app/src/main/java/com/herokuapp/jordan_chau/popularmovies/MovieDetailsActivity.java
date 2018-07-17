package com.herokuapp.jordan_chau.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Creates the back arrow on the top left corner to return to MainActivity
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

        Movie m = intent.getParcelableExtra("movie");
        setTitle(m.getTitle());

        Picasso.with(this).load(m.setPicSize(m.getImage(), "detail")).into(image);
        Picasso.with(this).load(m.setPicSize(m.getBackDrop(), "backdrop")).into(backDrop);
        voteAverage.setText(m.getVoteAverage().toString().concat("/10"));
        plotSynopsis.setText(m.getPlotSynopsis());
        releaseDate.setText(m.getReleaseDate());

        //Log.v("MDA: ", "id= " + m.getId());

        new GetOperation(this).execute(m.getId());
    }

    private class GetOperation extends AsyncTask<Integer, Void, ArrayList<String>> {
        ProgressDialog progressDialog;
        Context context;

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
                Log.v("MAF.java: ", "NO WIFI");
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

                Log.v("MDA.java: ", "json = " + jsonTrailerResponse);

                return reviewData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> rData) {
            if (rData != null) {
                String reviewText = "";
                for (int x = 0; x < rData.size(); ++x) {
                    reviewText += rData.get(x) + "\n\n";
                }

                reviews.setText(reviewText);
            }

            if(trailers != null) {
                for (String name : trailers.keySet()) {
                    final String key = trailers.get(name);

                    Button b = new Button(context);
                    b.setText(name);
                    //b.setTypeface(getResources().getFont(R.font.chela_one_regular));
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(key);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });

                    trailerLayout.addView(b);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handles back arrow presses
        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> parseReviewsString(String json) throws JSONException{

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

    public HashMap<String, String> parseTrailersString(String json) throws JSONException{

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
}
