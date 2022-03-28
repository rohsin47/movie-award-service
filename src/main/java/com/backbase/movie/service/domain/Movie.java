package com.backbase.movie.service.domain;

import javax.validation.constraints.NotNull;

public class Movie {

    private final int id;
    @NotNull
    private final String title;
    @NotNull
    private final String won;
    @NotNull
    private final long boxOffice;


    public Movie(int id, String title, String hasWon, long boxOffice) {
        this.id = id;
        this.title = title;
        this.won = hasWon;
        this.boxOffice = boxOffice;
    }

    public int getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getWon() {
        return won;
    }

    public long getBoxOffice() {
        return boxOffice;
    }
}
