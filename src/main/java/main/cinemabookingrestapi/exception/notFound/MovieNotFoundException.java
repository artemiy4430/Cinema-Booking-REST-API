package main.cinemabookingrestapi.exception.notFound;

public class MovieNotFoundException extends NotFoundException {
    public MovieNotFoundException(Long id) {
        super("Movie not found with id: " + id);
    }
}
