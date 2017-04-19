package com.example.android.umovies.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.android.umovies.MainActivity;
import com.example.android.umovies.R;

/**
 * Created by Sve on 3/12/17.
 */

public class WindowUtils {
    public static void initToolbarBar(Context context) {
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)context).findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setPadding(0, ((int)context.getResources().getDimension(R.dimen.padding_from_top_toolbar)), 0, 0);
        }

        ((AppCompatActivity)context).setSupportActionBar(toolbar);
    }

    public static void setColorToBackArrow(Context context) {
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)context).findViewById(R.id.toolbar);
        final PorterDuffColorFilter colorFilter
                = new PorterDuffColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

        for(int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);
            if(v instanceof AppCompatImageButton) {
                ((ImageButton)v).getDrawable().setColorFilter(colorFilter);
            }
        }
    }
}
