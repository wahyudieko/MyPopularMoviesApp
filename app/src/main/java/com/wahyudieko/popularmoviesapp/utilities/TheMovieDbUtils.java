package com.wahyudieko.popularmoviesapp.utilities;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by EKO on 09/07/2017.
 */

public class TheMovieDbUtils {

    private static final String BASE_PUBLIC_API_URL= "http://api.themoviedb.org/3";
    private static final String BASE_IMAGE_API_URL= "http://image.tmdb.org/t/p";
    private static final String MOVIE_PATH = "movie";
    private static final String IMAGE_TYPE= "w185";
    private static final String API_QUERY = "api_key";
    private static final String MY_API_KEY = "Please put your API here";

    public static URL buildImageUrl(String imagePath){
        Uri builtUri = Uri.parse(BASE_IMAGE_API_URL).buildUpon()
                .appendPath(IMAGE_TYPE)
                .build();
        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString()+imagePath);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return imageUrl;
    }

    public static URL buildMovieUrl(String movieType){
        Uri builtUri = Uri.parse(BASE_PUBLIC_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieType)
                .appendQueryParameter(API_QUERY, MY_API_KEY)
                .build();
        URL movieUrl = null;
        try {
            movieUrl = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return movieUrl;
    }


}
