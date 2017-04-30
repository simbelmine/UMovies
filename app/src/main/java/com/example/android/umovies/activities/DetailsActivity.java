package com.example.android.umovies.activities;

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

import com.example.android.umovies.Movie;
import com.example.android.umovies.R;
import com.example.android.umovies.asynctasks.FetchSingleMovieTask;
import com.example.android.umovies.asynctasks.FetchSingleMovieTaskCompleteListener;
import com.example.android.umovies.fragments.MoviesFragment;
import com.example.android.umovies.loaders.FetchMovieExtraDataTaskLoader;
import com.example.android.umovies.utilities.DataUtils;
import com.example.android.umovies.utilities.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_GENRES;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_IMG_URL;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_NAME;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RATING;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RELEASE_DATE;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVENUE;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_AUTHORS;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_CONTENTS;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_REVIEW_RATINGS;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_RUNTIME;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_SYNOPSIS;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TAGLINE;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TRAILER_KEYS;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TRAILER_NAMES;
import static com.example.android.umovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES;

public class DetailsActivity extends AppCompatActivity implements
        FetchSingleMovieTaskCompleteListener<Movie>,
        LoaderManager.LoaderCallbacks<Movie>,
        View.OnClickListener {
    public static final String MOVIE_REVIEW_LOADER_ID = "ReviewsLoaderId";
    public static final String MOVIE_TRAILERS_LOADER_ID = "TrailerKeysLoaderId";
    public static final String ADD_TO_FAVORITES_ACTION ="AddToFavoritesAction";
    public static final String MOVIE_EXTRA = "MovieExtra";
    public static final String PATH_EXTRA = "MovieDataPathExtra";
    public static final String LOADER_ID_EXTRA = "LoaderIdExtra";
    public static final String TRAILER_KEYS_EXTRA = "TrailerKeysExtra";
    public static final String TRAILER_NAMES_EXTRA = "TrailerNamesExtra";
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
    @BindView(R.id.iv_play_icon) ImageView btnPlayIcon;
    private Movie movie;
    private int moviePos;
    private LoaderManager loaderReviewsManager;
    private LoaderManager loaderTrailersManager;
    private int reviewsLoaderId = 1;
    private int trailersLoaderId = 2;
    private int fragmentPosition;
    private boolean isFavoritesUpdated;
    private List<String> trailerKeys;
    private List<String> trailerNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);
        isFavoritesUpdated = false;

        initView();
        initLoaders();
        getSavedInstanceStates(savedInstanceState);
        setFavoriteStar();
        if(fragmentPosition != -1) {
            if (fragmentPosition == 2) {
                populateDataOffline(movie);
            } else {
                populateData(movie);
                populateFromNetwork();
                fetchReviewData(movie, movie.getId() + "/reviews");
                fetchTrailerKeys(movie, movie.getId() + "/videos");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MOVIE_REVIEW_LOADER_ID, reviewsLoaderId);
        outState.putInt(MOVIE_TRAILERS_LOADER_ID, trailersLoaderId);
        outState.putParcelable(MOVIE_EXTRA, movie);

        super.onSaveInstanceState(outState);
    }

    private void getSavedInstanceStates(Bundle savedInstanceState) {
        int loaderId = getLoaderId(savedInstanceState, MOVIE_REVIEW_LOADER_ID);
        if(loaderId != 0) reviewsLoaderId = loaderId;
        loaderId = getLoaderId(savedInstanceState, MOVIE_TRAILERS_LOADER_ID);
        if(loaderId != 0) trailersLoaderId = loaderId;


        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_EXTRA)) {
            movie = savedInstanceState.getParcelable(MOVIE_EXTRA);
        }
        else {
            getFromExtras();
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
        btnPlayIcon.setOnClickListener(this);
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

    private int getLoaderId(Bundle savedInstanceState, String loaderId) {
        if(savedInstanceState != null && savedInstanceState.containsKey(loaderId)) {
            return savedInstanceState.getInt(loaderId);
        }
        else {
            return getIntBundleExtra(loaderId);
        }
    }

    private int getIntBundleExtra(String extraName) {
        Bundle bundle = getIntent().getExtras();
        return bundle.getInt(extraName);
    }

    private void initLoaders() {
        loaderReviewsManager = getSupportLoaderManager();
        loaderReviewsManager.initLoader(reviewsLoaderId, null, this);

        loaderTrailersManager = getSupportLoaderManager();
        loaderTrailersManager.initLoader(trailersLoaderId, null, this);
    }

    private void fetchReviewData(Movie movie, String path) {
        loadFetchedDataFromPath(movie, path, reviewsLoaderId);
    }

    private void fetchTrailerKeys(Movie movie, String path) {
        loadFetchedDataFromPath(movie, path, trailersLoaderId);
    }

    private void loadFetchedDataFromPath(Movie movie, String path, int loaderId) {
        Bundle bundle = new Bundle();
        bundle.putString(PATH_EXTRA, path);
        bundle.putParcelable(MOVIE_EXTRA, movie);
        String loaderIdStr = null;
        if(loaderId == reviewsLoaderId) loaderIdStr = MOVIE_REVIEW_LOADER_ID;
        else if(loaderId == trailersLoaderId) loaderIdStr = MOVIE_TRAILERS_LOADER_ID;
        bundle.putString(LOADER_ID_EXTRA, loaderIdStr);
        Loader<Movie> loader = getLoader(loaderId);


        if(loader == null) {
            if(loaderId == reviewsLoaderId) {
                loaderReviewsManager.initLoader(loaderId, bundle, this);
            }
            else if(loaderId == trailersLoaderId) {
                loaderTrailersManager.initLoader(loaderId, bundle, this);
            }
        }
        else {
            if(loaderId == reviewsLoaderId) {
                loaderReviewsManager.restartLoader(loaderId, bundle, this);
            }
            else if(loaderId == trailersLoaderId) {
                loaderTrailersManager.restartLoader(loaderId, bundle, this);
            }
        }
    }

    private Loader<Movie> getLoader(int loaderId) {
        return loaderReviewsManager.getLoader(loaderId);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        return new FetchMovieExtraDataTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        if(loader.getId() == reviewsLoaderId) {
            updateMovieWithReviews(movie);
            addReviews(movie);
        }
        if(loader.getId() == trailersLoaderId) {
            updateMovieWithTrailers(movie);
            trailerKeys = movie.getTrailerKeys();
            trailerNames = movie.getTrailerNames();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {
    }

    private void updateMovieWithReviews(Movie newMovie) {
        Movie.MovieBuilder movieBuilder = new Movie.MovieBuilder(this.movie);
        movieBuilder.reviewAuthor(newMovie.getReviewAuthor());
        movieBuilder.reviewContent(newMovie.getReviewContent());
        movieBuilder.reviewRating(newMovie.getReviewRating());
        this.movie = movieBuilder.build();
    }

    private void updateMovieWithTrailers(Movie newMovie) {
        Movie.MovieBuilder movieBuilder = new Movie.MovieBuilder(this.movie);
        movieBuilder.trailerKeys(newMovie.getTrailerKeys());
        movieBuilder.trailerNames(newMovie.getTrailerNames());
        this.movie = movieBuilder.build();
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
            String content = i < movie.getReviewContent().size() ? movie.getReviewContent().get(i) : "";
            String rating = i < movie.getReviewRating().size() ? movie.getReviewRating().get(i) : "";

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
            case R.id.iv_play_icon:
                loadTrailersActivity();
                break;
        }
    }

    private boolean addToFavorites() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MOVIE_ID, movie.getId());
        cv.put(COLUMN_MOVIE_NAME, movie.getTitle());
        cv.put(COLUMN_MOVIE_IMG_URL, movie.getImageURL());

        cv.put(COLUMN_MOVIE_SYNOPSIS, synopsisView.getText().toString());
        cv.put(COLUMN_MOVIE_RELEASE_DATE, releaseDateView.getText().toString());
        cv.put(COLUMN_MOVIE_RATING, DataUtils.getRatingForDB(ratingView.getText().toString()));
        cv.put(COLUMN_MOVIE_VOTES, votesView.getText().toString());
        cv.put(COLUMN_MOVIE_TAGLINE, taglineView.getText().toString());
        cv.put(COLUMN_MOVIE_RUNTIME, runtimeView.getText().toString());
        cv.put(COLUMN_MOVIE_REVENUE, revenueView.getText().toString());
        cv.put(COLUMN_MOVIE_GENRES, DataUtils.getGenresForDB(genresView.getText().toString()));
        cv.put(COLUMN_MOVIE_REVIEW_AUTHORS, DataUtils.getSeparatedStringFromList(movie.getReviewAuthor()));
        cv.put(COLUMN_MOVIE_REVIEW_CONTENTS, DataUtils.getSeparatedStringFromList(movie.getReviewContent()));
        cv.put(COLUMN_MOVIE_REVIEW_RATINGS, DataUtils.getSeparatedStringFromList(movie.getReviewRating()));


        String trailerKeysStr = DataUtils.getSeparatedStringFromList(trailerKeys);
        cv.put(COLUMN_MOVIE_TRAILER_KEYS, trailerKeysStr);
        String trailerNamesStr =  DataUtils.getSeparatedStringFromList(trailerNames);
        cv.put(COLUMN_MOVIE_TRAILER_NAMES, trailerNamesStr);

        return DataUtils.insert(this, cv);
    }

    private boolean deleteFromFavorites() {
        return DataUtils.delete(this, movie.getId());
    }

    private void populateDataOffline(Movie movie) {
        populateData(movie);
        runtimeView.setText(movie.getRuntime());
        revenueView.setText(movie.getRevenue());
        taglineView.setText(movie.getTagline());
        genresView.setText(DataUtils.getGenres(movie.getGenres()));
        addReviews(movie);
        trailerKeys = movie.getTrailerKeys();
        trailerNames = movie.getTrailerNames();
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

    private void loadTrailersActivity() {
        Intent intent = new Intent(this, TrailersActivity.class);
        if(trailerKeys != null) {
            List<String> trailerKeys = new ArrayList<>(this.trailerKeys);
            intent.putStringArrayListExtra(TRAILER_KEYS_EXTRA, (ArrayList<String>) trailerKeys);
        }
        if(trailerNames != null) {
            List<String> trailersNames = new ArrayList<>(trailerNames);
            intent.putStringArrayListExtra(TRAILER_NAMES_EXTRA, (ArrayList<String>) trailersNames);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(isFavoritesUpdated) {
            Intent intentToReturn = new Intent(this, MainActivity.class);
            intentToReturn.setData(Uri.parse(ADD_TO_FAVORITES_ACTION));
            intentToReturn.putExtra(MoviesFragment.FRAGMENT_POSITION, fragmentPosition);
            intentToReturn.putExtra(MoviesFragment.MOVIE_ITEM_POS,
                    getIntent().getIntExtra(MoviesFragment.MOVIE_ITEM_POS, 0));
            intentToReturn.putExtra("state", getIntent().getParcelableExtra("state"));
            startActivity(intentToReturn);
        }
    }
}
