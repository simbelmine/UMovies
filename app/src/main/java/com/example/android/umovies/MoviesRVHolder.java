package com.example.android.umovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sve on 3/10/17.
 */

public class MoviesRVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.fl_item_movie_container) LinearLayout movieContainer;
    @BindView(R.id.iv_item_img) ImageView movieImg;
    @BindView(R.id.tv_item_title) TextView movieTitle;
    protected ItemClickListener itemClickListener;

    public MoviesRVHolder(View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        this.itemClickListener = itemClickListener;
        ButterKnife.bind(this, itemView);
        initItemView();
    }

    private void initItemView() {
        movieContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(getAdapterPosition());
    }
}
