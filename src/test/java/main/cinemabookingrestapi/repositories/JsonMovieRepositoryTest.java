package main.cinemabookingrestapi.repositories;

import tools.jackson.databind.json.JsonMapper;
import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.repository.json.JsonMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JsonMovieRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonMovieRepository repository;

    @BeforeEach
    void setUp() {
        Path file = tempDir.resolve("temp_movies.json");
        repository = new JsonMovieRepository(new JsonMapper(), file.toString());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfMovies() {
        Movie movie1 = createMovie();
        Movie movie2 = new Movie(
                2L,
                "Interstellar",
                "Sci-Fi",
                169,
                12,
                new BigDecimal("280.00"),
                true
        );

        repository.addMovie(movie1);
        repository.addMovie(movie2);

        List<Movie> movies = repository.getAll();

        assertNotNull(movies);
        assertEquals(2, movies.size());
        assertEquals(1L, movies.get(0).getId());
        assertEquals(2L, movies.get(1).getId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenMovieIsNotFound() {
        Long incorrectId = 12L;

        Optional<Movie> movie = repository.getById(incorrectId);

        assertNotNull(movie);
        assertTrue(movie.isEmpty());
    }

    @Test
    void getById_shouldReturnOptionalOfMovie() {
        Movie movie = createMovie();
        repository.addMovie(movie);

        Optional<Movie> movieOptional = repository.getById(movie.getId());

        assertNotNull(movieOptional);
        assertTrue(movieOptional.isPresent());
        assertEquals(movie.getId(), movieOptional.get().getId());
        assertEquals(movie.getTitle(), movieOptional.get().getTitle());
    }

    @Test
    void addMovie_shouldGenerateIdAndAddEntityInFile() {
        Movie movie = createMovie();
        movie.setId(null);

        Movie addedMovie = repository.addMovie(movie);

        assertNotNull(addedMovie.getId());
        assertEquals(movie.getTitle(), addedMovie.getTitle());
        assertEquals(1, repository.getAll().size());

        Movie loadedMovie = repository.getById(addedMovie.getId()).orElseThrow();
        assertThat(loadedMovie).usingRecursiveComparison().isEqualTo(addedMovie);
    }

    @Test
    void updateMovie_shouldReturnEmptyOptional_whenMovieIsNotFound() {
        Long incorrectId = 12L;
        Movie movie = createMovie();

        Optional<Movie> movieOptional = repository.updateMovie(incorrectId, movie);

        assertNotNull(movieOptional);
        assertTrue(movieOptional.isEmpty());
    }

    @Test
    void updateMovie_shouldReturnOptionalOfUpdatedMovie() {
        Movie movie = createMovie();
        repository.addMovie(movie);
        Movie updatedMovie = new Movie(
                movie.getId(),
                "Avatar: The Way of Water",
                "Sci-Fi",
                192,
                12,
                new BigDecimal("350.00"),
                true
        );

        Optional<Movie> movieOptional = repository.updateMovie(movie.getId(), updatedMovie);

        assertNotNull(movieOptional);
        assertTrue(movieOptional.isPresent());
        assertEquals(updatedMovie.getId(), movieOptional.get().getId());
        assertEquals(updatedMovie.getTitle(), movieOptional.get().getTitle());

        Movie loadedMovie = repository.getById(movie.getId()).orElseThrow();
        assertEquals("Avatar: The Way of Water", loadedMovie.getTitle());
    }

    @Test
    void removeMovie_shouldReturnFalse_whenMovieIsNotPresent() {
        Long incorrectId = 12L;

        boolean result = repository.removeMovie(incorrectId);

        assertFalse(result);
    }

    @Test
    void removeMovie_shouldReturnTrue() {
        Movie movie = createMovie();
        repository.addMovie(movie);

        boolean result = repository.removeMovie(movie.getId());

        assertTrue(result);
    }

    private Movie createMovie() {
        return new Movie(
                1L,
                "Avatar",
                "Sci-Fi",
                162,
                12,
                new BigDecimal("300.00"),
                true
        );
    }
}
