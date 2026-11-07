package main.cinemabookingrestapi.repository;


import main.cinemabookingrestapi.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    List<Movie> getAll();

    Optional<Movie> getById(Long id);

    Movie addMovie(Movie movie);

    Optional<Movie> updateMovie(Long id, Movie updatedMovie);

    boolean removeMovie(Long id);
}
