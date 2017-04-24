package com.example.android.umovies;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sve on 3/10/17.
 */

public class Movie implements Parcelable {
    private final String id;            // required
    private final String title;         // required
    private final String imageURL;      // required
    private final String synopsis;      // optional
    private final String releaseDate;   // optional
    private final String rating;        // optional
    private final String votes;         // optional
    private final String tagline;       // optional
    private final String runtime;       // optional
    private final String revenue;       // optional
    private final List<String> genres;  // optional
    private final List<String> reviewAuthor;  // optional
    private final List<String> reviewContent; // optional
    private final List<String> reviewRating;  // optional

    private Movie(MovieBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.imageURL = builder.imageURL;
        this.synopsis = builder.synopsis;
        this.releaseDate = builder.releaseDate;
        this.rating = builder.rating;
        this.votes = builder.votes;
        this.tagline = builder.tagline;
        this.runtime = builder.runtime;
        this.revenue = builder.revenue;
        this.genres = builder.genres;
        this.reviewAuthor = builder.reviewAuthor;
        this.reviewContent = builder.reviewContent;
        this.reviewRating = builder.reviewRating;
    }

    private Movie(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.imageURL = in.readString();
        this.synopsis = in.readString();
        this.releaseDate = in.readString();
        this.rating = in.readString();
        this.votes = in.readString();
        this.tagline = in.readString();
        this.runtime = in.readString();
        this.revenue = in.readString();
        this.genres = in.readArrayList(null);
        this.reviewAuthor = in.readArrayList(null);
        this.reviewContent = in.readArrayList(null);
        this.reviewRating = in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(imageURL);
        dest.writeString(synopsis);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(votes);
        dest.writeString(tagline);
        dest.writeString(runtime);
        dest.writeString(revenue);
        dest.writeList(genres);
        dest.writeList(reviewAuthor);
        dest.writeList(reviewContent);
        dest.writeList(reviewRating);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {return id;}

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public String getVotes(){
        return votes;
    }

    public String getTagline() {
        return tagline;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getReviewAuthor() {
        return reviewAuthor;
    }

    public List<String> getReviewContent() {
        return reviewContent;
    }

    public List<String> getReviewRating() {
        return reviewRating;
    }

    public static class MovieBuilder {
        private final String id;
        private final String title;
        private final String imageURL;
        private String synopsis;
        private String releaseDate;
        private String rating;
        private String votes;
        private String tagline;
        private String runtime;
        private String revenue;
        private List<String> genres;
        private List<String> reviewAuthor;
        private List<String> reviewContent;
        private List<String> reviewRating;

        public MovieBuilder(String id, String title, String imageURL) {
            this.id = id;
            this.title = title;
            this.imageURL = imageURL;
        }

        public MovieBuilder synopsis(String synopsis) {
            this.synopsis = synopsis;
            return this;
        }

        public MovieBuilder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public MovieBuilder rating(String rating) {
            this.rating = rating;
            return this;
        }

        public MovieBuilder votes(String votes) {
            this.votes = votes;
            return this;
        }

        public MovieBuilder tagline(String tagline) {
            this.tagline = tagline;
            return this;
        }

        public MovieBuilder runtime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        public MovieBuilder revenue(String revenue) {
            this.revenue = revenue;
            return this;
        }
        
        public MovieBuilder genres(List<String> genres) {
            this.genres = genres;
            return this;
        }
        public MovieBuilder reviewAuthor(List<String> reviewAuthor) {
            this.reviewAuthor = reviewAuthor;
            return this;
        }
        public MovieBuilder reviewContent(List<String> reviewContent) {
            this.reviewContent = reviewContent;
            return this;
        }
        public MovieBuilder reviewRating(List<String> reviewRating) {
            this.reviewRating = reviewRating;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    private boolean isFullyUpdated;
    public boolean isFullyUpdated() {
        return isFullyUpdated;
    }
    public void setIsFullyUpdated(boolean isFullyUpdated) {
        this.isFullyUpdated = isFullyUpdated;
    }
}
