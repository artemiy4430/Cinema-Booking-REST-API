package main.cinemabookingrestapi.exception.seatException;

public class DuplicateSeatException extends SeatException {
    public DuplicateSeatException(String message) {
        super("the selected seats are duplicate");
    }
}
