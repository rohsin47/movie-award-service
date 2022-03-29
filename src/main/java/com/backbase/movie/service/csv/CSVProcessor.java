package com.backbase.movie.service.csv;

import com.backbase.movie.service.domain.Account;
import com.backbase.movie.service.domain.Movie;
import com.backbase.movie.service.domain.MovieInfo;
import com.backbase.movie.service.store.MovieStore;
import com.opencsv.bean.CsvToBeanBuilder;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@ApplicationScoped
public class CSVProcessor {

    private static final Logger log = LoggerFactory.getLogger(CSVProcessor.class);

    @ConfigProperty(name = "BB_MOVIE_DATA_PROCESSED")
    boolean isCSVProcessed;

    @Inject
    MovieStore movieStore;

    public void persistData(@Observes StartupEvent sv) throws FileNotFoundException {
        if(!isCSVProcessed) {
            List<MovieInfo> movieInfos = new CsvToBeanBuilder(new FileReader(getPath("academy_awards.csv")))
                    .withType(MovieInfo.class)
                    .build()
                    .parse();

            log.info("movies info : {}", movieInfos.size());

            List<Movie> moviesWithAwards = movieInfos.stream()
                    .filter(info -> "Best Picture".equals(info.getCategory()))
                    .map(info -> new Movie(0, info.getNominee(), info.getWon(), ThreadLocalRandom.current().nextInt(1000000, 2000000)))
                    .collect(Collectors.toList());

            log.info("movies with awards : {}", moviesWithAwards.size());

            movieStore.save(moviesWithAwards);
            movieStore.save(new Account(0, "guest", "John Doe"));
        } else {
            log.info("movie data has already been processed");
        }
    }

    public String getPath(String fileName) {
        try {
            return Paths.get(Objects.requireNonNull(
                    Thread.currentThread().getContextClassLoader().getResource("/data/" + fileName)).toURI())
                    .toFile()
                    .getPath();
        } catch (URISyntaxException var3) {
            log.error("Unable to get path for {} for kafka cluster", fileName, var3);
            return "";
        }
    }
}
