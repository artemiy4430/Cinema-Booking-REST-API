package main.cinemabookingrestapi.controllers;

import main.cinemabookingrestapi.controller.MovieController;
import main.cinemabookingrestapi.dto.movie.CreateMovieRequest;
import main.cinemabookingrestapi.dto.movie.MovieFilterRequest;
import main.cinemabookingrestapi.dto.movie.MovieResponse;
import main.cinemabookingrestapi.dto.movie.UpdateMovieRequest;
import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.exception.alreadyExist.MovieAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.MovieNotFoundException;
import main.cinemabookingrestapi.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Test
    void getMovies_shouldReturnOkAndAllMovies_whenFiltersAreNull() {
        MovieFilterRequest expectedFilter = new MovieFilterRequest(null, null, null, null, null);
        List<MovieResponse> expectedResponses = createMovieResponseList();

        when(movieService.getFilteredMovies(expectedFilter)).thenReturn(expectedResponses);

        ResponseEntity<List<MovieResponse>> response =
                movieController.getMovies(null, null, null, null, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expectedResponses);
        assertEquals(2, response.getBody().size());

        verify(movieService).getFilteredMovies(expectedFilter);
    }

    @Test
    void getMovies_shouldReturnOkAndFilteredMovies_whenFiltersAreProvided() {
        MovieFilterRequest expectedFilter = new MovieFilterRequest(
                "Sci-Fi", true, BigDecimal.valueOf(200), BigDecimal.valueOf(350), "Avatar"
        );
        List<MovieResponse> expectedResponses = List.of(
                new MovieResponse("Avatar", "Sci-Fi", 162, 12, BigDecimal.valueOf(300))
        );

        when(movieService.getFilteredMovies(expectedFilter)).thenReturn(expectedResponses);

        ResponseEntity<List<MovieResponse>> response = movieController.getMovies(
                "Sci-Fi", true, BigDecimal.valueOf(200), BigDecimal.valueOf(350), "Avatar"
        );

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expectedResponses);
        assertEquals(1, response.getBody().size());
        assertEquals("Avatar", response.getBody().get(0).getTitle());

        verify(movieService).getFilteredMovies(expectedFilter);
    }

    @Test
    void getMovies_shouldReturnOkAndEmptyList_whenNoMoviesMatch() {
        MovieFilterRequest expectedFilter = new MovieFilterRequest("Horror", null, null, null, null);

        when(movieService.getFilteredMovies(expectedFilter)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MovieResponse>> response =
                movieController.getMovies("Horror", null, null, null, null);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(movieService).getFilteredMovies(expectedFilter);
    }

    @Test
    void getById_shouldReturnOk_whenMovieExists() {
        Movie movie = createMovie();

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));

        ResponseEntity<Movie> response = movieController.getById(movie.getId());

        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(movie);

        verify(movieService).getById(movie.getId());
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenMovieDoesNotExist() {
        Long incorrectId = 99L;

        when(movieService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Movie> response = movieController.getById(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(movieService).getById(incorrectId);
    }

    @Test
    void addMovie_shouldReturnStatusCreatedAndMovieResponse_whenRequestIsValid() {
        CreateMovieRequest request = new CreateMovieRequest(
                "Inception", "Sci-Fi", 148, 12, BigDecimal.valueOf(280)
        );
        MovieResponse expectedResponse = new MovieResponse(
                "Inception", "Sci-Fi", 148, 12, BigDecimal.valueOf(280)
        );

        when(movieService.addMovie(request)).thenReturn(expectedResponse);

        ResponseEntity<MovieResponse> response = movieController.addMovie(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(movieService).addMovie(request);
    }

    @Test
    void addMovie_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        when(movieService.addMovie(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> movieController.addMovie(null));

        verify(movieService).addMovie(null);
    }

    @Test
    void addMovie_shouldThrowMovieAlreadyExistsException_whenTitleExists() {
        CreateMovieRequest request = new CreateMovieRequest(
                "Avatar", "Sci-Fi", 162, 12, BigDecimal.valueOf(300)
        );

        when(movieService.addMovie(request)).thenThrow(new MovieAlreadyExistsException());

        assertThrows(MovieAlreadyExistsException.class, () -> movieController.addMovie(request));

        verify(movieService).addMovie(request);
    }

    @Test
    void updateMovie_shouldReturnStatusAcceptedAndMovieResponse_whenRequestIsValid() {
        Long movieId = 1L;
        UpdateMovieRequest request = new UpdateMovieRequest(
                "Avatar 2", "Sci-Fi", 192, 12, BigDecimal.valueOf(350)
        );
        MovieResponse expectedResponse = new MovieResponse(
                "Avatar 2", "Sci-Fi", 192, 12, BigDecimal.valueOf(350)
        );

        when(movieService.updateMovie(movieId, request)).thenReturn(expectedResponse);

        ResponseEntity<MovieResponse> response = movieController.updateMovie(movieId, request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(movieService).updateMovie(movieId, request);
    }

    @Test
    void updateMovie_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        Long movieId = 1L;

        when(movieService.updateMovie(movieId, null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> movieController.updateMovie(movieId, null));

        verify(movieService).updateMovie(movieId, null);
    }

    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        Long incorrectId = 99L;
        UpdateMovieRequest request = new UpdateMovieRequest(
                "Avatar 2", "Sci-Fi", 192, 12, BigDecimal.valueOf(350)
        );

        when(movieService.updateMovie(incorrectId, request))
                .thenThrow(new MovieNotFoundException(incorrectId));

        assertThrows(MovieNotFoundException.class, () -> movieController.updateMovie(incorrectId, request));

        verify(movieService).updateMovie(incorrectId, request);
    }

    @Test
    void removeMovie_shouldReturnNoContent_whenMovieExists() {
        Long movieId = 1L;

        ResponseEntity<Void> response = movieController.removeMovie(movieId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(movieService).removeMovie(movieId);
    }

    @Test
    void removeMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        Long incorrectId = 99L;

        doThrow(new MovieNotFoundException(incorrectId)).when(movieService).removeMovie(incorrectId);

        assertThrows(MovieNotFoundException.class, () -> movieController.removeMovie(incorrectId));

        verify(movieService).removeMovie(incorrectId);
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

    private List<MovieResponse> createMovieResponseList() {
        return List.of(
                new MovieResponse("Avatar", "Sci-Fi", 162, 12, BigDecimal.valueOf(300)),
                new MovieResponse("The Dark Knight", "Action", 152, 16, BigDecimal.valueOf(250))
        );
    }
}