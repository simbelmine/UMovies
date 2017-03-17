package com.example.android.umovies;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.umovies.Transformations.BlurTransformation;
import com.example.android.umovies.utilities.DataUtils;
import com.example.android.umovies.utilities.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity implements FetchSingleMovieTaskCompleteListener<Movie> {
    private static final int BLUR_RADIUS = 25;
    private static final int TOTAL_COUNT_RATING_STARS = 5;
    private FrameLayout movieContainer;
    private ImageView blurImage;
    private ImageView movieImage;
    private TextView ratingView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView synopsisView;
    private TextView votesView;
    private TextView runtimeView;
    private TextView revenueView;
    private TextView taglineView;
    private TextView genresView;
    private Movie movie;
    private int moviePos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        initView();

        getFromExtras();
        populateData(movie);
        populateFromNetwork();
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
            setRatingStars(movie.getRating());
            ratingView.setText(getRating(movie.getRating()));
            releaseDateView.setText(movie.getReleaseDate());
            synopsisView.setText(movie.getSynopsis());

            if(movie.isFullyUpdated()) {
                runtimeView.setText(getRuntime(movie.getRuntime()));
                revenueView.setText(getRevenue(movie.getRevenue()));
                taglineView.setText(movie.getTagline());
                genresView.setText(getGenres(movie.getGenres()));
            }
        }
    }

    private void populateFromNetwork() {
        if(DataUtils.isOnline(this)) {
            if (movie != null && !movie.isFullyUpdated()) {
                String movieId = movie.getId();
                new FetchSingleMovieTask(this, this, movieId, movie).execute();
            }
        }
        else {
            DataUtils.showSnackbarMessage(this, movieContainer, getResources().getString(R.string.no_network));
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

    private void getFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Movie movie = bundle.getParcelable(MainActivity.MOVIE_OBJ);
            if(movie != null) {
                this.movie = movie;
            }
            else {
                this.movie = null;
            }

            moviePos = bundle.getInt(MainActivity.MOVIE_POS, -1);
        }
    }

    private void initView() {
        movieContainer = (FrameLayout) findViewById(R.id.ll_container);
        blurImage = (ImageView) findViewById(R.id.iv_blur_img);
        movieImage = (ImageView) findViewById(R.id.iv_tumbnail_img);
        titleView = (TextView) findViewById(R.id.tv_movie_name);
        votesView = (TextView) findViewById(R.id.tv_votes);
        ratingView = (TextView) findViewById(R.id.tv_rating);
        releaseDateView = (TextView) findViewById(R.id.tv_movie_release_date);
        runtimeView = (TextView) findViewById(R.id.tv_movie_runtime);
        revenueView = (TextView) findViewById(R.id.tv_movie_revenue);
        genresView = (TextView) findViewById(R.id.tv_movie_genres);
        taglineView = (TextView) findViewById(R.id.tv_movie_tagline);
        synopsisView = (TextView) findViewById(R.id.tv_movie_synopsis);

        if(Build.VERSION.SDK_INT >= 21) {
            movieContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }

    @Override
    public void onTaskCompleted(Movie movie) {
        if(movie != null) {
            runtimeView.setText(getRuntime(movie.getRuntime()));
            revenueView.setText(getRevenue(movie.getRevenue()));
            taglineView.setText(movie.getTagline());
            genresView.setText(getGenres(movie.getGenres()));
            DataUtils.updateMovie(movie, moviePos);
        }
    }

    private String getRuntime(String original) {
        int runtimeNum = Integer.valueOf(original);
        int hours = Math.round(runtimeNum/60);
        int minutes = runtimeNum - (hours*60);

        return hours + " hr. " + minutes + " min.";
    }

    private String getRevenue(String original) {
        int billion = 1000000000;
        int million = 1000000;
        int thousand = 1000;
        int revenueAmount = Integer.valueOf(original);
        double result;
        String postfix;

        if(revenueAmount/billion > 0) {
            result = revenueAmount/(double)billion;
            postfix = "B";
        }
        else if(revenueAmount/million > 0) {
            result = revenueAmount/(double)million;
            postfix = "M";
        }
        else if(revenueAmount/thousand > 0) {
            result = revenueAmount/(double)thousand;
            postfix = "k";
        }
        else {
            result = revenueAmount;
            postfix = "";
        }

        result = (double)Math.round(result * 10d) / 10d;

        if(isZeroAfterFloatingPoint(result)){
            return "$" + (int)result + postfix;
        }

        return "$" + result + postfix;
    }

    private String getGenres(List<String> genres) {
        StringBuilder genresStr = new StringBuilder();

        for(String g : genres) {
            genresStr.append(g + "    ");
        }

        return genresStr.toString();
    }

    private String getRating(String rating) {
        double ratingVal = Double.valueOf(rating);
        double result = (double)Math.round((ratingVal/2) * 10d) / 10d;
        String ratingStr;

        if(isZeroAfterFloatingPoint(result)) {
            ratingStr = String.valueOf((int) result);
        }
        else {
            ratingStr = String.valueOf(result);
        }

        return ratingStr + "/" + TOTAL_COUNT_RATING_STARS;
    }

    private void setRatingStars(String rating) {
        double ratingNumValue = Double.valueOf(rating);

        inflateRatingStars(ratingNumValue);
    }

    private void inflateRatingStars(double ratingStarsCount) {
        int total = TOTAL_COUNT_RATING_STARS;
        LinearLayout ratingStarsContainer = (LinearLayout) findViewById(R.id.ll_stars_container);
        int fullStarsCount = ((int)ratingStarsCount)/2;

        for(int i = 0; i < fullStarsCount; i++) {
            addImageViewToLayout(ratingStarsContainer, R.mipmap.full_star);
            total--;
        }

        double halfStarsCount = ratingStarsCount - (double)fullStarsCount/2;
        if(halfStarsCount > 0){
            addImageViewToLayout(ratingStarsContainer, R.mipmap.half_star);
            total--;
        }

        for(int i = 0; i < total; i++) {
            addImageViewToLayout(ratingStarsContainer, R.mipmap.empty_star);
        }

        ratingStarsContainer.invalidate();
    }

    private void addImageViewToLayout(LinearLayout container, int resId) {
        float starSize = getResources().getDimension(R.dimen.star_size);
        float paddingRight = getResources().getDimension(R.dimen.star_padding);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)starSize, (int)starSize);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resId);
        imageView.setPadding(0, 0, (int)paddingRight, 0);
        imageView.setLayoutParams(layoutParams);
        container.addView(imageView);
    }

    private boolean isZeroAfterFloatingPoint(double val) {
        double res = val - Math.floor(val);
        res = (res%1.0)*10;

        if(res == 0)
            return true;

        return false;
    }

}
