package com.backbase.movie.service.domain;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Rating {

    private final int id;
    @NotNull
    private final Movie movie;
    @NotNull
    private final Account user;
    @NotNull
    private final Integer rating;

    public Rating(int id, Movie movie, Account user, Integer rating) {
        this.id = id;
        this.movie = movie;
        this.user = user;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Account getUser() {
        return user;
    }

    public Integer getRating() {
        return rating;
    }

}
