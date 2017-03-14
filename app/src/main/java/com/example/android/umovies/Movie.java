package com.example.android.umovies;

import java.io.Serializable;

/**
 * Created by Sve on 3/10/17.
 */

public class Movie implements Serializable {
    private final String title;         // required
    private final String imageURL;      // required
    private final String synopsis;      // optional
    private final String releaseDate;   // optional
    private final String rating;         // optional
    private final String votes;


    private Movie(MovieBuilder builder) {
        this.title = builder.title;
        this.imageURL = builder.imageURL;
        this.synopsis = builder.synopsis;
        this.releaseDate = builder.releaseDate;
        this.rating = builder.rating;
        this.votes = builder.votes;
    }

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

    public static class MovieBuilder {
        private final String title;
        private final String imageURL;
        private String synopsis;
        private String releaseDate;
        private String rating;
        private String votes;

        public MovieBuilder(String title, String imageURL) {
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

        public Movie build() {
            return new Movie(this);
        }
    }
}
