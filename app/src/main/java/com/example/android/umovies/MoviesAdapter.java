package com.example.android.umovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.umovies.utilities.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sve on 3/10/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesRVHolder> {
    private ItemClickListener itemClickListener;
    private List<Movie> movies;
    private Context context;

    public MoviesAdapter(Context context, List<Movie> movieTitles, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.movies = movieTitles;
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
    public void onBindViewHolder(final MoviesRVHolder holder, int position) {
        Movie currMovie = movies.get(position);
        String title = currMovie.getTitle();
        String imageUrl = currMovie.getImageURL();

        holder.movieTitle.setText(title);
        holder.progressBar.setVisibility(View.VISIBLE);

        String imageUrlStr = ImageUtils.getImageUrl(imageUrl);
        Picasso.with(context)
                .load(imageUrlStr)
                .fit()
                .into(holder.movieImg, ImageUtils.getImageCallback(holder.progressBar));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void populateMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies = new ArrayList<>(movies);
        notifyDataSetChanged();
    }
}
