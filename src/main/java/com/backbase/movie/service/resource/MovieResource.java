package com.backbase.movie.service.resource;

import com.backbase.movie.service.domain.*;
import com.backbase.movie.service.error.ServiceError;
import com.backbase.movie.service.store.MovieStore;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Path("/v1/movie")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    private static final Logger log = LoggerFactory.getLogger(MovieResource.class);

    @Inject
    MovieStore movieStore;

    @GET
    @RolesAllowed("backbase")
    @Path("/hasWonOscar")
    public Uni<Boolean> hasWonAward(@QueryParam("title") String title) throws ServiceError {
        return movieStore.findByTitle(title)
                .onItem().ifNotNull().transform(movie -> "YES".equals(movie.getWon()))
                .onItem().ifNull().failWith(ServiceError.getInstance(Response.Status.NOT_FOUND, "Could not find movie %s", title));
    }

    @POST
    @Path("/rating")
    @RolesAllowed("backbase")
    @Blocking
    public Response postRating(@Valid AddRatingRequest addRatingRequest) throws ServiceError {
        Movie movie = movieStore.findByTitle(addRatingRequest.getMovieTitle()).await().atMost(Duration.ofSeconds(5));
        if (movie == null) {
            throw ServiceError.getInstance(Response.Status.NOT_FOUND, "movie title does not exist %s", addRatingRequest.toString());
        }
        Account account = movieStore.findByLogin(addRatingRequest.getUserLogin()).await().atMost(Duration.ofSeconds(5));
        if (account == null) {
            account = movieStore.findByLogin("anonymous").await().atMost(Duration.ofSeconds(1));
        }
        return movieStore.save(new Rating(0, movie, account, addRatingRequest.getRating())).await().atMost(Duration.ofSeconds(5));
    }

    @GET
    @RolesAllowed("backbase")
    @Path("/topTenRated")
    @JacksonFeatures(serializationEnable = {SerializationFeature.INDENT_OUTPUT})
    public Uni<List<Movie>> getTopTen() throws ServiceError {
        return movieStore.findTopRated()
                .onItem().ifNotNull().transform(movieList -> {
                    movieList.sort(Comparator.comparing(Movie::getBoxOffice).reversed());
                    return movieList;
                }).onItem().ifNull().failWith(ServiceError.getInstance(Response.Status.INTERNAL_SERVER_ERROR, "error in processing"));
    }

    @POST
    @RolesAllowed("backbase")
    @Path("/registerUser")
    public Response createAccount(@Valid AddAccountRequest addAccountRequest) throws ServiceError {
        movieStore.save(new Account(0, addAccountRequest.getLogin(), addAccountRequest.getName()));
        return Response.accepted().build();
    }

}
