package com.example.android.umovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sve on 4/27/17.
 */

public class TrailersRVHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_trailer_thumbnail) ImageView trailerThumbnail;
    @BindView(R.id.tv_trailer_name) TextView trailerName;

    public TrailersRVHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
