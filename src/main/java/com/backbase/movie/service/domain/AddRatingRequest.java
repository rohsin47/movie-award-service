package com.backbase.movie.service.domain;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class AddRatingRequest {

    @NotNull
    private final String movieTitle;
    @NotNull
    private final String userLogin;
    @NotNull
    @Range(min = 1, max = 10)
    private final Integer rating;

    public AddRatingRequest(String movieTitle, String userLogin, Integer rating) {
        this.movieTitle = movieTitle;
        this.userLogin = userLogin;
        this.rating = rating;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public Integer getRating() {
        return rating;
    }
}
