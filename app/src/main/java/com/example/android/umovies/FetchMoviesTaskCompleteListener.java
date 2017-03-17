package com.example.android.umovies;

import java.util.List;

public interface FetchMoviesTaskCompleteListener<T> {
    void onTaskCompleted(List<T> result);
}
