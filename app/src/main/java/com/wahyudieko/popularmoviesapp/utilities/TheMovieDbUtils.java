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
    private static final String BASE_YOUTUBE_VIDEO_URL= "https://www.youtube.com/watch?v=";

    private static final String MOVIE_PATH = "movie";
    private static final String TRAILER_PATH = "videos";
    private static final String REVIEW_PATH = "reviews";
    private static final String IMAGE_TYPE= "w185";
    private static final String API_QUERY = "api_key";
    private static final String MY_API_KEY = "PLEASE PUT YOUR API KEY HERE";

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

    public static URL buildMovieTrailerUrl(String movieId){
        Uri builtUri = Uri.parse(BASE_PUBLIC_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(TRAILER_PATH)
                .appendQueryParameter(API_QUERY, MY_API_KEY)
                .build();
        URL trailerUrl = null;
        try {
            trailerUrl = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return trailerUrl;
    }

    public static URL buildMovieReviewsUrl(String movieId){
        Uri builtUri = Uri.parse(BASE_PUBLIC_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(API_QUERY, MY_API_KEY)
                .build();
        URL reviewUrl = null;
        try {
            reviewUrl = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return reviewUrl;
    }

    public static URL buildYoutubeVideoUrl(String videoKey){
        Uri builtUri = Uri.parse(BASE_YOUTUBE_VIDEO_URL).buildUpon()
                .build();
        URL videoUrl = null;
        try {
            videoUrl = new URL(builtUri.toString()+videoKey);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return videoUrl;
    }


}
