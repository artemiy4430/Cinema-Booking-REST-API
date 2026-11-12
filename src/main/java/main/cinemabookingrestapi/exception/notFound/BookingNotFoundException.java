package main.cinemabookingrestapi.exception.notFound;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }
}