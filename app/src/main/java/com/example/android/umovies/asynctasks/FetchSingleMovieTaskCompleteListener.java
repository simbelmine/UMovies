package com.example.android.umovies.asynctasks;

public interface FetchSingleMovieTaskCompleteListener<T> {
    void onTaskCompleted(T result);
}
