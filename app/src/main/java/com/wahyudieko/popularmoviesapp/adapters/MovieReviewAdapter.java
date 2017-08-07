package com.wahyudieko.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wahyudieko.popularmoviesapp.R;
import com.wahyudieko.popularmoviesapp.entities.MovieReview;

import java.util.List;

/**
 * Created by EKO on 07/08/2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder>{

    private List<MovieReview> mMovieReviewList;
    private Context mContext;


    private final MovieReviewAdapterOnClickHandler mClickHandler;

    public interface MovieReviewAdapterOnClickHandler{
        void onClick(MovieReview movieReview);
    }

    public MovieReviewAdapter(MovieReviewAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mAuthorNameTextView;
        public final TextView mReviewTextView;

        public MovieReviewAdapterViewHolder(View view) {
            super(view);
            mAuthorNameTextView = (TextView) view.findViewById(R.id.tv_author_name);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_movie_review);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieReview movieReviewSingle = mMovieReviewList.get(adapterPosition);
            mClickHandler.onClick(movieReviewSingle);
        }
    }

    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapterViewHolder holder, int position) {
        MovieReview movieReview = mMovieReviewList.get(position);
        String authorName = movieReview.getAuthor();
        String review = movieReview.getContent();
        holder.mAuthorNameTextView.setText(authorName);
        holder.mReviewTextView.setText(Html.fromHtml(review));
    }

    @Override
    public int getItemCount() {
        if (null == mMovieReviewList) return 0;
        return mMovieReviewList.size();
    }

    public void setMovieReviewList(List<MovieReview> movieReviewList){
        mMovieReviewList = movieReviewList;
        notifyDataSetChanged();
    }


}
