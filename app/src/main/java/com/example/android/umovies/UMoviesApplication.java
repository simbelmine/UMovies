package com.example.android.umovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Sve on 4/23/17.
 */

public class UMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
