package com.example.android.umovies.utilities;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.umovies.R;

/**
 * Created by Sve on 3/12/17.
 */

public class WindowUtils {
    public static void initToolbarBar(Context context) {
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)context).findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.mipmap.app_icon);
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setPadding(0, ((int)context.getResources().getDimension(R.dimen.padding_from_top_toolbar)), 0, 0);
        }
        toolbar.setOverflowIcon(context.getResources().getDrawable(R.mipmap.dots_vertical));
        ((AppCompatActivity)context).setSupportActionBar(toolbar);
    }
}
