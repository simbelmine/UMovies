package com.example.android.umovies;

public interface FetchSingleMovieTaskCompleteListener<T> {
    void onTaskCompleted(T result);
}
