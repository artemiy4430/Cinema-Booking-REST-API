package main.cinemabookingrestapi.exception.seatException;

public abstract class SeatException extends RuntimeException {
    public SeatException(String message) {
        super(message);
    }
}
