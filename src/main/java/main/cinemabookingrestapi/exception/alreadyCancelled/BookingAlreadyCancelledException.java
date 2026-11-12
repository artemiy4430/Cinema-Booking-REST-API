package main.cinemabookingrestapi.exception.alreadyCancelled;

import main.cinemabookingrestapi.entity.Booking;

public class BookingAlreadyCancelledException extends RuntimeException {
    public BookingAlreadyCancelledException(Booking status) {
        super("Booking is already cancelled (status: " + status  + " )");
    }
}
