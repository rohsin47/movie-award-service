package com.backbase.movie.service.store;

import com.backbase.movie.service.domain.Account;
import com.backbase.movie.service.domain.Movie;
import com.backbase.movie.service.domain.Rating;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class MovieStoreTest {

    @InjectMock
    PgPool pgPool;

    @Inject
    MovieStore movieStore;

    @Test
    void testFindByTitle() {
        final Row row = Mockito.mock(Row.class);
        when(row.getInteger(0)).thenReturn(1);
        when(row.getString(1)).thenReturn("TEST MOVIE");
        when(row.getString(2)).thenReturn("YES");
        when(row.getLong(3)).thenReturn(34354366L);

        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.FIND_BY_TITLE_QUERY, Tuple.of("TEST MOVIE"), row);

        final Movie movie = movieStore.findByTitle("TEST MOVIE").await().indefinitely();
        assertEquals("TEST MOVIE", movie.getTitle());
    }

    @Test
    void testFindByTitleNonExisting() {
        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.FIND_BY_TITLE_QUERY, Tuple.of("TEST MOVIE"));

        final Movie movie = movieStore.findByTitle("TEST MOVIE").await().indefinitely();
        assertNull(movie);
    }

    @Test
    void testFindByLogin() {
        final Row row = Mockito.mock(Row.class);
        when(row.getInteger(0)).thenReturn(1);
        when(row.getString(1)).thenReturn("TEST_LOGIN");
        when(row.getString(2)).thenReturn("TEST LOGIN");

        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.FIND_BY_USER_QUERY, Tuple.of("TEST_LOGIN"), row);

        final Account account = movieStore.findByLogin("TEST_LOGIN").await().indefinitely();
        assertEquals("TEST_LOGIN", account.getLogin());
    }

    @Test
    void testSaveMovie() {
        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.SAVE_MOVIE_QUERY, Tuple.of("TEST MOVIE", "YES", 34354366L));
        movieStore.save(new Movie(1, "TEST MOVIE", "YES", 34354366L)).await().indefinitely();
    }

    @Test
    void testSaveAccount() {
        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.SAVE_USER_QUERY, Tuple.of(Tuple.of("TEST_LOGIN", "TEST LOGIN")));
        movieStore.save(new Account(1, "TEST_LOGIN", "TEST LOGIN")).await().indefinitely();
    }

    @Test
    void testSaveRating() {
        PgPoolHelper.configureToReturnRows(pgPool, MovieStore.SAVE_RATING_QUERY, Tuple.of(1, 2, 6));
        movieStore.save(new Rating(1, new Movie(1, "TEST MOVIE", "YES", 34354366L), new Account(1, "TEST_LOGIN", "TEST LOGIN"), 7)).await().indefinitely();
    }


}
