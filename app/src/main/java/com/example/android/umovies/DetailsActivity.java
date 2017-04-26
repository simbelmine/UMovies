package com.example.android.umovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.umovies.data.FavoriteMoviesContract;
import com.example.android.umovies.utilities.DataUtils;
import com.example.android.umovies.utilities.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements
        FetchSingleMovieTaskCompleteListener<Movie>,
        LoaderManager.LoaderCallbacks<Movie>,
        View.OnClickListener {
    private static final String MOVIE_REVIEW_LOADER_ID = "ReviewsLoaderId";
    public static final String ADD_TO_FAVORITES_ACTION ="AddToFavoritesAction";
    @BindView(R.id.ll_container) FrameLayout movieContainer;
    @BindView(R.id.iv_blur_img) ImageView blurImageView;
    @BindView(R.id.iv_tumbnail_img) ImageView movieImageView;
    @BindView(R.id.tv_rating) TextView ratingView;
    @BindView(R.id.tv_movie_name) TextView titleView;
    @BindView(R.id.tv_movie_release_date) TextView releaseDateView;
    @BindView(R.id.tv_movie_synopsis) TextView synopsisView;
    @BindView(R.id.tv_votes) TextView votesView;
    @BindView(R.id.tv_movie_runtime) TextView runtimeView;
    @BindView(R.id.tv_movie_revenue) TextView revenueView;
    @BindView(R.id.tv_movie_tagline) TextView taglineView;
    @BindView(R.id.tv_movie_genres) TextView genresView;
    @BindView(R.id.ll_movie_reviews) LinearLayout reviewsView;
    @BindView(R.id.iv_favorite_off) ImageView btnFavoriteOff;
    @BindView(R.id.iv_favorite_on) ImageView btnFavoriteOn;
    private Movie movie;
    private int moviePos;
    private LoaderManager loaderManager;
    private int loaderId;
    private int fragmentPosition;
    private boolean isFavoritesUpdated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);
        isFavoritesUpdated = false;

        initView();
        initLoader();
        getSavedInstanceStates(savedInstanceState);
        getFromExtras();
        setFavoriteStar();
        if(fragmentPosition != -1) {
            if (fragmentPosition == 2) {
                populateDataOffline(movie);
            } else {
                populateData(movie);
                populateFromNetwork();
                fetchReviewData(movie.getId() + "/reviews");
            }
        }
    }

    private void populateData(Movie movie) {
        if(movie != null) {
            String imgUrl = movie.getImageURL();
            String fullUrl = ImageUtils.getImageUrl(imgUrl);

            loadBlurBgImage(fullUrl);
            ImageUtils.loadImageWithPicasso(this, fullUrl, movieImageView, false);
            titleView.setText(movie.getTitle());
            String votesStr = (fragmentPosition != -1 && fragmentPosition == 2) ? movie.getVotes() : "(" + movie.getVotes() + " votes)";
            votesView.setText(votesStr);
            setRatingStars(movie.getRating());
            ratingView.setText(DataUtils.getRating(movie.getRating()));
            releaseDateView.setText(movie.getReleaseDate());
            synopsisView.setText(movie.getSynopsis());

            if(movie.isFullyUpdated()) {
                runtimeView.setText(DataUtils.getRuntime(movie.getRuntime()));
                revenueView.setText(DataUtils.getRevenue(movie.getRevenue()));
                taglineView.setText(movie.getTagline());
                genresView.setText(DataUtils.getGenres(movie.getGenres()));
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
            ImageUtils.loadImageWithPicasso(this, fullUrl, blurImageView, true);
        }
    }

    private void getFromExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Movie movie = bundle.getParcelable(MoviesFragment.MOVIE_OBJ);
            if(movie != null) {
                this.movie = movie;
            }
            else {
                this.movie = null;
            }

            moviePos = bundle.getInt(MoviesFragment.MOVIE_POS, -1);
            fragmentPosition = bundle.getInt(MoviesFragment.FRAGMENT_POSITION, -1);
        }
    }

    private void initView() {
        if(Build.VERSION.SDK_INT >= 21) {
            movieContainer.setPadding(0, (int)getResources().getDimension(R.dimen.padding_from_top_toolbar), 0, 0);
        }

        btnFavoriteOff.setOnClickListener(this);
        btnFavoriteOn.setOnClickListener(this);
    }

    @Override
    public void onTaskCompleted(Movie movie) {
        if(movie != null) {
            runtimeView.setText(DataUtils.getRuntime(movie.getRuntime()));
            revenueView.setText(DataUtils.getRevenue(movie.getRevenue()));
            taglineView.setText(movie.getTagline());
            genresView.setText(DataUtils.getGenres(movie.getGenres()));
            DataUtils.updateMovie(movie, moviePos);
        }
    }

    private void setRatingStars(String rating) {
        double ratingNumValue = Double.valueOf(rating);

        inflateRatingStars(ratingNumValue);
    }

    private void inflateRatingStars(double ratingStarsCount) {
        int total = DataUtils.TOTAL_COUNT_RATING_STARS;
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MOVIE_REVIEW_LOADER_ID, loaderId);
        super.onSaveInstanceState(outState);
    }

    private void getSavedInstanceStates(Bundle savedInstanceState) {
        updateLoaderId(savedInstanceState);
    }

    private void updateLoaderId(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_REVIEW_LOADER_ID)) {
            loaderId = savedInstanceState.getInt(MOVIE_REVIEW_LOADER_ID);
        }
        else {
            int id = getIntBundleExtra(MOVIE_REVIEW_LOADER_ID);
            loaderId = id;
        }
    }

    private int getIntBundleExtra(String extraName) {
        Bundle bundle = getIntent().getExtras();
        return bundle.getInt(extraName);
    }

    private void initLoader() {
        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(loaderId, null, this);
    }

    private void fetchReviewData(String path) {
        loadFetchedMovieReviews(path);
    }

    private void loadFetchedMovieReviews(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putParcelable("movie", movie);

        Loader<Movie> loader = getLoader();
        if(loader == null) {
            loaderManager.initLoader(loaderId, bundle, this);
        }
        else {
            loaderManager.restartLoader(loaderId, bundle, this);
        }
    }

    private Loader<Movie> getLoader() {
        return loaderManager.getLoader(loaderId);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        return new FetchMovieReviewsTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        addReviews(movie);
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
    }

    private void addReviews(Movie movie) {
        List<String> reviewAuthors = movie.getReviewAuthor();
        if(reviewAuthors == null || reviewAuthors.size() == 0) {
            TextView tv = createTextView("", false);
            reviewsView.addView(tv);
            return;
        }
        int size = reviewAuthors.size();
        int i = 0;

        while (i < size) {
            String author = movie.getReviewAuthor().get(i);
            String content = movie.getReviewContent().get(i);
            String rating = movie.getReviewRating().get(i);

            createReview(author, content, rating);

            i++;
        }
    }

    private void createReview(String author, String content, String rating) {
        TextView tvAuthor = createTextView(author, true);
        reviewsView.addView(tvAuthor);

        TextView tvContent = createTextView(content, false);
        reviewsView.addView(tvContent);

        TextView tvRating = createTextView(rating, false);
        reviewsView.addView(tvRating);
    }

    private TextView createTextView(String text, boolean isTitle) {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        tv.setLayoutParams(lparams);
        if(isTitle) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.txt_large_size));
            tv.setTextColor(getResources().getColor(R.color.secondary_color));
        }
        else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.txt_regular_size));
            tv.setTextColor(getResources().getColor(R.color.main_text_color));
        }
        tv.setText(text);

        return tv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_favorite_off:
                /**
                 * Favorites ON; Save to DB
                 */
                if(addToFavorites()) {
                    isFavoritesUpdated = true;
                    btnFavoriteOff.setVisibility(View.INVISIBLE);
                    btnFavoriteOn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_favorite_on:
                /**
                 * Favorites ON; Delete from DB
                 */
                if(deleteFromFavorites()) {
                    isFavoritesUpdated = true;
                    btnFavoriteOff.setVisibility(View.VISIBLE);
                    btnFavoriteOn.setVisibility(View.INVISIBLE);
                }

                break;
        }
    }

    private boolean addToFavorites() {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_NAME, movie.getTitle());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_IMG_URL, movie.getImageURL());

        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS, synopsisView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDateView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING, DataUtils.getRatingForDB(ratingView.getText().toString()));
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES, votesView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE, taglineView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RUNTIME, runtimeView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVENUE, revenueView.getText().toString());
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_GENRES, DataUtils.getGenresForDB(genresView.getText().toString()));
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_AUTHORS, getReviewDetails(1));
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_CONTENTS, getReviewDetails(2));
        cv.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_RATINGS, getReviewDetails(3));

        if(DataUtils.insertToDb(this, cv) != -1) {
            return true;
        }
        return false;
    }

    private boolean deleteFromFavorites() {
        if(DataUtils.deleteFromDb(this, movie.getId()) > 0) {
            return true;
        }
        return false;
    }

    private String getReviewDetails(int position) {
        if(reviewsView.getChildAt(position) instanceof TextView) {
            return ((TextView) reviewsView.getChildAt(position)).getText().toString();
        }
        return "";
    }

    private void populateDataOffline(Movie movie) {
        populateData(movie);
        runtimeView.setText(movie.getRuntime());
        revenueView.setText(movie.getRevenue());
        taglineView.setText(movie.getTagline());
        genresView.setText(DataUtils.getGenres(movie.getGenres()));
        addReviews(movie);
    }

    private void setFavoriteStar() {
        if(movie != null) {
            if(DataUtils.isMovieInDB(this, movie.getId())){
                btnFavoriteOff.setVisibility(View.INVISIBLE);
                btnFavoriteOn.setVisibility(View.VISIBLE);
            }
            else {
                btnFavoriteOff.setVisibility(View.VISIBLE);
                btnFavoriteOn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(isFavoritesUpdated) {
            Intent intentToReturn = new Intent(this, MainActivity.class);
            intentToReturn.setData(Uri.parse(ADD_TO_FAVORITES_ACTION));
            intentToReturn.putExtra(MoviesFragment.FRAGMENT_POSITION, fragmentPosition);
            startActivity(intentToReturn);
        }
    }
}
