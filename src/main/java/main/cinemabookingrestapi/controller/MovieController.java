package main.cinemabookingrestapi.controller;


import main.cinemabookingrestapi.dto.movie.CreateMovieRequest;
import main.cinemabookingrestapi.dto.movie.MovieFilterRequest;
import main.cinemabookingrestapi.dto.movie.MovieResponse;
import main.cinemabookingrestapi.dto.movie.UpdateMovieRequest;
import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getMovies(@RequestBody(required = false) String genre,
                                                         @RequestBody(required = false) Boolean isActive,
                                                         @RequestBody(required = false) BigDecimal minPrice,
                                                         @RequestBody(required = false) BigDecimal maxPrice,
                                                         @RequestBody(required = false) String title
    ) {
        MovieFilterRequest request = new MovieFilterRequest(genre, isActive, minPrice, maxPrice, title);

        List<MovieResponse> filterList = movieService.getFilteredMovies(request);

        return ResponseEntity.status(HttpStatus.OK).body(filterList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id) {
        return movieService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovieResponse> addMovie(@RequestBody CreateMovieRequest request) {
        MovieResponse response = movieService.addMovie(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @RequestBody UpdateMovieRequest request) {
        MovieResponse response = movieService.updateMovie(id, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMovie(@PathVariable Long id) {
        movieService.removeMovie(id);

        return ResponseEntity.noContent().build();
    }
}
