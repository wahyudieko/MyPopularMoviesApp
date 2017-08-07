package com.wahyudieko.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wahyudieko.popularmoviesapp.adapters.MovieAdapter;
import com.wahyudieko.popularmoviesapp.data.MovieContract;
import com.wahyudieko.popularmoviesapp.entities.Movie;
import com.wahyudieko.popularmoviesapp.utilities.NetworkUtils;
import com.wahyudieko.popularmoviesapp.utilities.TheMovieDbUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView mFavoriteRecyclerView;

    private MovieAdapter mMovieAdapter;
    private MovieAdapter mFavoriteMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private GridLayoutManager mGridLayoutManager;
    private GridLayoutManager mFavoriteGridLayoutManager;

    private static final int MOVIES_LOADER_ID= 0;

    private static final int FAVORITE_MOVIES_LOADER_ID= 1;

    private String movieType = "popular";

    private List<Movie> mFavoriteMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mFavoriteRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_favorite_movie);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mGridLayoutManager =
                new GridLayoutManager(this, 2);
        mFavoriteGridLayoutManager =
                new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mFavoriteRecyclerView.setLayoutManager(mFavoriteGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mFavoriteRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mFavoriteMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);
        mFavoriteRecyclerView.setAdapter(mFavoriteMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int loaderId = MOVIES_LOADER_ID;

        LoaderManager.LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }


    private void showMovieDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(getString(R.string.error_message));
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showFavoriteMovieDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mFavoriteRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyMessage(){
        mFavoriteRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(getString(R.string.empty_favorite));
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movieList = null;

            @Override
            protected void onStartLoading() {
                if(movieList != null){
                    deliverResult(movieList);
                }else {
                    if(NetworkUtils.isConnected(getApplicationContext())){
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }else {
                        Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        showErrorMessage();
                    }
                }
            }

            @Override
            public List<Movie> loadInBackground() {

                String movieParam = movieType;

                URL movieRequestUrl = TheMovieDbUtils.buildMovieUrl(movieParam);

                Log.v("URL Request", movieRequestUrl.toString());

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    List<Movie> moviesJsonData = NetworkUtils
                            .getMovieDataFromJson(MainActivity.this, jsonMovieResponse);

                    return moviesJsonData;

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(List<Movie> data) {
                movieList = data;
                super.deliverResult(data);
            }
        };


    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mFavoriteRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(data != null){
            showMovieDataView();
            mMovieAdapter.setMovieList(data);
        }else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_popular:
                getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER_ID);
                if(NetworkUtils.isConnected(this)){
                    movieType = "popular";
                    getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                    return true;
                }else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_top_rated:
                getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER_ID);
                if(NetworkUtils.isConnected(this)){
                    movieType = "top_rated";
                    getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                    return true;
                }else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_favorite:
                int favoriteLoaderId = FAVORITE_MOVIES_LOADER_ID;
                Bundle bundleForFavoriteLoader = null;
                getSupportLoaderManager().initLoader(favoriteLoaderId, bundleForFavoriteLoader, dataFavoriteLoaderListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<Cursor> dataFavoriteLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri movieUri = MovieContract.MovieEntry.buildMovie();
            Log.v(LOG_TAG, "URI Loader: " + movieUri);

            String sortOrder = MovieContract.MovieEntry._ID + " DESC";

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getBaseContext(),
                    movieUri,
                    null,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mRecyclerView.setVisibility(View.GONE);
            setFavoriteRecyclerViewData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void setFavoriteRecyclerViewData(Cursor cursor){
        mFavoriteMovieList.clear();
        if(cursor == null){
            showEmptyMessage();
            Log.v(LOG_TAG, "database kosong");
        }else {
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
                boolean isCursor = cursor.moveToFirst();
                int nCursor = cursor.getCount();
                Log.v(LOG_TAG,"Jml cursor:"+ nCursor);
                if(isCursor){
                    for (int i=0;i<nCursor;i++){
                        Movie movie = new Movie();
                        int id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
                        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                        String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                        String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                        Double voteAverage = Double.valueOf(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                        String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));

                        movie.setId(id);
                        movie.setTitle(title);
                        movie.setReleaseDate(releaseDate);
                        movie.setPosterPath(posterPath);
                        movie.setVoteAverage(voteAverage);
                        movie.setOverview(overview);

                        Log.v(LOG_TAG,"judul "+ cursor.getPosition()+": "+posterPath);

                        mFavoriteMovieList.add(movie);
                        cursor.moveToNext();
                    }

                    mFavoriteMovieAdapter.setMovieList(mFavoriteMovieList);
                }
                showFavoriteMovieDataView();
            }else {
                showEmptyMessage();
            }
        }

        Log.v("Total Movie ",""+mFavoriteMovieList.size());
        Log.v("Object Movie",""+mFavoriteMovieList.toString());
    }

}
