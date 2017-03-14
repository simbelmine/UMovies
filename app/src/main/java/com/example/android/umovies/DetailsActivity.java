package com.example.android.umovies;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.umovies.Transformations.BlurTransformation;
import com.example.android.umovies.utilities.ImageUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Sve on 3/10/17.
 */

public class DetailsActivity extends AppCompatActivity {
    private static final int BLUR_RADIUS = 25;
    private FrameLayout movieContainer;
    private ImageView blurImage;
    private ImageView movieImage;
    private TextView ratingView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView synopsisView;
    private TextView votesView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        initView();

        Movie movie = getFromExtras();
        populateData(movie);
    }

    private void populateData(Movie movie) {
        if(movie != null) {
            String imgUrl = movie.getImageURL();
            String fullUrl = ImageUtils.getImageUrl(imgUrl);

            loadBlurBgImage(fullUrl);
            Picasso.with(this)
                    .load(fullUrl)
                    .fit()
                    .into(movieImage);
            titleView.setText(movie.getTitle());
            votesView.setText("(" + movie.getVotes() + " votes)");
            ratingView.setText(movie.getRating()+"/10");
            releaseDateView.setText(movie.getReleaseDate());
            synopsisView.setText(movie.getSynopsis());
        }
    }

    private void loadBlurBgImage(String fullUrl) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Picasso.with(this)
                    .load(fullUrl)
                    .fit()
                    .transform(new BlurTransformation(this, BLUR_RADIUS))
                    .into(blurImage);
        }
    }

    private Movie getFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Movie movie = (Movie)bundle.get(MainActivity.MOVIE_OBJ);
            if(movie != null) {
                return movie;
            }
            else {
                return null;
            }
        }
        return null;
    }

    private void initView() {
        movieContainer = (FrameLayout) findViewById(R.id.ll_container);
        blurImage = (ImageView) findViewById(R.id.iv_blur_img);
        movieImage = (ImageView) findViewById(R.id.iv_tumbnail_img);
        titleView = (TextView) findViewById(R.id.tv_movie_name);
        votesView = (TextView) findViewById(R.id.tv_votes);
        ratingView = (TextView) findViewById(R.id.tv_rating);
        releaseDateView = (TextView) findViewById(R.id.tv_movie_release_date);
        synopsisView = (TextView) findViewById(R.id.tv_movie_synopsis);

        if(Build.VERSION.SDK_INT >= 21) {
            movieContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }
}
