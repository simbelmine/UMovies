package com.example.android.umovies.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.umovies.R;
import com.example.android.umovies.utilities.WindowUtils;

/**
 * Created by Sve on 4/17/17.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        WindowUtils.initToolbarBar(this);
        setToolbarEnhancement();
    }

    private void setToolbarEnhancement() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WindowUtils.setColorToBackArrow(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
