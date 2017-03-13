package com.example.android.umovies;

import android.app.Activity;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.umovies.utilities.ImageUtils;
import com.example.android.umovies.utilities.WindowUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Sve on 3/10/17.
 */

public class DetailsActivity extends AppCompatActivity {
    private ConstraintLayout movieContainer;
    private ImageView bluredImage;
    private ImageView movieImage;
    private TextView ratingView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView synopsisView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        initView();

        Movie movie = getFromExtras();
        populateData(movie);
    }

    private void populateData(Movie movie) {
        if(movie != null) {
            progressBar.setVisibility(View.VISIBLE);
            String imgUrl = movie.getImageURL();
            Picasso.with(this)
                    .load(ImageUtils.getImageUrl(imgUrl))
                    .fit()
                    .into(movieImage, ImageUtils.getImageCallback(progressBar));
            ratingView.setText(movie.getRating());
            titleView.setText(movie.getTitle());
            releaseDateView.setText(movie.getReleaseDate());
            synopsisView.setText(movie.getSynopsis());
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private Movie getFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Movie movie = (Movie)bundle.get(MainActivity.MOVIE_OBJ);
            if(movie != null) {
                Log.v(MainActivity.TAG, "Name = " + movie.getTitle());
                return movie;
            }
            else {
                return null;
            }
        }
        return null;
    }

    private void initView() {
        movieContainer = (ConstraintLayout) findViewById(R.id.cl_container);
        bluredImage = (ImageView) findViewById(R.id.iv_blured_img);
        movieImage = (ImageView) findViewById(R.id.iv_tumbnail_img);
        ratingView = (TextView) findViewById(R.id.tv_rating);
        titleView = (TextView) findViewById(R.id.tv_movie_name);
        releaseDateView = (TextView) findViewById(R.id.tv_movie_release_date);
        synopsisView = (TextView) findViewById(R.id.tv_movie_synopsis);
        progressBar = (ProgressBar) findViewById(R.id.pb_movie_img_progress);

        if(Build.VERSION.SDK_INT >= 21) {
            movieContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }
}
