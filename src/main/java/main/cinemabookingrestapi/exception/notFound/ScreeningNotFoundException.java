package main.cinemabookingrestapi.exception.notFound;

public class ScreeningNotFoundException extends NotFoundException {
    public ScreeningNotFoundException(Long id) {
        super("Screening not found with id:" + id);
    }
}
