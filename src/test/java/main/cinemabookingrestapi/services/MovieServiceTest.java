package main.cinemabookingrestapi.services;

import main.cinemabookingrestapi.dto.movie.CreateMovieRequest;
import main.cinemabookingrestapi.dto.movie.MovieFilterRequest;
import main.cinemabookingrestapi.dto.movie.MovieResponse;
import main.cinemabookingrestapi.dto.movie.UpdateMovieRequest;
import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.exception.alreadyExist.MovieAlreadyExistsException;
import main.cinemabookingrestapi.exception.isPresentException.MovieIsPresentInScreeningException;
import main.cinemabookingrestapi.exception.notFound.MovieNotFoundException;
import main.cinemabookingrestapi.repository.MovieRepository;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import main.cinemabookingrestapi.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void getAll_shouldReturnEmptyList() {
        when(movieRepository.getAll()).thenReturn(Collections.emptyList());

        List<Movie> movies = movieService.getAll();

        assertNotNull(movies);
        assertTrue(movies.isEmpty());

        verify(movieRepository).getAll();
    }

    @Test
    void getAll_shouldReturnListOfMovies() {
        List<Movie> movieList = createList();

        when(movieRepository.getAll()).thenReturn(movieList);

        List<Movie> movies = movieService.getAll();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(3, movies.size());
        assertEquals("Avatar", movies.get(0).getTitle());

        verify(movieRepository).getAll();
    }

    @Test
    void getById_shouldReturnMovie() {
        Movie movie = createMovie();

        when(movieRepository.getById(1L)).thenReturn(Optional.of(movie));

        Optional<Movie> result = movieService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Avatar", result.get().getTitle());

        verify(movieRepository).getById(1L);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> movieService.getById(0L));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> movieService.getById(null));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenMovieNotFound() {
        when(movieRepository.getById(99L)).thenReturn(Optional.empty());

        Optional<Movie> result = movieService.getById(99L);

        assertTrue(result.isEmpty());

        verify(movieRepository).getById(99L);
    }

    @Test
    void addMovie_shouldReturnMovieResponse() {
        CreateMovieRequest request = new CreateMovieRequest("Inception", "Sci-Fi", 148, 12, BigDecimal.valueOf(280));
        Movie savedMovie = new Movie(4L, "Inception", "Sci-Fi", 148, 12, BigDecimal.valueOf(280), true);

        when(movieRepository.getAll()).thenReturn(createList());
        when(movieRepository.addMovie(any(Movie.class))).thenReturn(savedMovie);

        MovieResponse response = movieService.addMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        assertEquals("Sci-Fi", response.getGenre());

        verify(movieRepository).getAll();
        verify(movieRepository).addMovie(any(Movie.class));
    }

    @Test
    void addMovie_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> movieService.addMovie(null));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void addMovie_shouldThrowMovieAlreadyExistsException_whenTitleExists() {
        CreateMovieRequest request = new CreateMovieRequest("Avatar", "Sci-Fi", 162, 12, BigDecimal.valueOf(300));

        when(movieRepository.getAll()).thenReturn(createList());

        assertThrows(MovieAlreadyExistsException.class, () -> movieService.addMovie(request));

        verify(movieRepository).getAll();
        verify(movieRepository, never()).addMovie(any(Movie.class));
    }

    @Test
    void addMovie_shouldThrowIllegalArgumentException_whenRepositoryReturnsNull() {
        CreateMovieRequest request = new CreateMovieRequest("Inception", "Sci-Fi", 148, 12, BigDecimal.valueOf(280));

        when(movieRepository.getAll()).thenReturn(createList());
        when(movieRepository.addMovie(any(Movie.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> movieService.addMovie(request));

        verify(movieRepository).getAll();
        verify(movieRepository).addMovie(any(Movie.class));
    }

    @Test
    void updateMovie_shouldReturnUpdatedMovieResponse() {
        Movie existingMovie = createMovie();
        UpdateMovieRequest request = new UpdateMovieRequest("Avatar 2", "Sci-Fi", 192, 12, BigDecimal.valueOf(350));
        Movie updatedMovie = new Movie(1L, "Avatar 2", "Sci-Fi", 192, 12, BigDecimal.valueOf(350), true);

        when(movieRepository.getById(1L)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.updateMovie(eq(1L), any(Movie.class))).thenReturn(Optional.of(updatedMovie));

        MovieResponse response = movieService.updateMovie(1L, request);

        assertNotNull(response);
        assertEquals("Avatar 2", response.getTitle());
        assertEquals(BigDecimal.valueOf(350), response.getBasePrice());

        verify(movieRepository).getById(1L);
        verify(movieRepository).updateMovie(eq(1L), any(Movie.class));
    }

    @Test
    void updateMovie_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> movieService.updateMovie(1L, null));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenMovieNotFound() {
        UpdateMovieRequest request = new UpdateMovieRequest("Avatar 2", "Sci-Fi",
                192, 12, BigDecimal.valueOf(350));

        when(movieRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(99L, request));

        verify(movieRepository).getById(99L);
        verify(movieRepository, never()).updateMovie(anyLong(), any(Movie.class));
    }

    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenRepositoryReturnsEmpty() {
        Movie existingMovie = createMovie();
        UpdateMovieRequest request = new UpdateMovieRequest("Avatar 2", "Sci-Fi", 192,
                12, BigDecimal.valueOf(350));

        when(movieRepository.getById(1L)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.updateMovie(eq(1L), any(Movie.class))).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(1L, request));

        verify(movieRepository).getById(1L);
        verify(movieRepository).updateMovie(eq(1L), any(Movie.class));
    }

    @Test
    void removeMovie_shouldRemoveMovie() {
        Movie movie = createMovie();
        movie.setId(12L);

        when(movieRepository.getById(12L)).thenReturn(Optional.of(movie));
        when(screeningRepository.getAll()).thenReturn(createScreeningList());

        movieService.removeMovie(12L);

        verify(movieRepository).getById(12L);
        verify(screeningRepository).getAll();
        verify(movieRepository).removeMovie(12L);
    }

    @Test
    void removeMovie_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> movieService.removeMovie(null));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void removeMovie_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> movieService.removeMovie(0L));

        verifyNoInteractions(movieRepository);
    }

    @Test
    void removeMovie_shouldThrowMovieNotFoundException_whenMovieNotFound() {
        when(movieRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.removeMovie(99L));

        verify(movieRepository).getById(99L);
        verify(movieRepository, never()).removeMovie(anyLong());
    }

    @Test
    void removeMovie_shouldThrowMovieIsPresentInScreeningException_whenGivenMovieIsBeingContainedInExistingScreening() {
        Movie movie = createMovie();

        when(movieRepository.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(screeningRepository.getAll()).thenReturn(createScreeningList());

       assertThrows(MovieIsPresentInScreeningException.class, () -> movieService.removeMovie(movie.getId()));

        verify(movieRepository).getById(movie.getId());
        verify(screeningRepository).getAll();
    }


    @Test
    void getFilteredMovies_shouldReturnAllMovies_whenFilterIsEmpty() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest(null,
                null, null, null, null);

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals("Avatar", response.get(0).getTitle());
        assertEquals("Action", response.get(1).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldFilterByGenre_whenGenreIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest("Sci-Fi",
                null, null, null, null);

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Sci-Fi", response.get(0).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> movieService.getFilteredMovies(null));
    }

    @Test
    void getFilteredMovies_shouldFilterByActive_whenActiveIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest(null,
                false, null, null, null);

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Drama", response.get(0).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldFilterByMinPrice_whenMinPriceIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest(null,
                null, BigDecimal.valueOf(300), null, null);

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Sci-Fi", response.get(0).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldFilterByMaxPrice_whenMaxPriceIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest(null,
                null, null, BigDecimal.valueOf(200), null);

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Drama", response.get(0).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldFilterByTitle_whenTitleIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest(null,
                null, null, null, "Forrest Gump");

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Drama", response.get(0).getGenre());

        verify(movieRepository).getAll();
    }

    @Test
    void getFilteredMovies_shouldFilterByTitleAndGenre_whenTitleAndGenreIsProvided() {
        List<Movie> movies = createList();

        MovieFilterRequest request = new MovieFilterRequest("Drama",
                null, null, null, "Forrest Gump");

        when(movieRepository.getAll()).thenReturn(movies);

        List<MovieResponse> response = movieService.getFilteredMovies(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Drama", response.get(0).getGenre());
        assertEquals("Forrest Gump", response.get(0).getTitle());

        verify(movieRepository).getAll();
    }

    private Movie createMovie() {
        return new Movie(
                1L,
                "Avatar",
                "Sci-Fi",
                162,
                12,
                BigDecimal.valueOf(300),
                true
        );
    }

    private List<Movie> createList() {
        return List.of(
                new Movie(
                        1L,
                        "Avatar",
                        "Sci-Fi",
                        162,
                        12,
                        BigDecimal.valueOf(300),
                        true
                ),
                new Movie(
                        2L,
                        "The Dark Knight",
                        "Action",
                        152,
                        16,
                        BigDecimal.valueOf(250),
                        true
                ),
                new Movie(
                        5L,
                        "Forrest Gump",
                        "Drama",
                        142,
                        16,
                        BigDecimal.valueOf(200),
                        false
                )
        );
    }

    private List<Screening> createScreeningList() {
        return List.of(
                new Screening(
                        1L,
                        1L,
                        1L,
                        LocalDateTime.of(2026, 11, 26, 14, 0),
                        LocalDateTime.of(2026, 11, 26, 16, 42),
                        new BigDecimal("300.00"),
                        true
                ),
                new Screening(
                        2L,
                        2L,
                        2L,
                        LocalDateTime.of(2026, 11, 16, 18, 0),
                        LocalDateTime.of(2026, 11, 16, 20, 32),
                        new BigDecimal("250.00"),
                        true
                ),
                new Screening(
                        3L,
                        3L,
                        3L,
                        LocalDateTime.of(2026, 11, 8, 12, 0),
                        LocalDateTime.of(2026, 11, 8, 14, 49),
                        new BigDecimal("280.00"),
                        false
                )
        );
    }

}