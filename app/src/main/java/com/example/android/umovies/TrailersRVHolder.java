package com.example.android.umovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.umovies.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersRVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.ll_item_trailer_container) LinearLayout trailerContainer;
    @BindView(R.id.iv_trailer_thumbnail) ImageView trailerThumbnail;
    @BindView(R.id.tv_trailer_name) TextView trailerName;
    @BindView(R.id.iv_share) ImageView shareBtn;
    @BindView(R.id.iv_play_icon) ImageView playBtn;
    protected ItemClickListener itemClickListener;

    public TrailersRVHolder(View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemClickListener = itemClickListener;
        initItemView();
    }

    private void initItemView() {
        playBtn.setOnClickListener(this);

        shareBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v, getAdapterPosition());
    }
}
