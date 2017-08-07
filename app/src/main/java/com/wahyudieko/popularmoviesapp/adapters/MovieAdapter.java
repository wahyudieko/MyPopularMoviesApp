package com.wahyudieko.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wahyudieko.popularmoviesapp.R;
import com.wahyudieko.popularmoviesapp.entities.Movie;
import com.wahyudieko.popularmoviesapp.utilities.TheMovieDbUtils;

import java.net.URL;
import java.util.List;

import static android.view.View.OnClickListener;

/**
 * Created by EKO on 08/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{
    private List<Movie> mMovieList;
    private Context mContext;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

        public final ImageView mPosterImageView;
        public final ProgressBar loadImageProgressBar;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.movie_poster);
            loadImageProgressBar = (ProgressBar) view.findViewById(R.id.pb_poster_loading);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movieSingle = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movieSingle);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        Movie movie = mMovieList.get(position);
        String posterPath = movie.getPosterPath();
        URL imageUrl = TheMovieDbUtils.buildImageUrl(posterPath);
        Log.v("MovieAdapter.java", "Image URL - "+ imageUrl);
        Glide.with(mContext).load(imageUrl)
                .listener(new RequestListener<URL, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, URL model, Target<GlideDrawable> target, boolean isFirstResource) {
                        movieAdapterViewHolder.loadImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, URL model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        movieAdapterViewHolder.loadImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(movieAdapterViewHolder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList){
        mMovieList = movieList;
        notifyDataSetChanged();
    }
}
