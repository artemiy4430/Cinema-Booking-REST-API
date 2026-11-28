package main.cinemabookingrestapi.service;

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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    public MovieService(MovieRepository movieRepository, ScreeningRepository screeningRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
    }

    public List<Movie> getAll() {
        return movieRepository.getAll();
    }

    public Optional<Movie> getById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        return movieRepository.getById(id);
    }

    public MovieResponse addMovie(CreateMovieRequest request) {
        if (request == null) throw new IllegalArgumentException();

        List<Movie> movies = movieRepository.getAll();

        boolean isPresent = movies.stream()
                .anyMatch(m -> Objects.equals(m.getTitle(), request.getTitle()));

        if (isPresent) throw new MovieAlreadyExistsException();

        Movie movie = request.toEntity();

        movie.setActive(true);

        Movie savedMovie = movieRepository.addMovie(movie);

        if (savedMovie == null) throw new IllegalArgumentException();

        return MovieResponse.fromEntity(savedMovie);
    }

    public MovieResponse updateMovie(Long id, UpdateMovieRequest request) {
        if (request == null) throw new IllegalArgumentException();

        Movie movie = movieRepository.getById(id).orElseThrow(() -> new MovieNotFoundException(id));

        Movie updatedMovie = movieRepository.updateMovie(id, request.updateMovie(movie))
                .orElseThrow(() -> new MovieNotFoundException(id));

        return MovieResponse.fromEntity(updatedMovie);
    }

    public void removeMovie(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        Movie movie = movieRepository.getById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        boolean isAnyScreeningContainsMovie = screeningRepository.getAll()
                .stream()
                .anyMatch(s -> Objects.equals(s.getMovieId(), id));

        if (isAnyScreeningContainsMovie) throw new MovieIsPresentInScreeningException(id);

        movieRepository.removeMovie(id);
    }

    public List<MovieResponse> getFilteredMovies(MovieFilterRequest request) {
        if (request == null) throw new IllegalArgumentException();
        List<Movie> movies = getAll();

        if (request.getGenre().isPresent()
                || request.getActive().isPresent()
                || request.getMaxPrice().isPresent()
                || request.getMinPrice().isPresent()
                || request.getTitle().isPresent()) {
            movies = movies.stream().filter(m -> request.getGenre().isEmpty()
                            || Objects.equals(m.getGenre(), request.getGenre().get()))
                    .filter(m -> request.getActive().isEmpty() ||
                            m.isActive() == request.getActive().get())
                    .filter(m -> request.getMinPrice().isEmpty() ||
                            m.getBasePrice().compareTo(request.getMinPrice().get()) >= 0)
                    .filter(m -> request.getMaxPrice().isEmpty() ||
                            m.getBasePrice().compareTo(request.getMaxPrice().get()) <= 0)
                    .filter(m -> request.getTitle().isEmpty() ||
                            Objects.equals(m.getTitle(), request.getTitle().get())).toList();
        }

        return movies.stream().map(MovieResponse::fromEntity).toList();
    }
}
