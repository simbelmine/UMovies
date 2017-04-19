package com.example.android.umovies;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.android.umovies.Transformations.ZoomOutPageTransformer;
import com.example.android.umovies.utilities.WindowUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    public static final String TAG = "uMovies";
    private static final int TAB_COUNT = 3;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.initToolbarBar(this);
        setToolbarEnhancement();
        ButterKnife.bind(this);

        setupViewPager(viewPager);
        setupTabLayout();
        setViewPagerCachedTabs();
        setViewPagerTransformation();
    }

    private void setToolbarEnhancement() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.app_icon);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.dots_vertical));
    }

    private void setViewPagerCachedTabs() {
        // In current Application design, it's a relatively small number
        int cachedPageLimit = viewPager.getAdapter().getCount()-1;
        viewPager.setOffscreenPageLimit(cachedPageLimit);
    }

    private void setViewPagerTransformation() {
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String[] tabTitles = getResources().getStringArray(R.array.tab_titles);

        for(int i = 0; i < TAB_COUNT; i++) {
            adapter.addFrag(MoviesFragment.newInstance(i), tabTitles[i]);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                break;
            default:
                return false;
        }

        return true;
    }

    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
