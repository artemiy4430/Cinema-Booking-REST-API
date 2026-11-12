package main.cinemabookingrestapi.exception.screeningException;

public abstract class ScreeningTimeException extends RuntimeException {
    public ScreeningTimeException(String message) {
        super(message);
    }
}
