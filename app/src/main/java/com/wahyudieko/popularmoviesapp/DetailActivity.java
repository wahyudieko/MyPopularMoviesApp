package com.wahyudieko.popularmoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wahyudieko.popularmoviesapp.adapters.MovieReviewAdapter;
import com.wahyudieko.popularmoviesapp.adapters.MovieTrailerAdapter;
import com.wahyudieko.popularmoviesapp.data.MovieContract;
import com.wahyudieko.popularmoviesapp.entities.Movie;
import com.wahyudieko.popularmoviesapp.entities.MovieReview;
import com.wahyudieko.popularmoviesapp.entities.MovieTrailer;
import com.wahyudieko.popularmoviesapp.utilities.MovieDateUtils;
import com.wahyudieko.popularmoviesapp.utilities.NetworkUtils;
import com.wahyudieko.popularmoviesapp.utilities.TheMovieDbUtils;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import static com.wahyudieko.popularmoviesapp.MainActivity.LOG_TAG;
import static com.wahyudieko.popularmoviesapp.data.MovieContract.MovieEntry;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler, MovieReviewAdapter.MovieReviewAdapterOnClickHandler{

    private TextView mMovieTitleTextView, mReleaseDateTextView, mRatingTextView, mOverviewTextView;
    private ImageView mMoviePosterImageView;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewRecyclerView;

    private MovieTrailerAdapter mMovieTrailerAdapter;
    private MovieReviewAdapter mMovieReviewAdapter;

    private TextView mErrorMessageDisplayTrailer;
    private TextView mErrorMessageDisplayReview;

    private ProgressBar mTrailerLoadingIndicator;
    private ProgressBar mReviewLoadingIndicator;

    private LinearLayoutManager mTrailerLinearLayoutManager;
    private LinearLayoutManager mReviewLinearLayoutManager;

    private static final int TRAILERS_LOADER_ID= 0;
    private static final int REVIEWS_LOADER_ID= 1;

    private int movieId;

    private Movie mMovie = null;
    private MovieTrailer mMovieTrailer = null;

    private Button mMarkAsFavoriteButton;

    private Uri currentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Trailer
        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailer);
        mErrorMessageDisplayTrailer = (TextView) findViewById(R.id.tv_error_message_display_trailer);


        mTrailerLinearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailerRecyclerView.setLayoutManager(mTrailerLinearLayoutManager);

        mTrailerRecyclerView.setHasFixedSize(true);

        mTrailerRecyclerView.setNestedScrollingEnabled(false);

        mMovieTrailerAdapter = new MovieTrailerAdapter(this);

        mTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);

        mTrailerLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_trailer);

        int trailerLoaderId = TRAILERS_LOADER_ID;

        Bundle bundleForTrailerLoader = null;

        getSupportLoaderManager().initLoader(trailerLoaderId, bundleForTrailerLoader, dataTrailerLoaderListener);

        // Review
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_review);
        mErrorMessageDisplayReview = (TextView) findViewById(R.id.tv_error_message_display_review);

        mReviewLinearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mReviewRecyclerView.setLayoutManager(mReviewLinearLayoutManager);

        mReviewRecyclerView.setHasFixedSize(true);

        mReviewRecyclerView.setNestedScrollingEnabled(false);

        mMovieReviewAdapter = new MovieReviewAdapter(this);

        mReviewRecyclerView.setAdapter(mMovieReviewAdapter);

        mReviewLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_review);

        int reviewLoaderId = REVIEWS_LOADER_ID;

        Bundle bundleForReviewLoader = null;

        getSupportLoaderManager().initLoader(reviewLoaderId, bundleForReviewLoader, dataReviewLoaderListener);

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
                int id = movie.getId();
                String title = movie.getTitle();
                String releaseDate = movie.getReleaseDate();
                Double rating = movie.getVoteAverage();
                String overview = movie.getOverview();
                String imagePath = movie.getPosterPath();
                movieId = movie.getId();

                mMovie = new Movie();
                mMovie.setId(id);
                mMovie.setTitle(title);
                mMovie.setReleaseDate(releaseDate);
                mMovie.setPosterPath(imagePath);
                mMovie.setVoteAverage(rating);
                mMovie.setOverview(overview);

                mMovieTitleTextView.setText(title);
                mReleaseDateTextView.setText(MovieDateUtils.simpleDateFormat(releaseDate));
                mRatingTextView.setText(new DecimalFormat("##.#").format(rating));
                mOverviewTextView.setText(overview);
                Glide.with(this).load(TheMovieDbUtils.buildImageUrl(imagePath))
                        .listener(new RequestListener<URL, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, URL model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, URL model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .placeholder(R.drawable.placeholder)
                        .into(mMoviePosterImageView);
            }
        }

        mMarkAsFavoriteButton = (Button) findViewById(R.id.btn_mark_as_favorite);
        mMarkAsFavoriteButton.setBackgroundResource(android.R.drawable.btn_default);

        getMovieStatus(mMovie.getId());

        if(mMarkAsFavoriteButton.getText().toString().equalsIgnoreCase(getString(R.string.mark_as_favorite))){
            mMarkAsFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean isSuccess = saveFavoriteMovie(
                            mMovie.getId(),
                            mMovie.getTitle(),
                            mMovie.getReleaseDate(),
                            mMovie.getPosterPath(),
                            mMovie.getVoteAverage(),
                            mMovie.getOverview()
                    );

                    if(isSuccess){
                        Toast.makeText(DetailActivity.this, "Movie " + mMovie.getTitle() + " added to favorite", Toast.LENGTH_SHORT).show();
                        mMarkAsFavoriteButton.setText(R.string.remove_favorite);
                        mMarkAsFavoriteButton.setBackgroundColor(Color.GRAY);
                    }
                    getMovieStatus(mMovie.getId());
                }
            });
        }else {
            mMarkAsFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int result = delete(mMovie.getId());
                    if(result > 0){
                        Toast.makeText(DetailActivity.this, "Movie "+ mMovie.getTitle() + " removed from favorite", Toast.LENGTH_SHORT).show();

                        mMarkAsFavoriteButton.setText(R.string.mark_as_favorite);
                        mMarkAsFavoriteButton.setBackgroundResource(android.R.drawable.btn_default);
                        finish();
                    }
                    getMovieStatus(mMovie.getId());
                }
            });
        }
    }

    private boolean saveFavoriteMovie(int id, String title, String releaseDate, String posterPath,
                              Double voteAverage, String overview){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_ID, id);
        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);

        if(currentUri==null){
            getContentResolver().insert(MovieEntry.CONTENT_URI,movieValues);
            return true;
        }

        return true;
    }

    private void getMovieStatus(int id){
        Uri movieUri = MovieContract.MovieEntry.buildMovie();
        Log.v(LOG_TAG, "URI: " + movieUri);

        String sortOrder = MovieContract.MovieEntry._ID + " ASC";
        String selectionClause = "id_movie = " + id;

        Cursor cursor;
        int nCursor;

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        cursor = getContentResolver().query(
                movieUri,
                null,
                selectionClause,
                null,
                sortOrder
        );

        if(cursor != null){
            cursor.moveToFirst();
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
            nCursor = cursor.getCount();
            if(nCursor>0){
                mMarkAsFavoriteButton.setBackgroundColor(Color.GRAY);
                mMarkAsFavoriteButton.setText(getString(R.string.remove_favorite));
                mMarkAsFavoriteButton.setPadding(10, 0, 10, 0);
            }else {
                mMarkAsFavoriteButton.setBackgroundResource(android.R.drawable.btn_default);
                mMarkAsFavoriteButton.setText(getString(R.string.mark_as_favorite));
            }
        }
    }

    private int delete(int id) {
        int result = getContentResolver().delete(MovieEntry.CONTENT_URI, MovieEntry.COLUMN_ID+" =?", new String[]{String.valueOf(id)});
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }else if(id == R.id.action_share){
            shareIntent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMovieTrailerDataView(){
        mErrorMessageDisplayTrailer.setVisibility(View.INVISIBLE);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showTrailerErrorMessage(){
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplayTrailer.setText(getString(R.string.error_message_trailer));
        mErrorMessageDisplayTrailer.setVisibility(View.VISIBLE);
    }

    private void showTrailerNoDataMessage(){
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplayTrailer.setText(getString(R.string.no_trailer));
        mErrorMessageDisplayTrailer.setVisibility(View.VISIBLE);
    }

    private void showMovieReviewDataView(){
        mErrorMessageDisplayReview.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage(){
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplayReview.setText(getString(R.string.error_message_review));
        mErrorMessageDisplayReview.setVisibility(View.VISIBLE);
    }

    private void showReviewNoDataMessage(){
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplayReview.setText(getString(R.string.no_review));
        mErrorMessageDisplayReview.setVisibility(View.VISIBLE);
    }

    private LoaderManager.LoaderCallbacks<List<MovieTrailer>> dataTrailerLoaderListener
            = new LoaderManager.LoaderCallbacks<List<MovieTrailer>>() {
        @Override
        public Loader<List<MovieTrailer>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<MovieTrailer>>(getApplicationContext()) {

                List<MovieTrailer> movieTrailerList = null;

                @Override
                protected void onStartLoading() {
                    if(movieTrailerList != null){
                        deliverResult(movieTrailerList);
                    }else {
                        if(NetworkUtils.isConnected(getApplicationContext())){
                            mTrailerLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }else {
                            showTrailerErrorMessage();
                        }
                    }
                }

                @Override
                public List<MovieTrailer> loadInBackground() {
                    String movieIdString = String.valueOf(movieId);

                    URL movieTrailerRequestUrl = TheMovieDbUtils.buildMovieTrailerUrl(movieIdString);

                    Log.v("URL Request", movieTrailerRequestUrl.toString());

                    try {
                        String jsonMovieTrailerResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieTrailerRequestUrl);

                        List<MovieTrailer> trailersJsonData = NetworkUtils
                                .getMovieTrailerDataFromJson(DetailActivity.this, jsonMovieTrailerResponse);

                        return trailersJsonData;

                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(List<MovieTrailer> data) {
                    movieTrailerList = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
            mTrailerLoadingIndicator.setVisibility(View.INVISIBLE);
            if(data != null){
                showMovieTrailerDataView();
                mMovieTrailerAdapter.setMovieTrailerList(data);
                String name = data.get(0).getName();
                String key = data.get(0).getKey();
                String type = data.get(0).getType();
                int size = data.get(0).getSize();

                mMovieTrailer = new MovieTrailer();
                mMovieTrailer.setName(name);
                mMovieTrailer.setKey(key);
                mMovieTrailer.setType(type);
                mMovieTrailer.setSize(size);
            }else {
                if(NetworkUtils.isConnected(getApplicationContext())){
                    showTrailerNoDataMessage();
                }else {
                    showTrailerErrorMessage();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<List<MovieTrailer>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<MovieReview>> dataReviewLoaderListener
            = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<MovieReview>>(getApplicationContext()) {

                List<MovieReview> movieReviewList = null;

                @Override
                protected void onStartLoading() {
                    if(movieReviewList != null){
                        deliverResult(movieReviewList);
                    }else {
                        if(NetworkUtils.isConnected(getApplicationContext())){
                            mReviewLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }else {
                            showReviewErrorMessage();
                        }
                    }
                }

                @Override
                public List<MovieReview> loadInBackground() {
                    String movieIdString = String.valueOf(movieId);

                    URL movieReviewrRequestUrl = TheMovieDbUtils.buildMovieReviewsUrl(movieIdString);

                    Log.v("URL Request", movieReviewrRequestUrl.toString());

                    try {
                        String jsonMovieReviewResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieReviewrRequestUrl);

                        List<MovieReview> reviewsJsonData = NetworkUtils
                                .getMovieReviewDataFromJson(DetailActivity.this, jsonMovieReviewResponse);

                        return reviewsJsonData;

                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(List<MovieReview> data) {
                    movieReviewList = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
            mReviewLoadingIndicator.setVisibility(View.INVISIBLE);
            if(data != null){
                showMovieReviewDataView();
                mMovieReviewAdapter.setMovieReviewList(data);
            }else {
                if(NetworkUtils.isConnected(getApplicationContext())){
                    showReviewNoDataMessage();
                }else {
                    showReviewErrorMessage();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {

        }
    };

    @Override
    public void onClick(MovieTrailer movieTrailer) {
        String videoKey = movieTrailer.getKey();
        URL trailerUrl = TheMovieDbUtils.buildYoutubeVideoUrl(videoKey);
        openTrailer(trailerUrl.toString());
    }

    private void openTrailer(String url) {
        Uri trailerUri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(MovieReview movieReview) {

    }

    private String createShareMovieData(){
        String movieName = mMovie.getTitle();
        String releaseDate = MovieDateUtils.simpleDateFormat(mMovie.getReleaseDate());
        Double rating = mMovie.getVoteAverage();
        String overView = mMovie.getOverview();
        String urlTrailer = TheMovieDbUtils.buildYoutubeVideoUrl(mMovieTrailer.getKey()).toString();

        String shareMovieData = movieName +
                "\n" + "Release Date: " + releaseDate +
                "\n" + "Rating: " + rating +
                "\n\n" + overView +
                "\n\n" + "Trailer: " +
                urlTrailer;
        return shareMovieData;
    }

    private void shareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share Popular Movie");
        intent.putExtra(Intent.EXTRA_TEXT, createShareMovieData());
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "How do you want to share?"));
    }
}
