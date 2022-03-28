package com.backbase.movie.service.resource;

import com.backbase.movie.service.domain.Account;
import com.backbase.movie.service.domain.AddRatingRequest;
import com.backbase.movie.service.domain.Movie;
import com.backbase.movie.service.domain.Rating;
import com.backbase.movie.service.error.ServiceError;
import com.backbase.movie.service.store.MovieStore;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

@Path("/v1/movie")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    private static final Logger log = LoggerFactory.getLogger(MovieResource.class);

    @Inject
    MovieStore movieStore;

    @GET
    @Path("/{title}")
    public Uni<Boolean> hasWonAward(@PathParam("title") String title) throws  ServiceError  {
        return movieStore.findByTitle(title)
                .onItem().ifNotNull().transform(movie -> "YES".equals(movie.getWon()))
                .onItem().ifNull().failWith(ServiceError.getInstance(Response.Status.NOT_FOUND, "Could not find movie %s", title));
    }

    @POST
    @Blocking
    public Response postRating(@Valid AddRatingRequest addRatingRequest) throws  ServiceError{
        if(addRatingRequest.getMovieTitle() == null ||
                addRatingRequest.getUserLogin() == null ||
                    addRatingRequest.getRating() == null){
            throw ServiceError.getInstance(Response.Status.NOT_FOUND, "Invalid request %s", addRatingRequest.toString());
        }

        Movie movie = movieStore.findByTitle(addRatingRequest.getMovieTitle()).await().atMost(Duration.ofSeconds(5));
        if(movie == null){
            throw ServiceError.getInstance(Response.Status.NOT_FOUND, "movie title does not exist %s", addRatingRequest.toString());
        }
        Account account = movieStore.findByLogin(addRatingRequest.getUserLogin()).await().atMost(Duration.ofSeconds(5));
        if(account == null){
            account = movieStore.findByLogin("anonymous").await().atMost(Duration.ofSeconds(1));
        }
        return movieStore.save(new Rating(0, movie, account, addRatingRequest.getRating())).await().atMost(Duration.ofSeconds(5));
    }

}
