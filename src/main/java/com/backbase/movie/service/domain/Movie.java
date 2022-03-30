package com.backbase.movie.service.domain;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return title.equals(movie.title) && won.equals(movie.won);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, won);
    }
}
