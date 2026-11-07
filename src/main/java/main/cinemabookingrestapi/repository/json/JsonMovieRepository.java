package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonMovieRepository extends JsonRepository<Movie> implements MovieRepository {

    public JsonMovieRepository(JsonMapper jsonMapper, @Value("./data/movies.json") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Movie>>() {});
    }

    @Override
    public synchronized List<Movie> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Movie> getById(Long id) {
        return readEntities().stream().filter(m -> Objects.equals(m.getId(), id)).findFirst();
    }

    @Override
    public synchronized Movie addMovie(Movie movie) {
        List<Movie> movies = readEntities();

        if (movies.contains(movie)) {
            return null;
        }
        movie.setId(generateNewId(movies));
        movies.add(movie);
        writeEntities(movies);

        return movie;
    }

    @Override
    public synchronized Optional<Movie> updateMovie(Long id, Movie updatedMovie) {
        List<Movie> movies = readEntities();
        Optional<Movie> movie = movies.stream().filter(m -> Objects.equals(m.getId(), id)).findFirst();

        if (movie.isEmpty()) {
            return Optional.empty();
        }

        movies.remove(movie.get());
        movies.add(updatedMovie);
        writeEntities(movies);

        return Optional.of(updatedMovie);
    }

    @Override
    public synchronized boolean removeMovie(Long id) {
        List<Movie> movies = readEntities();
        Optional<Movie> movie = movies.stream().filter(m -> Objects.equals(m.getId(), id)).findFirst();

        if (movie.isEmpty()) {
            return false;
        }

        movies.remove(movie.get());
        writeEntities(movies);

        return true;
    }
}
