package com.example.android.umovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.umovies.DetailsActivity;
import com.example.android.umovies.MoviesFragment;
import com.example.android.umovies.R;
import com.example.android.umovies.ViewPagerAdapter;
import com.example.android.umovies.transformations.ZoomOutPageTransformer;
import com.example.android.umovies.utilities.WindowUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    public static final String TAG = "uMovies";
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private SharedPreferences mainSharedPrefs;
    private List<Boolean> fragmentsToShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.initToolbarBar(this);
        setToolbarEnhancement();
        ButterKnife.bind(this);
        getMainSharedPrefs();

        fragmentsToShow = new ArrayList<>(Arrays.asList(true, true, true));
        updateFragmentsToShow();
        initViewPager();
        setupTabLayout();
        setViewPagerCachedTabs();
        setViewPagerTransformation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!getVisibleFragmentsFromSettings().equals(fragmentsToShow)) {
            updateFragmentsToShow();
            initViewPager();
            setupTabLayout();
            setViewPagerCachedTabs();
            setViewPagerTransformation();
        }

    }

    private void setToolbarEnhancement() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.app_icon);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.dots_vertical));
    }

    private void getMainSharedPrefs() {
        mainSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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

    private void initViewPager() {
        setupViewPager();
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String[] tabTitles = getResources().getStringArray(R.array.tab_titles);

        for (int i = 0; i < fragmentsToShow.size(); i++) {
            if (fragmentsToShow.get(i)) {
                MoviesFragment moviesFragment = MoviesFragment.newInstance(i);
                adapter.addFrag(moviesFragment, tabTitles[i]);
            }
        }
        viewPager.setAdapter(adapter);

    }

    private void updateFragmentsToShow() {
        List<Boolean> fragmentsVisibilityList = getVisibleFragmentsFromSettings();
        if(!fragmentsVisibilityList.equals(fragmentsToShow)) {
            fragmentsToShow = fragmentsVisibilityList;
        }
    }

    private List<Boolean> getVisibleFragmentsFromSettings() {
        boolean popularMoviesVisibility = mainSharedPrefs.getBoolean(getString(R.string.popular_key), true);
        boolean topRatedMoviesVisibility = mainSharedPrefs.getBoolean(getString(R.string.top_rated_key), true);
        boolean favoritesMoviesVisibility = mainSharedPrefs.getBoolean(getString(R.string.favorites_key), true);
        List<Boolean> fragmentsVisibilityList = new ArrayList<>();
        fragmentsVisibilityList.add(popularMoviesVisibility);
        fragmentsVisibilityList.add(topRatedMoviesVisibility);
        fragmentsVisibilityList.add(favoritesMoviesVisibility);

        return fragmentsVisibilityList;
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(DetailsActivity.ADD_TO_FAVORITES_ACTION.equals(intent.getDataString())) {
            viewPager.setAdapter(adapter);
            if(intent.hasExtra(MoviesFragment.FRAGMENT_POSITION)) {
                viewPager.setCurrentItem(intent.getIntExtra(MoviesFragment.FRAGMENT_POSITION, MoviesFragment.FAVORITES_FRAGMENT_POSITION));
            }
        }
    }
}
