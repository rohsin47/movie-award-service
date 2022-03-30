package com.backbase.movie.service.store;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.*;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

public class PgPoolHelper {
    public static SqlConnection configureTransaction(PgPool pgPoolMock) {
        final SqlConnection sqlConnection = Mockito.mock(SqlConnection.class);
        Mockito.when(pgPoolMock.withTransaction(Mockito.any())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Function<SqlConnection, Uni<Object>> function = (Function<SqlConnection, Uni<Object>>) args[0];
            return function.apply(sqlConnection);
        });
        return sqlConnection;
    }

    public static void configureToReturnRows(SqlConnection sqlConnectionMock, String queryString, Row... rows) {
        final Uni<RowSet<Row>> rowSetUni = createUniFromRows(rows);

        final Query<RowSet<Row>> query = Mockito.mock(Query.class);
        Mockito.when(query.execute()).thenReturn(rowSetUni);

        Mockito.when(sqlConnectionMock.query(Mockito.eq(queryString))).thenReturn(query);
    }

    public static void configureToReturnRows(SqlConnection sqlConnectionMock, String queryString, Tuple tuple, Row... rows) {
        final Uni<RowSet<Row>> rowSetUni = createUniFromRows(rows);

        final PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
        Mockito.when(preparedQuery.execute(Mockito.argThat(new TupleMatcher(tuple)))).thenReturn(rowSetUni);

        Mockito.when(sqlConnectionMock.preparedQuery(Mockito.eq(queryString))).thenReturn(preparedQuery);
    }

    public static void configureToReturnRows(PgPool pgPoolMock, String queryString, Row... rows) {
        final Uni<RowSet<Row>> rowSetUni = createUniFromRows(rows);

        final Query<RowSet<Row>> query = Mockito.mock(Query.class);
        Mockito.when(query.execute()).thenReturn(rowSetUni);

        Mockito.when(pgPoolMock.query(Mockito.eq(queryString))).thenReturn(query);
    }

    public static void configureToReturnRows(PgPool pgPoolMock, String queryString, Tuple tuple, Row... rows) {
        final Uni<RowSet<Row>> rowSetUni = createUniFromRows(rows);

        final PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
        Mockito.when(preparedQuery.execute(Mockito.argThat(new TupleMatcher(tuple)))).thenReturn(rowSetUni);

        Mockito.when(pgPoolMock.preparedQuery(Mockito.eq(queryString))).thenReturn(preparedQuery);
    }

    public static Uni<RowSet<Row>> createUniFromRows(Row[] rows) {
        final RowIterator<Row> rowIterator = Mockito.mock(RowIterator.class);
        LinkedList<Row> rowsList = new LinkedList<>(Arrays.asList(rows));
        Mockito.when(rowIterator.hasNext()).thenAnswer(invocation -> !rowsList.isEmpty());
        Mockito.when(rowIterator.next()).thenAnswer(invocation -> rowsList.removeFirst());

        final RowSet<Row> rowSet = Mockito.mock(RowSet.class);
        Mockito.when(rowSet.iterator()).thenReturn(rowIterator);

        final Uni<RowSet<Row>> rowSetUni = Uni.createFrom().item(rowSet);
        return rowSetUni;
    }

    public static void configureToThrowException(PgPool pgPoolMock, String query, Tuple tuple, Throwable t) {
        final Uni<RowSet<Row>> rowSetUni = Uni.createFrom().failure(t);

        final PreparedQuery<RowSet<Row>> preparedQuery = Mockito.mock(PreparedQuery.class);
        Mockito.when(preparedQuery.execute(Mockito.argThat(new TupleMatcher(tuple)))).thenReturn(rowSetUni);

        Mockito.when(pgPoolMock.preparedQuery(Mockito.eq(query))).thenReturn(preparedQuery);
    }

    private static class TupleMatcher implements ArgumentMatcher<Tuple> {
        private final Tuple expected;

        public TupleMatcher(Tuple expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Tuple other) {
            return Objects.equals(other.deepToString(), expected.deepToString());
        }
    }
}
