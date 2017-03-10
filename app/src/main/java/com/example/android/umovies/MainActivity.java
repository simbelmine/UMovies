package com.example.android.umovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private RecyclerView moviesRView;
    private MoviesAdapter moviesAdapter;
    private List<String> movieTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupRecyclerView();
        fetchData();
        setRecyclerViewAdapter();
    }

    private void fetchData() {
        movieTitles = new ArrayList<>();
        movieTitles.add("AAaaaaaaa");
        movieTitles.add("Bbbb");
        movieTitles.add("CCCCCCcccc");
        movieTitles.add("DDdddd");
        movieTitles.add("Eeeeeeeee");
        movieTitles.add("Ffffff");
        movieTitles.add("GGggggggg");
        movieTitles.add("HHhHhhhh");
        movieTitles.add("IIiiii");
        movieTitles.add("JJjjjjjjjjjjj");
    }

    private void setRecyclerViewAdapter() {
        moviesAdapter = new MoviesAdapter(movieTitles, this);
        moviesRView.setAdapter(moviesAdapter);
    }

    private void setupRecyclerView() {
        // TODO Change LinearLayout to GridLayout
        LinearLayoutManager llmanager = new LinearLayoutManager(this);
        moviesRView.setLayoutManager(llmanager);
    }

    private void initView() {
        moviesRView = (RecyclerView) findViewById(R.id.rv_movies);
    }


    @Override
    public void onItemClick() {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }
}
