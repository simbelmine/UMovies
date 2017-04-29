package com.example.android.umovies.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.umovies.DetailsActivity;
import com.example.android.umovies.ItemClickListener;
import com.example.android.umovies.R;
import com.example.android.umovies.TrailersAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersActivity extends AppCompatActivity implements
        ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener
{
    private static final String YOUTUBE_APP_PATH = "vnd.youtube:";
    private static final String YOUTUBE_WEB_PATH = "http://www.youtube.com/watch?v=";
    @BindView(R.id.srl_trailers_swipe_container) SwipeRefreshLayout trailerSwipeToRefreshLayout;
    @BindView(R.id.rv_trailers) RecyclerView recyclerViewTrailers;
    private TrailersAdapter trailersAdapter;
    private List<String> trailerKeys;
    private List<String> trailerNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trailers);
        ButterKnife.bind(this);

        initView();
        getFromExtras();
        setupRecyclerView();
        setRecyclerViewAdapter();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            recyclerViewTrailers.setPadding(0, (int) getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
        trailerSwipeToRefreshLayout.setOnRefreshListener(this);
    }

    private void getFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(DetailsActivity.TRAILER_KEYS_EXTRA)) {
            trailerKeys = bundle.getStringArrayList(DetailsActivity.TRAILER_KEYS_EXTRA);
        }
        if(bundle != null && bundle.containsKey(DetailsActivity.TRAILER_NAMES_EXTRA)) {
            trailerNames = bundle.getStringArrayList(DetailsActivity.TRAILER_NAMES_EXTRA);
        }
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager listLayoutManager = new LinearLayoutManager(this);
        recyclerViewTrailers.setLayoutManager(listLayoutManager);
    }

    private void setRecyclerViewAdapter() {
        trailersAdapter = new TrailersAdapter(this, trailerKeys, trailerNames, this);
        recyclerViewTrailers.setAdapter(trailersAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        String id = trailerKeys.get(position);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_PATH + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_WEB_PATH + id));
        switch (view.getId()) {
            case R.id.iv_play_icon:
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
                break;
            case R.id.iv_share:
                shareVideo(id);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if(trailerKeys == null || trailerNames == null) {
            getFromExtras();
        }
        setRecyclerViewAdapter();
        trailerSwipeToRefreshLayout.setRefreshing(false);
    }

    private void shareVideo(String id) {
        String webPath = YOUTUBE_WEB_PATH + id;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, webPath);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_lbl)));
    }
}
