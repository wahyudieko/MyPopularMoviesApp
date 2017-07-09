package com.wahyudieko.popularmoviesapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.wahyudieko.popularmoviesapp.Movie;

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

    public static List<Movie> getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
        throws JSONException{

        final String TMDB_RESULTS = "results";

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

            String title;
            String releaseDate;
            String posterPath;
            Double voteAverage;
            String overview;

            JSONObject movieObject = movieArray.getJSONObject(i);

            title = movieObject.getString(TMDB_TITLE);
            releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
            posterPath = movieObject.getString(TMDB_POSTER_PATH);
            voteAverage = movieObject.getDouble(TMDB_VOTE_AVERAGE);
            overview = movieObject.getString(TMDB_OVERVIEW);

            Log.v("Movie data: ", title + " " + posterPath);

            Movie movie = new Movie(title, releaseDate, posterPath, voteAverage, overview);
            parsedMovieData.add(movie);

        }

        return parsedMovieData;
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
