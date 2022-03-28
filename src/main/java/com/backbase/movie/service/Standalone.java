package com.backbase.movie.service;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Standalone {
    public static void main(String... args) {
        System.out.println("Running movie awards service...");
        Quarkus.run(args);
    }
}