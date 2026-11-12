package main.cinemabookingrestapi.exception.seatException;

public class InvalidSeatException extends SeatException {
    public InvalidSeatException(String seat) {
        super("invalid seat selected: " + seat);
    }

    public InvalidSeatException() {
        super("invalid seat selected: ");
    }
}
