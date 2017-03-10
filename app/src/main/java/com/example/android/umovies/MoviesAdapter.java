package com.example.android.umovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sve on 3/10/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesRVHolder> {
    private ItemClickListener itemClickListener;
    private List<String> movieTitles;

    public MoviesAdapter(List<String> movieTitles, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.movieTitles = movieTitles;
    }

    @Override
    public MoviesRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.movie_item, parent, false);
        MoviesRVHolder viewHolder = new MoviesRVHolder(movieView, itemClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesRVHolder holder, int position) {
        holder.movieTitle.setText(movieTitles.get(position));
    }

    @Override
    public int getItemCount() {
        return movieTitles.size();
    }
}
