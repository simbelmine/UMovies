package com.example.android.umovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sve on 4/27/17.
 */

public class TrailersRVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.ll_item_trailer_container) LinearLayout trailerContainer;
    @BindView(R.id.iv_trailer_thumbnail) ImageView trailerThumbnail;
    @BindView(R.id.tv_trailer_name) TextView trailerName;
    protected ItemClickListener itemClickListener;

    public TrailersRVHolder(View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemClickListener = itemClickListener;
        initItemView();
    }

    private void initItemView() {
        trailerContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(getAdapterPosition());
    }
}
