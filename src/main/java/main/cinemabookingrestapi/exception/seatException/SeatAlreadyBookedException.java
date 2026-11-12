package main.cinemabookingrestapi.exception.seatException;

public class SeatAlreadyBookedException extends SeatException{
    public SeatAlreadyBookedException() {
        super("given seat is already booked");
    }
}
