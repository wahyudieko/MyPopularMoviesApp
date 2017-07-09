package com.wahyudieko.popularmoviesapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wahyudieko.popularmoviesapp.utilities.MovieDateUtils;
import com.wahyudieko.popularmoviesapp.utilities.TheMovieDbUtils;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitleTextView, mReleaseDateTextView, mRatingTextView, mOverviewTextView;
    private ImageView mMoviePosterImageView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_detail);
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_detail_poster_loading);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                Movie movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                String title = movie.title;
                String releaseDate = movie.releaseDate;
                Double rating = movie.voteAverage;
                String overview = movie.overview;
                String imagePath = movie.posterPath;

                mMovieTitleTextView.setText(title);
                mReleaseDateTextView.setText(MovieDateUtils.simpleDateFormat(releaseDate));
                mRatingTextView.setText(String.valueOf(rating));
                mOverviewTextView.setText(overview);
                Glide.with(this).load(TheMovieDbUtils.buildImageUrl(imagePath))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .into(mMoviePosterImageView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
