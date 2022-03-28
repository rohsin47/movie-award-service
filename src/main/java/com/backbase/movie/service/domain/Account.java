package com.backbase.movie.service.domain;

import javax.validation.constraints.NotNull;

public class Account {

    private final int id;
    @NotNull
    private final String login;
    @NotNull
    private final String name;

    public Account(int id, String login, String name) {
        this.id = id;
        this.login = login;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }
}
