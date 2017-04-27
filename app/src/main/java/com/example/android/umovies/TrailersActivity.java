package com.example.android.umovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersActivity extends AppCompatActivity implements ItemClickListener {
    private static final String YOUTUBE_APP_PATH = "vnd.youtube:";
    private static final String YOUTUBE_WEB_PATH = "http://www.youtube.com/watch?v=";
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
    public void onItemClick(int position) {
        String id = trailerKeys.get(position);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_PATH + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_WEB_PATH + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
