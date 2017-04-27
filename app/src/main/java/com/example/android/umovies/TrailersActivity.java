package com.example.android.umovies;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersActivity extends AppCompatActivity {
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
        if(bundle != null && bundle.containsKey("trailerKeys")) {
            trailerKeys = bundle.getStringArrayList("trailerKeys");
        }
        if(bundle != null && bundle.containsKey("trailerNames")) {
            trailerNames = bundle.getStringArrayList("trailerNames");
        }
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager listLayoutManager = new LinearLayoutManager(this);
        recyclerViewTrailers.setLayoutManager(listLayoutManager);
    }

    private void setRecyclerViewAdapter() {
        trailersAdapter = new TrailersAdapter(this, trailerKeys, trailerNames);
        recyclerViewTrailers.setAdapter(trailersAdapter);
    }
}
