package com.backbase.movie.service.csv;

import com.backbase.movie.service.domain.Account;
import com.backbase.movie.service.domain.Movie;
import com.backbase.movie.service.domain.Rating;
import com.backbase.movie.service.store.MovieStore;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
class CSVProcessorTest {

    @Inject
    CSVProcessor csvProcessor;

    @InjectMock
    MovieStore movieStore;


    @Test
    void testPersist() throws FileNotFoundException {
        doNothing().when(movieStore).save(any(List.class));
        when(movieStore.findAll()).thenReturn(Uni.createFrom().item(Collections.emptyList()));
        when(movieStore.save(any(Account.class))).thenReturn(Uni.createFrom().voidItem());

        csvProcessor.persistData(new StartupEvent());
    }


}
