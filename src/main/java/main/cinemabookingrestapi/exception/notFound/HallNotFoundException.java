package main.cinemabookingrestapi.exception.notFound;

public class HallNotFoundException extends NotFoundException {
    public HallNotFoundException(Long id) {
        super("hall not found with id: " + id);
    }
}
