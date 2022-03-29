package com.backbase.movie.service.domain;

import javax.validation.constraints.NotNull;

public class AddAccountRequest {

    @NotNull
    private final String login;
    @NotNull
    private final String name;

    public AddAccountRequest(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }
}
