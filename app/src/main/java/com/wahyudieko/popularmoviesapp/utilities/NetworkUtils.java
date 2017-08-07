package com.wahyudieko.popularmoviesapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.wahyudieko.popularmoviesapp.entities.Movie;
import com.wahyudieko.popularmoviesapp.entities.MovieReview;
import com.wahyudieko.popularmoviesapp.entities.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by EKO on 09/07/2017.
 */

public class NetworkUtils {

    public static List<Movie> getMovieDataFromJson(Context context, String movieJsonStr)
        throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";

        //Movie[] parsedMovieData = null;
        ArrayList<Movie> parsedMovieData = new ArrayList<Movie>();

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if(movieJson.has(TMDB_RESULTS)){
            JSONArray results = movieJson.getJSONArray(TMDB_RESULTS);

            Log.v("Results length", ""+results.length());

            if(results.length() == 0){
                return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

        for(int i=0; i < movieArray.length(); i++){

            int id;
            String title;
            String releaseDate;
            String posterPath;
            Double voteAverage;
            String overview;

            JSONObject movieObject = movieArray.getJSONObject(i);

            id = movieObject.getInt(TMDB_ID);
            title = movieObject.getString(TMDB_TITLE);
            releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
            posterPath = movieObject.getString(TMDB_POSTER_PATH);
            voteAverage = movieObject.getDouble(TMDB_VOTE_AVERAGE);
            overview = movieObject.getString(TMDB_OVERVIEW);

            Log.v("Movie data: ", title + " " + posterPath);

            Movie movie = new Movie();
            movie.setId(id);
            movie.setTitle(title);
            movie.setReleaseDate(releaseDate);
            movie.setPosterPath(posterPath);
            movie.setVoteAverage(voteAverage);
            movie.setOverview(overview);

            parsedMovieData.add(movie);

        }

        return parsedMovieData;
    }

    public static List<MovieTrailer> getMovieTrailerDataFromJson(Context context, String movieJsonStr)
            throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_TYPE = "type";
        final String TMDB_SIZE = "size";

        //Movie[] parsedMovieData = null;
        ArrayList<MovieTrailer> parsedMovieTrailerData = new ArrayList<MovieTrailer>();

        JSONObject movieTrailerJson = new JSONObject(movieJsonStr);

        if(movieTrailerJson.has(TMDB_RESULTS)){
            JSONArray results = movieTrailerJson.getJSONArray(TMDB_RESULTS);

            Log.v("Results length", ""+results.length());

            if(results.length() == 0){
                return null;
            }
        }

        JSONArray movieTrailerArray = movieTrailerJson.getJSONArray(TMDB_RESULTS);

        for(int i=0; i < movieTrailerArray.length(); i++){

            String key;
            String name;
            String type;
            int size;

            JSONObject movieTrailerObject = movieTrailerArray.getJSONObject(i);

            key = movieTrailerObject.getString(TMDB_KEY);
            name = movieTrailerObject.getString(TMDB_NAME);
            type = movieTrailerObject.getString(TMDB_TYPE);
            size = movieTrailerObject.getInt(TMDB_SIZE);

            Log.v("Movie Trailer data: ", key + " " + name);

            MovieTrailer movieTrailer = new MovieTrailer();
            movieTrailer.setKey(key);
            movieTrailer.setName(name);
            movieTrailer.setType(type);
            movieTrailer.setSize(size);

            if(type.equals("Trailer")){
                parsedMovieTrailerData.add(movieTrailer);
            }

        }
        return parsedMovieTrailerData;
    }

    public static List<MovieReview> getMovieReviewDataFromJson(Context context, String movieJsonStr)
            throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";

        ArrayList<MovieReview> parsedMovieReviewData = new ArrayList<MovieReview>();

        JSONObject movieReviewJson = new JSONObject(movieJsonStr);

        if(movieReviewJson.has(TMDB_RESULTS)){
            JSONArray results = movieReviewJson.getJSONArray(TMDB_RESULTS);

            Log.v("Results length", ""+results.length());

            if(results.length() == 0){
                return null;
            }
        }

        JSONArray movieReviewArray = movieReviewJson.getJSONArray(TMDB_RESULTS);

        for(int i=0; i < movieReviewArray.length(); i++){

            String id;
            String author;
            String content;
            String url;

            JSONObject movieReviewObject = movieReviewArray.getJSONObject(i);

            id = movieReviewObject.getString(TMDB_ID);
            author = movieReviewObject.getString(TMDB_AUTHOR);
            content = movieReviewObject.getString(TMDB_CONTENT);
            url = movieReviewObject.getString(TMDB_URL);

            Log.v("Movie Review data: ", author + " " + content);

            MovieReview movieReview = new MovieReview();
            movieReview.setId(id);
            movieReview.setAuthor(author);
            movieReview.setContent(content);
            movieReview.setUrl(url);

            parsedMovieReviewData.add(movieReview);

        }

        return parsedMovieReviewData;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
