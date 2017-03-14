package com.example.android.umovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.umovies.Transformations.BlurTransformation;
import com.example.android.umovies.utilities.DataUtils;
import com.example.android.umovies.utilities.ImageUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

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
    private TextView runtimeView;
    private TextView revenueView;
    private TextView taglineView;
    private TextView genresView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        initView();

        Movie movie = getFromExtras();
        populateData(movie);
        String movieId = movie.getId();
        new FetchAdditionalMovieData(this, movieId, movie).execute();
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
        runtimeView = (TextView) findViewById(R.id.tv_movie_runtime);
        revenueView = (TextView) findViewById(R.id.tv_movie_revenue);
        genresView = (TextView) findViewById(R.id.tv_movie_genres);
        taglineView = (TextView) findViewById(R.id.tv_movie_tagline);
        synopsisView = (TextView) findViewById(R.id.tv_movie_synopsis);

        if(Build.VERSION.SDK_INT >= 21) {
            movieContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }
    }

    private class FetchAdditionalMovieData extends AsyncTask<Void, Void, Movie> {
        private Context context;
        private String movieId;
        private Movie movie;

        FetchAdditionalMovieData(Context context, String movieId, Movie movie) {
            this.context = context;
            this.movieId = movieId;
            this.movie = movie;
        }

        @Override
        protected Movie doInBackground(Void... params) {
            if(movieId != null && movie != null) {
                URL url = DataUtils.getDBUrl(context, movieId);

                try {
                    String response = DataUtils.getResponseFromHTTP(url);
                    Movie movieWithAdditionalData = DataUtils.getMovieAdditionalData(movie, response);

                    return movieWithAdditionalData;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if(movie != null) {
                runtimeView.setText(getRuntime(movie.getRuntime()));
                revenueView.setText(getRevenue(movie.getRevenue()));
                taglineView.setText(movie.getTagline());
                genresView.setText(getGenres(movie.getGenres()));
            }
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
            postfix = "M";
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

        double afterFloatingPointVal = result%Math.floor(result);
        afterFloatingPointVal = (afterFloatingPointVal%1.0)*10;

        if((int)afterFloatingPointVal == 0) {
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
}
