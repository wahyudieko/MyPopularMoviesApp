package com.wahyudieko.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wahyudieko.popularmoviesapp.R;
import com.wahyudieko.popularmoviesapp.entities.MovieTrailer;

import java.util.List;

/**
 * Created by EKO on 07/08/2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder>{

    private List<MovieTrailer> mMovieTrailerList;
    private Context mContext;


    private final MovieTrailerAdapterOnClickHandler mClickHandler;

    public interface MovieTrailerAdapterOnClickHandler{
        void onClick(MovieTrailer movieTrailer);
    }

    public MovieTrailerAdapter(MovieTrailerAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrailerTitleTextView;

        public MovieTrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerTitleTextView = (TextView) view.findViewById(R.id.tv_trailer_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer movieTrailerSingle = mMovieTrailerList.get(adapterPosition);
            mClickHandler.onClick(movieTrailerSingle);
        }
    }

    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerAdapter.MovieTrailerAdapterViewHolder holder, int position) {
        holder.mTrailerTitleTextView.setText("Trailer "+(position+1));
    }

    @Override
    public int getItemCount() {
        if (null == mMovieTrailerList) return 0;
        return mMovieTrailerList.size();
    }

    public void setMovieTrailerList(List<MovieTrailer> movieTrailerList){
        mMovieTrailerList = movieTrailerList;
        notifyDataSetChanged();
    }


}
