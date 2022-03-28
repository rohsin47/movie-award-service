package com.backbase.movie.service.store;

import com.backbase.movie.service.domain.Account;
import com.backbase.movie.service.domain.Movie;
import com.backbase.movie.service.domain.Rating;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieStore {

    public static final String ID_COLUMN = "id";
    public static final String MOVIE_TITLE_COLUMN = "title";
    public static final String MOVIE_WON_COLUMN = "won";
    public static final String MOVIE_BOX_OFFICE_COLUMN = "box_office";
    public static final String ACCOUNT_LOGIN_COLUMN = "login";
    public static final String ACCOUNT_NAME_COLUMN = "name";
    private static final String RATING_MOVIE_ID = "movie_id";
    private static final String RATING_USER_ID = "user_id";
    private static final String RATING = "rating";

    public static final String FIND_BY_TITLE_QUERY = "SELECT " + ID_COLUMN + "," + MOVIE_TITLE_COLUMN + "," + MOVIE_WON_COLUMN + "," + MOVIE_BOX_OFFICE_COLUMN + " FROM movie WHERE title = $1";
    public static final String FIND_BY_USER_QUERY = "SELECT " + ID_COLUMN + "," + ACCOUNT_LOGIN_COLUMN + " FROM account WHERE login = $1";
    public static final String SAVE_RATING_QUERY = "INSERT INTO rating (" + RATING_MOVIE_ID + "," + RATING_USER_ID + "," + RATING + ") VALUES ($1,$2,$3) ";
    public static final String SAVE_MOVIE_QUERY = "INSERT INTO movie (" + MOVIE_TITLE_COLUMN + "," + MOVIE_WON_COLUMN + "," + MOVIE_BOX_OFFICE_COLUMN + ") VALUES ($1,$2,$3) ON CONFLICT (title) DO UPDATE SET title = movie.title, won = movie.won, box_office = movie.box_office;";
    public static final String SAVE_USER_QUERY = "INSERT INTO account (" + ACCOUNT_LOGIN_COLUMN + "," + ACCOUNT_NAME_COLUMN +") VALUES ($1,$2) ";

    @Inject
    PgPool pgPool;

    public Uni<Movie> findByTitle(String title) {
        return pgPool.preparedQuery(FIND_BY_TITLE_QUERY)
                .execute(Tuple.of(title))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromMovie(iterator.next()) : null);
    }

    public Uni<Account> findByLogin(String login) {
        return pgPool.preparedQuery(FIND_BY_USER_QUERY)
                .execute(Tuple.of(login))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? fromUser(iterator.next()) : null);
    }

    public Uni<Void> save(Movie movie) {
        return pgPool.preparedQuery(SAVE_MOVIE_QUERY)
                .execute(Tuple.of(movie.getTitle(), movie.getWon(), movie.getBoxOffice()))
                .replaceWithVoid();
    }

    public void save(Account account) {
        pgPool.preparedQuery(SAVE_USER_QUERY)
                .execute(Tuple.of(account.getLogin(), account.getName()))
                .ifNoItem().after(Duration.ofSeconds(30)).fail()
                .await().indefinitely();
    }

    public void save(List<Movie> movies) {
        List<Tuple> batch = movies.stream().map(movie -> Tuple.of(movie.getTitle(), movie.getWon(), movie.getBoxOffice())).collect(Collectors.toList());
        pgPool.preparedQuery(SAVE_MOVIE_QUERY)
                .executeBatch(batch)
                .ifNoItem().after(Duration.ofSeconds(30)).fail()
                .await().indefinitely();
    }

    public Uni<Response> save(Rating rating) {
        return pgPool.preparedQuery(SAVE_RATING_QUERY)
                .execute(Tuple.of(rating.getMovie().getId(), rating.getUser().getId(), rating.getRating()))
                .onItem()
                .transform(inserted -> Response.accepted().build());
    }

    private Movie fromMovie(Row row) {
        return new Movie(row.getInteger(0), row.getString(1), row.getString(2), row.getLong(3));
    }

    private Account fromUser(Row row) {
        return new Account(row.getInteger(0), row.getString(1), row.getString(2));
    }


}
