package main.cinemabookingrestapi.exception.isPresentException;

public class ScreeningIsPresentInBookingException extends IsPresentException {
    public ScreeningIsPresentInBookingException(Long id) {
        super("Screening: " + id + " is present in booking");
    }
}
