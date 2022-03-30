package com.backbase.movie.service.resource;

import com.backbase.movie.service.domain.*;
import com.backbase.movie.service.store.MovieStore;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@QuarkusTest
class MovieResourceTest {

    @InjectMock
    MovieStore movieStore;

    @Test
    void testPostRating() {
        when(movieStore.findByTitle("test_movie")).thenReturn(Uni.createFrom().item(new Movie(0, "test_movie", "YES", 243245354)));
        when(movieStore.findByLogin("test_user")).thenReturn(Uni.createFrom().item(new Account(0, "test_user", "Test User")));
        when(movieStore.save(any(Rating.class))).thenReturn(Uni.createFrom().voidItem());

        final AddRatingRequest request = new AddRatingRequest("test_movie", "test_user", 6);

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/v1/movie/rating")
                .then()
                .statusCode(RestResponse.StatusCode.ACCEPTED);
    }

    @Test
    void testPostRatingForInvalidMovie() {
        when(movieStore.findByTitle("test_movie")).thenReturn(Uni.createFrom().nullItem());
        when(movieStore.findByLogin("test_user")).thenReturn(Uni.createFrom().item(new Account(0, "test_user", "Test User")));

        final AddRatingRequest request = new AddRatingRequest("test_movie", "test_user", 6);

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/v1/movie/rating")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testPostRatingForUnknownUser() {
        when(movieStore.findByTitle("test_movie")).thenReturn(Uni.createFrom().item(new Movie(0, "test_movie", "YES", 243245354)));
        when(movieStore.findByLogin("test_user")).thenReturn(Uni.createFrom().nullItem());
        when(movieStore.save(any(Rating.class))).thenReturn(Uni.createFrom().voidItem());

        final AddRatingRequest request = new AddRatingRequest("test_movie", "test_user", 6);

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/v1/movie/rating")
                .then()
                .statusCode(RestResponse.StatusCode.ACCEPTED);
    }

    @Test
    void testPostRatingNoMovie() {
        final AddRatingRequest request = new AddRatingRequest("null", "test_user", 6);

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/v1/movie/rating")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testGet() {
        when(movieStore.findByTitle("test_movie")).thenReturn(Uni.createFrom().item(new Movie(1, "test_movie", "YES", 243245354)));

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).get("/v1/movie/hasWonOscar?title=test_movie")
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }

    @Test
    void testGetNotFound() {
        when(movieStore.findByTitle("test_movie")).thenReturn(Uni.createFrom().nullItem());

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).get("/v1/movie/hasWonOscar?title=test_movie")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);
    }

    @Test
    void testGetTopTen() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1, "test_movie", "YES", 243245354));
        when(movieStore.findTopRated()).thenReturn(Uni.createFrom().item(movies));

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).get("/v1/movie/topTenRated")
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }

    @Test
    void testGetTopTenFail() {
        when(movieStore.findTopRated()).thenReturn(Uni.createFrom().nullItem());

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).get("/v1/movie/topTenRated")
                .then()
                .statusCode(RestResponse.StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testCreateAccount() {
        when(movieStore.save(any(Account.class))).thenReturn(Uni.createFrom().voidItem());

        final AddAccountRequest request = new AddAccountRequest("test_login", "test_user");

        given()
                .auth().basic("backbase", "backbase")
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body(request).post("/v1/movie/registerUser")
                .then()
                .statusCode(RestResponse.StatusCode.ACCEPTED);
    }

    @Test
    void testUnauthorizedAccess() {
        given()
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).get("/v1/movie/topTenRated")
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

}
