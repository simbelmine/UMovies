package com.example.android.umovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sve on 3/10/17.
 */

public class MoviesRVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected FrameLayout movieContainer;
    protected ImageView movieImg;
    protected TextView movieTitle;
    protected ItemClickListener itemClickListener;

    public MoviesRVHolder(View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        this.itemClickListener = itemClickListener;
        initItemView(itemView);
    }

    private void initItemView(View itemView) {
        movieContainer = (FrameLayout) itemView.findViewById(R.id.fl_item_movie_container);
        movieContainer.setOnClickListener(this);
        movieImg = (ImageView) itemView.findViewById(R.id.iv_item_img);
        movieTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick();
    }
}
