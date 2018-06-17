package com.herokuapp.jordan_chau.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.jordan_chau.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

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
        ImageView backDrop = findViewById(R.id.iv_backdrop);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie m = intent.getParcelableExtra("movie");
        setTitle(m.getTitle());

        Picasso.with(this).load(m.setPicSize(m.getImage(), "detail")).into(image);
        Picasso.with(this).load(m.setPicSize(m.getBackDrop(), "backdrop")).into(backDrop);
        voteAverage.setText(m.getVoteAverage().toString());
        plotSynopsis.setText(m.getPlotSynopsis());
        releaseDate.setText(m.getReleaseDate());
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
}
